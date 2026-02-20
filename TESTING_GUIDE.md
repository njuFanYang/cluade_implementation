# Phase 5 Testing Guide

## Prerequisites

1. **Backend Running**: `http://localhost:8080`
2. **Frontend Running**: `http://localhost:5173`
3. **Test Admin Account**: Create an admin user or ensure one exists in database

## Creating Test Admin User (If Needed)

### Option 1: Via Database (Recommended)
```sql
-- Find an existing user
SELECT * FROM users WHERE username = 'admin';

-- Add ROLE_ADMIN to user (replace user_id with actual ID)
INSERT INTO user_roles (user_id, role_id)
SELECT 1, id FROM roles WHERE name = 'ROLE_ADMIN';
```

### Option 2: Via Application
1. Register a new user: `testadmin` / `admin@test.com` / `password123`
2. Manually add ROLE_ADMIN via database query above
3. Login with the admin account

## Quick Test Sequence (5 minutes)

### 1. Test Admin Dashboard
```
1. Login as admin user
2. Navigate to http://localhost:5173/admin
3. Verify all statistics cards show numbers
4. Check "Today's Activity" section
5. Click "Manage Users" button → should navigate to /admin/users
6. Click "Approve Music" button → should navigate to /admin/audit
```

**Expected Result**: Dashboard loads with statistics, no console errors

### 2. Test User Management
```
1. Navigate to /admin/users
2. Search for a username
3. Click "Details" on any user
4. Try to enable/disable a non-admin user
5. Add MUSICIAN role to a user
6. Remove MUSICIAN role from a user
```

**Expected Result**: All operations succeed, admin users cannot be disabled/deleted

### 3. Test Music Approval
```
1. Navigate to /admin/audit
2. Switch between PENDING, APPROVED, REJECTED tabs
3. If there's pending music:
   - Click "Approve" → music should move to APPROVED tab
4. Upload new music as musician:
   - Music should appear in PENDING tab
   - Approve it
   - Verify it appears in public music browse
```

**Expected Result**: Music approval workflow works correctly

### 4. Test Security
```
1. Logout admin
2. Try to access /admin → should redirect to login
3. Login as regular user
4. Try to access /admin → should show "Access denied"
5. Open browser console → try to call API:
   fetch('http://localhost:8080/api/admin/dashboard', {
     headers: { 'Authorization': 'Bearer YOUR_TOKEN' }
   })
   → Should get 403 Forbidden
```

**Expected Result**: Non-admin users cannot access admin features

## Detailed Test Cases

### Backend API Tests (Use Postman/Swagger)

#### 1. Dashboard Statistics
```
GET /api/admin/dashboard
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "message": "Success",
  "data": {
    "totalUsers": 10,
    "activeUsers": 8,
    "totalMusic": 50,
    "pendingMusic": 5,
    "approvedMusic": 45,
    "totalPlaylists": 20,
    "totalComments": 100,
    "todayUploads": 2,
    "todayRegistrations": 1
  }
}
```

#### 2. Get All Users
```
GET /api/admin/users?page=0&size=20
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "roles": ["ROLE_USER"],
        "enabled": true,
        "locked": false,
        "musicCount": 5,
        "followersCount": 10
      }
    ],
    "totalElements": 10,
    "totalPages": 1
  }
}
```

#### 3. Update User Status
```
PUT /api/admin/users/2/status
Headers: Authorization: Bearer {admin_token}
Body:
{
  "enabled": false
}

Expected Response:
{
  "code": 200,
  "message": "User status updated successfully"
}

Test Edge Case (Try to disable admin user):
PUT /api/admin/users/1/status  (where user 1 is admin)
Body: { "enabled": false }

Expected Response:
{
  "code": 5005,
  "message": "Cannot disable admin user"
}
```

#### 4. Manage User Role
```
POST /api/admin/users/roles
Headers: Authorization: Bearer {admin_token}
Body:
{
  "userId": 2,
  "roleName": "ROLE_MUSICIAN",
  "action": "ADD"
}

Expected Response:
{
  "code": 200,
  "message": "User role updated successfully"
}
```

#### 5. Delete User
```
DELETE /api/admin/users/3
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "message": "User deleted successfully"
}

Test Edge Case (Try to delete admin):
DELETE /api/admin/users/1

Expected Response:
{
  "code": 5006,
  "message": "Cannot delete admin user"
}
```

#### 6. Get Pending Music
```
GET /api/admin/music/pending?page=0&size=20
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": 10,
        "title": "Test Song",
        "artist": "Test Artist",
        "status": "PENDING",
        "uploaderName": "testmusician"
      }
    ],
    "totalElements": 5
  }
}
```

#### 7. Approve Music
```
PUT /api/admin/music/10/approve
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "message": "Music approved successfully"
}

Verify:
GET /api/music/10 → status should be "APPROVED"
```

#### 8. Reject Music
```
PUT /api/admin/music/11/reject?reason=Low quality
Headers: Authorization: Bearer {admin_token}

Expected Response:
{
  "code": 200,
  "message": "Music rejected successfully"
}

Verify:
GET /api/music/11 → status should be "REJECTED"
```

### Frontend UI Tests

#### Test 1: Dashboard UI
```
Steps:
1. Navigate to /admin
2. Wait for statistics to load
3. Verify all 4 stat cards display numbers
4. Verify "Today's Activity" shows upload and registration counts
5. Verify "Quick Actions" buttons are clickable
6. Click "Manage Users" → should navigate to /admin/users
7. Go back to /admin
8. Click "Approve Music" → should navigate to /admin/audit

Pass Criteria:
- No console errors
- All numbers display correctly
- Navigation works
- Responsive on mobile (test by resizing browser)
```

#### Test 2: User Management UI
```
Steps:
1. Navigate to /admin/users
2. Search for "test" in search box
3. Verify table filters results
4. Change status filter to "Enabled"
5. Verify only enabled users show
6. Click "Details" on first user
7. Verify dialog shows user information
8. Close dialog
9. Click "Disable" on a non-admin user
10. Confirm action
11. Verify user status changes to "Disabled"
12. Try to click "Disable" on admin user
13. Verify button is disabled

Pass Criteria:
- Search and filters work
- User details dialog opens
- Enable/Disable works for non-admins
- Admin users cannot be disabled/deleted
- Pagination works
```

#### Test 3: Music Approval UI
```
Steps:
1. Navigate to /admin/audit
2. Click "PENDING" tab
3. Verify pending music list loads
4. Click "APPROVED" tab
5. Verify approved music list loads
6. Click "REJECTED" tab
7. Verify rejected music list loads
8. Go back to "PENDING" tab
9. Click "Approve" on first music
10. Confirm action
11. Verify music disappears from PENDING
12. Switch to "APPROVED" tab
13. Verify music appears in approved list
14. Go back to "PENDING" tab
15. Click "Reject" on a music
16. Enter rejection reason
17. Click "Reject" in dialog
18. Verify music moves to "REJECTED" tab

Pass Criteria:
- All tabs load correctly
- Approve/Reject actions work
- Music moves between tabs correctly
- Cover images display
- Pagination works
```

#### Test 4: Role Management
```
Steps:
1. Navigate to /admin/users
2. Click "Details" on a regular user (not admin, not musician)
3. In the dialog, scroll to "Role Management" section
4. Click "Add Musician" button
5. Close dialog
6. Reload user list
7. Click "Details" on same user
8. Verify MUSICIAN tag is present
9. Click "Remove Musician" button
10. Confirm action
11. Verify MUSICIAN role removed

Pass Criteria:
- Add role succeeds
- Remove role succeeds
- Role changes persist after page reload
- Cannot modify admin user roles (buttons disabled)
```

#### Test 5: Security & Access Control
```
Steps:
1. Login as admin
2. Navigate to /admin → Success
3. Logout
4. Try to navigate to /admin → Redirects to /login
5. Login as regular user (no admin role)
6. Try to navigate to /admin → Shows "Access denied" and redirects to /
7. Open browser dev tools (F12)
8. Go to Console tab
9. Try to manually call admin API:

   const token = localStorage.getItem('token');
   fetch('http://localhost:8080/api/admin/dashboard', {
     headers: { 'Authorization': `Bearer ${token}` }
   }).then(r => r.json()).then(console.log)

10. Should see 403 Forbidden error

Pass Criteria:
- Admin routes protected
- Non-admin users redirected
- API returns 403 for non-admin
- Clear error messages displayed
```

## End-to-End Workflow Test

### Complete Music Approval Workflow
```
Scenario: A musician uploads music, admin approves it, and it becomes public

Steps:
1. Register new musician user: "testmusician" / "musician@test.com" / "password123"
2. Login as admin
3. Navigate to /admin/users
4. Find "testmusician"
5. Click "Details"
6. Add MUSICIAN role
7. Logout admin

8. Login as "testmusician"
9. Navigate to /music/upload
10. Upload a music file (any MP3):
    - Title: "Test Approval Song"
    - Artist: "Test Artist"
    - Select a genre
    - Click Upload
11. Wait for upload to complete
12. Navigate to /music/my
13. Verify music status is "PENDING"
14. Logout musician

15. Login as admin
16. Navigate to /admin
17. Verify "Pending Music" count increased by 1
18. Click "Approve Music" button
19. Verify "Test Approval Song" is in the PENDING tab
20. Click "Approve" button
21. Confirm approval
22. Verify music moved to APPROVED tab
23. Logout admin

24. Login as regular user (or no login)
25. Navigate to /music
26. Search for "Test Approval Song"
27. Verify music appears in browse results
28. Click on the music
29. Verify music detail page loads
30. Try to play the music
31. Verify music plays successfully

Pass Criteria:
✅ Music uploads as PENDING
✅ Admin sees pending count
✅ Admin can approve music
✅ Approved music becomes public
✅ All users can browse and play approved music
```

## Performance Tests

### Load Test Dashboard
```
Steps:
1. Open browser dev tools
2. Go to Network tab
3. Navigate to /admin
4. Check API call timing:
   - /api/admin/dashboard should complete in < 500ms
5. Refresh page 5 times
6. Verify consistent load times
```

### Load Test User List
```
Steps:
1. Navigate to /admin/users
2. Set page size to 100
3. Check table renders in < 1 second
4. Try searching for common terms
5. Verify search results return quickly (< 500ms)
```

## Common Issues & Solutions

### Issue 1: 403 Forbidden on Admin Endpoints
**Cause**: User doesn't have ROLE_ADMIN
**Solution**: Add ROLE_ADMIN to user in database

### Issue 2: Dashboard Shows 0 for All Stats
**Cause**: No data in database
**Solution**: Use TestMockData.vue to generate test data

### Issue 3: Music Upload Still Auto-Approves
**Cause**: Code change not deployed
**Solution**: Rebuild backend, restart server

### Issue 4: Cannot Disable Admin User
**Cause**: This is by design (security feature)
**Solution**: Expected behavior, not a bug

### Issue 5: Navigation Guard Not Working
**Cause**: Roles not stored in localStorage
**Solution**: Logout and login again to refresh userInfo

## Test Data Setup

Use the existing TestMockData page to generate test data:
```
1. Navigate to /test/mock-data
2. Click "Generate All Test Data"
3. Wait for completion
4. Now you have:
   - 10 test users
   - 50 test music tracks
   - 20 test playlists
   - 100 test comments
5. Manually add ROLE_ADMIN to one user
6. Start testing
```

## Success Metrics

After all tests pass, you should see:
- ✅ All API endpoints return 200 (or appropriate error codes)
- ✅ All UI pages load without console errors
- ✅ All CRUD operations work correctly
- ✅ Security restrictions enforced
- ✅ Music approval workflow functions end-to-end
- ✅ Responsive design works on mobile

## Reporting Bugs

If you find issues, report with:
1. **Environment**: Browser, OS, backend/frontend versions
2. **Steps to Reproduce**: Exact steps to trigger the bug
3. **Expected Result**: What should happen
4. **Actual Result**: What actually happened
5. **Console Errors**: Any errors in browser console or backend logs
6. **Screenshots**: If UI bug, include screenshots

## Next Steps After Testing

1. Fix any bugs found during testing
2. Deploy to staging environment
3. Perform user acceptance testing (UAT)
4. Gather feedback from admin users
5. Make improvements based on feedback
6. Deploy to production

---

**Happy Testing!** 🎉
