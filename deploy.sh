#!/bin/bash
# =============================================================================
# MusicShare 一键部署脚本
# 用法: ./deploy.sh [dev|prod|stop|test|status]
#
#   dev    开发模式（默认）：热重载，后台运行前后端
#   prod   生产模式：先打包再运行
#   stop   停止所有服务（含 Docker）
#   test   运行所有自动化测试（无需 Docker）
#   status 查看各服务运行状态
# =============================================================================

set -euo pipefail

# ── 颜色 ──────────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'
YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

# ── 路径 ──────────────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
LOG_DIR="$SCRIPT_DIR/.deploy/logs"
PID_DIR="$SCRIPT_DIR/.deploy/pids"
mkdir -p "$LOG_DIR" "$PID_DIR"

BACKEND_LOG="$LOG_DIR/backend.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"
BACKEND_PID="$PID_DIR/backend.pid"
FRONTEND_PID="$PID_DIR/frontend.pid"

# ── 日志函数 ──────────────────────────────────────────────────────────────────
log_info()  { echo -e "${GREEN}[✓]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
log_error() { echo -e "${RED}[✗]${NC} $1" >&2; }
log_step()  { echo -e "\n${BLUE}━━━ $1 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }

# ── 自动检测 Docker 路径（Windows 兼容）──────────────────────────────────────
setup_docker_path() {
    if command -v docker &>/dev/null; then
        return 0
    fi
    # Windows Docker Desktop 常见安装路径
    local docker_paths=(
        "/c/Program Files/Docker/Docker/resources/bin"
        "/c/ProgramData/DockerDesktop/version-bin"
        "$HOME/.docker/bin"
    )
    for p in "${docker_paths[@]}"; do
        if [ -f "$p/docker" ] || [ -f "$p/docker.exe" ]; then
            export PATH="$p:$PATH"
            log_info "Docker 路径已添加: $p"
            return 0
        fi
    done
    return 1
}

# ── 检查端口是否开放 ──────────────────────────────────────────────────────────
port_open() {
    (echo >/dev/tcp/"$1"/"$2") >/dev/null 2>&1
}

# ── 等待端口开放（带超时）────────────────────────────────────────────────────
wait_for_port() {
    local host=$1 port=$2 name=$3 timeout=${4:-60}
    local elapsed=0
    printf "    等待 %-22s" "$name..."
    while ! port_open "$host" "$port"; do
        sleep 2; elapsed=$((elapsed + 2))
        printf "."
        if [ $elapsed -ge $timeout ]; then
            echo ""
            log_error "$name 在 ${timeout}s 内未就绪，请检查 Docker 服务"
            return 1
        fi
    done
    echo " 就绪 ✓"
}

# ── 等待 HTTP 接口响应 ────────────────────────────────────────────────────────
wait_for_http() {
    local url=$1 name=$2 timeout=${3:-120}
    local elapsed=0
    printf "    等待 %-22s" "$name..."
    while ! curl -sf "$url" >/dev/null 2>&1; do
        sleep 3; elapsed=$((elapsed + 3))
        printf "."
        if [ $elapsed -ge $timeout ]; then
            echo ""
            log_error "$name 在 ${timeout}s 内未响应"
            log_warn "查看日志: tail -f $BACKEND_LOG"
            return 1
        fi
    done
    echo " 就绪 ✓"
}

# ── 检查前置依赖 ──────────────────────────────────────────────────────────────
check_prereqs() {
    log_step "检查前置依赖"
    local failed=0
    for tool in java mvn node; do
        if command -v "$tool" &>/dev/null; then
            local ver; ver=$("$tool" --version 2>&1 | head -1)
            log_info "$tool: $ver"
        else
            log_error "$tool 未安装或不在 PATH 中"
            failed=1
        fi
    done
    # Docker 单独检查（带路径检测）
    if setup_docker_path && command -v docker &>/dev/null; then
        local ver; ver=$(docker --version 2>&1 | head -1)
        log_info "docker: $ver"
    else
        log_error "docker 未找到，请确认 Docker Desktop 已启动"
        failed=1
    fi
    [ $failed -eq 0 ] || { log_error "请安装缺失工具后重试"; exit 1; }
}

# ── 复制种子音乐文件 ───────────────────────────────────────────────────────────
# 将 test/music/ 里的 MP3 复制到 backend/music/seed/（后端运行时读取该路径）
copy_seed_music() {
    local src="$SCRIPT_DIR/test/music"
    local dst="$BACKEND_DIR/music/seed"
    if [ ! -d "$src" ]; then
        log_warn "未找到 test/music/ 目录，跳过种子音乐复制"
        return 0
    fi
    mkdir -p "$dst"
    # 按固定名称复制，与 SQL 中的 file_path 对应
    local -A map=(
        ["周杰伦 - 晴天(1).mp3"]="qingtian.mp3"
        ["周杰伦 - 花海(1).mp3"]="huahai.mp3"
        ["曹雨航,朝歌夜弦+-+江湖之间.mp3"]="jianghu.mp3"
        ["王大毛+-+去年夏天.mp3"]="qunian_xiatian.mp3"
    )
    local copied=0
    for orig in "${!map[@]}"; do
        local target="${map[$orig]}"
        if [ -f "$src/$orig" ] && [ ! -f "$dst/$target" ]; then
            cp "$src/$orig" "$dst/$target"
            log_info "已复制: $orig → music/seed/$target"
            copied=$((copied + 1))
        fi
    done
    [ $copied -gt 0 ] && log_info "种子音乐复制完成（共 $copied 个文件）" \
                      || log_info "种子音乐已存在，无需重复复制"
}

# ── Docker 服务 ───────────────────────────────────────────────────────────────
start_docker() {
    log_step "启动 Docker 基础服务（MySQL + Redis）"
    cd "$SCRIPT_DIR"
    setup_docker_path
    docker compose up -d
    wait_for_port localhost 3306 "MySQL:3306"  90
    wait_for_port localhost 6379 "Redis:6379"  30
    log_info "Docker 服务已就绪"
}

# ── 后端（开发模式）──────────────────────────────────────────────────────────
start_backend_dev() {
    log_step "启动后端（开发模式 - spring-boot:run）"
    # 终止旧进程
    if [ -f "$BACKEND_PID" ]; then
        kill "$(cat "$BACKEND_PID")" 2>/dev/null || true
        rm -f "$BACKEND_PID"
    fi
    cd "$BACKEND_DIR"
    > "$BACKEND_LOG"
    mvn spring-boot:run >> "$BACKEND_LOG" 2>&1 &
    echo $! > "$BACKEND_PID"
    log_info "后端进程已启动 (PID: $!)"
    log_warn "首次运行需下载依赖并编译，约需 2-4 分钟..."
    wait_for_http "http://localhost:8081/api/health/ping" "后端 API:8081" 240
}

# ── 后端（生产模式）──────────────────────────────────────────────────────────
start_backend_prod() {
    log_step "构建并启动后端（生产模式）"
    cd "$BACKEND_DIR"
    log_info "执行 mvn clean package（跳过测试）..."
    mvn clean package -DskipTests -q
    local jar
    jar=$(ls target/musicshare-backend-*.jar 2>/dev/null | head -1)
    [ -n "$jar" ] || { log_error "找不到构建产物 jar 文件"; exit 1; }
    if [ -f "$BACKEND_PID" ]; then
        kill "$(cat "$BACKEND_PID")" 2>/dev/null || true
        rm -f "$BACKEND_PID"
    fi
    > "$BACKEND_LOG"
    java -jar "$jar" --spring.profiles.active=prod >> "$BACKEND_LOG" 2>&1 &
    echo $! > "$BACKEND_PID"
    log_info "后端进程已启动 (PID: $!)"
    wait_for_http "http://localhost:8081/api/health/ping" "后端 API:8081" 120
}

# ── 前端（开发模式）──────────────────────────────────────────────────────────
start_frontend_dev() {
    log_step "启动前端（开发模式 - vite dev）"
    if [ -f "$FRONTEND_PID" ]; then
        kill "$(cat "$FRONTEND_PID")" 2>/dev/null || true
        rm -f "$FRONTEND_PID"
    fi
    cd "$FRONTEND_DIR"
    if [ ! -d "node_modules" ]; then
        log_info "安装 npm 依赖..."
        npm install --silent
    fi
    > "$FRONTEND_LOG"
    npm run dev >> "$FRONTEND_LOG" 2>&1 &
    echo $! > "$FRONTEND_PID"
    log_info "前端进程已启动 (PID: $!)"
    wait_for_http "http://localhost:5173" "前端:5173" 60
}

# ── 前端（生产模式）──────────────────────────────────────────────────────────
build_frontend_prod() {
    log_step "构建前端（生产模式）"
    cd "$FRONTEND_DIR"
    if [ ! -d "node_modules" ]; then
        log_info "安装 npm 依赖..."
        npm install --silent
    fi
    npm run build
    log_info "前端构建完成，产物位于 frontend/dist/"
    log_warn "请将 dist/ 部署到 Nginx 或其他静态服务器"
}

# ── 停止所有服务 ──────────────────────────────────────────────────────────────
stop_all() {
    log_step "停止所有服务"
    # 停止前端进程
    if [ -f "$FRONTEND_PID" ]; then
        local pid; pid=$(cat "$FRONTEND_PID")
        if kill "$pid" 2>/dev/null; then
            log_info "前端进程 (PID: $pid) 已停止"
        fi
        rm -f "$FRONTEND_PID"
    fi
    # 停止后端进程
    if [ -f "$BACKEND_PID" ]; then
        local pid; pid=$(cat "$BACKEND_PID")
        if kill "$pid" 2>/dev/null; then
            log_info "后端进程 (PID: $pid) 已停止"
        fi
        rm -f "$BACKEND_PID"
    fi
    # 停止 Docker 服务
    cd "$SCRIPT_DIR"
    if setup_docker_path && command -v docker &>/dev/null; then
        docker compose down
        log_info "Docker 服务已停止"
    else
        log_warn "Docker 不可用，跳过 docker compose down"
    fi
    log_info "所有服务已停止 ✓"
}

# ── 运行测试 ──────────────────────────────────────────────────────────────────
run_tests() {
    log_step "后端单元测试（JUnit + Mockito）"
    cd "$BACKEND_DIR"
    mvn test
    local backend_result=$?

    echo ""
    log_step "前端单元测试（Vitest）"
    cd "$FRONTEND_DIR"
    npm test
    local frontend_result=$?

    echo ""
    if [ $backend_result -eq 0 ] && [ $frontend_result -eq 0 ]; then
        log_info "所有测试通过 ✓"
    else
        log_error "部分测试失败，请查看上方输出"
        exit 1
    fi
}

# ── 状态查看 ──────────────────────────────────────────────────────────────────
show_status() {
    log_step "服务状态"

    # Docker
    echo -e "\n  ${BLUE}Docker 服务:${NC}"
    if setup_docker_path && command -v docker &>/dev/null; then
        cd "$SCRIPT_DIR"
        docker compose ps 2>/dev/null || echo "    （Docker Compose 未运行）"
    else
        echo "    （Docker 不可用）"
    fi

    # 后端
    echo -e "\n  ${BLUE}后端进程:${NC}"
    if [ -f "$BACKEND_PID" ] && kill -0 "$(cat "$BACKEND_PID")" 2>/dev/null; then
        log_info "运行中 (PID: $(cat "$BACKEND_PID")) → http://localhost:8081"
        local health
        health=$(curl -sf http://localhost:8081/api/health/ping 2>/dev/null | \
            python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('message',''))" 2>/dev/null || echo "无法解析")
        echo "    健康检查: $health"
    else
        log_warn "未运行"
    fi

    # 前端
    echo -e "\n  ${BLUE}前端进程:${NC}"
    if [ -f "$FRONTEND_PID" ] && kill -0 "$(cat "$FRONTEND_PID")" 2>/dev/null; then
        log_info "运行中 (PID: $(cat "$FRONTEND_PID")) → http://localhost:5173"
    else
        log_warn "未运行"
    fi
    echo ""
}

# ── 部署完成提示 ──────────────────────────────────────────────────────────────
print_summary() {
    local mode=$1
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║       🎵  MusicShare 部署完成！              ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════╝${NC}"
    echo ""
    [ "$mode" = "dev" ] && echo -e "  前端应用   →  ${BLUE}http://localhost:5173${NC}"
    echo -e "  后端 API   →  ${BLUE}http://localhost:8081${NC}"
    echo -e "  Swagger    →  ${BLUE}http://localhost:8081/swagger-ui.html${NC}"
    echo -e "  phpMyAdmin →  ${BLUE}http://localhost:8080${NC}"
    echo ""
    echo -e "  日志:"
    echo -e "    后端  →  tail -f $BACKEND_LOG"
    [ "$mode" = "dev" ] && echo -e "    前端  →  tail -f $FRONTEND_LOG"
    echo ""
    echo -e "  停止服务:  ${YELLOW}./deploy.sh stop${NC}"
    echo -e "  服务状态:  ${YELLOW}./deploy.sh status${NC}"
    echo ""
}

# ── 主流程 ────────────────────────────────────────────────────────────────────
MODE=${1:-dev}

case "$MODE" in
    stop)   stop_all   ;;
    test)   run_tests  ;;
    status) show_status ;;
    dev)
        check_prereqs
        copy_seed_music
        start_docker
        start_backend_dev
        start_frontend_dev
        print_summary dev
        ;;
    prod)
        check_prereqs
        copy_seed_music
        start_docker
        start_backend_prod
        build_frontend_prod
        print_summary prod
        ;;
    *)
        cat <<EOF
用法: $0 [dev|prod|stop|test|status]

  dev     开发模式（默认），热重载
  prod    生产模式，先构建再运行
  stop    停止所有服务（含 Docker）
  test    运行后端 + 前端自动化测试
  status  查看各服务运行状态
EOF
        exit 1
        ;;
esac
