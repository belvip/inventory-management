# User Management APIs

## Base Configuration
```json
{
  "base_url": "http://localhost:8282",
  "api_prefix": "/api/v1",
  "authentication": "JWT tokens + OAuth2 (Google/GitHub)",
  "documentation": "Available at /swagger-ui/"
}
```

## User APIs

```json
{
  "user_apis": [
    {
      "method": "POST",
      "endpoint": "/api/v1/users/create",
      "summary": "Create a new user",
      "description": "Creates a new user with default role ROLE_USER. Fails if email or username already exist.",
      "access_roles": ["ADMIN"],
      "request_body": "UserRequestDto",
      "responses": {
        "201": "User created successfully",
        "400": "Validation error",
        "409": "Conflict (Email or Username already exists)"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update/{id}",
      "summary": "Update a user",
      "description": "Allows an ADMIN to update an existing user's details",
      "access_roles": ["ADMIN"],
      "path_parameters": {
        "id": "User ID (Long)"
      },
      "request_body": "UpdateUserRequestDto",
      "responses": {
        "200": "User updated successfully",
        "400": "Validation failed",
        "404": "User not found"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update/role",
      "summary": "Update a user's role",
      "description": "Allows an ADMIN to update the role of an existing user",
      "access_roles": ["ADMIN"],
      "request_body": "UpdateUserRoleRequest",
      "responses": {
        "200": "User role updated successfully",
        "404": "User not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/users/{id}",
      "summary": "Get a user by ID",
      "description": "Fetches a user by their unique ID",
      "access_roles": ["ADMIN"],
      "path_parameters": {
        "id": "User ID (Long)"
      },
      "responses": {
        "200": "User found",
        "404": "User not found"
      }
    },
    {
      "method": "DELETE",
      "endpoint": "/api/v1/users/{id}",
      "summary": "Delete a user",
      "description": "Allows an ADMIN to delete a user by ID",
      "access_roles": ["ADMIN"],
      "path_parameters": {
        "id": "User ID (Long)"
      },
      "responses": {
        "200": "User deleted successfully",
        "404": "User not found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/users/search",
      "summary": "Search users by keyword",
      "description": "Allows ADMIN to search for users by username, email, or other fields",
      "access_roles": ["ADMIN"],
      "query_parameters": {
        "keyword": "Search keyword (String)"
      },
      "responses": {
        "200": "Users found"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/users/all",
      "summary": "Get all users",
      "description": "Fetches all users in the system",
      "access_roles": ["ADMIN"],
      "responses": {
        "200": "Users retrieved successfully"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/{userId}/image",
      "summary": "Update user image",
      "description": "Allows an ADMIN to update a user's profile image",
      "access_roles": ["ADMIN"],
      "path_parameters": {
        "userId": "User ID (Long)"
      },
      "request_body": "MultipartFile (image)",
      "content_type": "multipart/form-data",
      "responses": {
        "200": "Image updated successfully",
        "404": "User not found",
        "500": "Internal server error"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update-password",
      "summary": "Update own password",
      "description": "Allows an authenticated user to update their own password",
      "access_roles": ["Authenticated users"],
      "request_body": "UpdatePasswordRequest",
      "responses": {
        "200": "Password updated successfully",
        "400": "Invalid request or validation failed",
        "401": "Unauthorized"
      }
    },
    {
      "method": "GET",
      "endpoint": "/api/v1/users/roles",
      "summary": "Get all roles",
      "description": "Fetches all available roles in the system",
      "access_roles": ["ADMIN"],
      "responses": {
        "200": "Roles retrieved successfully",
        "403": "Access denied"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update-lock-status",
      "summary": "Update account lock status",
      "description": "Allows an ADMIN to lock or unlock a user account for security purposes",
      "access_roles": ["ADMIN"],
      "request_body": "UpdateAccountLockStatusRequest",
      "responses": {
        "200": "Account lock status updated successfully",
        "400": "Validation error",
        "404": "User not found",
        "403": "Access denied - ADMIN role required"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update-credentials-expiry-status",
      "summary": "Update credentials expiry status",
      "description": "Allows setting whether a user's credentials are expired or not",
      "access_roles": ["ADMIN"],
      "request_body": "UpdateCredentialsExpiryStatusRequest",
      "responses": {
        "200": "Credentials expiry status updated",
        "404": "User not found",
        "500": "Internal server error"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update-enabled-status",
      "summary": "Update account enabled status",
      "description": "Allows enabling or disabling a user account",
      "access_roles": ["ADMIN"],
      "request_body": "UpdateAccountEnabledStatusRequest",
      "responses": {
        "200": "Account enabled status updated",
        "404": "User not found",
        "500": "Internal server error"
      }
    },
    {
      "method": "PUT",
      "endpoint": "/api/v1/users/update-expiry-status",
      "summary": "Update account expiry status",
      "description": "Allows setting whether a user account is expired or not",
      "access_roles": ["ADMIN"],
      "request_body": "UpdateAccountExpiryStatusRequest",
      "responses": {
        "200": "Account expiry status updated",
        "404": "User not found",
        "500": "Internal server error"
      }
    }
  ]
}
```

## Request/Response DTOs

### UserRequestDto
```json
{
  "userName": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "password": "string"
}
```

### UpdateUserRequestDto
```json
{
  "userName": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string"
}
```

### UpdateUserRoleRequest
```json
{
  "userId": "number",
  "roleName": "string"
}
```

### UpdatePasswordRequest
```json
{
  "password": "string"
}
```

### UpdateAccountLockStatusRequest
```json
{
  "userId": "number",
  "lock": "boolean"
}
```

### UpdateCredentialsExpiryStatusRequest
```json
{
  "userId": "number",
  "expire": "boolean"
}
```

### UpdateAccountEnabledStatusRequest
```json
{
  "userId": "number",
  "enabled": "boolean"
}
```

### UpdateAccountExpiryStatusRequest
```json
{
  "userId": "number",
  "expire": "boolean"
}
```

### UserResponseDto
```json
{
  "id": "number",
  "userName": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "role": {
    "id": "number",
    "roleName": "string"
  },
  "accountNonExpired": "boolean",
  "accountNonLocked": "boolean",
  "credentialsNonExpired": "boolean",
  "enabled": "boolean",
  "twoFactorEnabled": "boolean",
  "signUpMethod": "string",
  "createdDate": "string",
  "updatedDate": "string"
}
```

## Available Roles
- ROLE_ADMIN
- ROLE_MANAGER  
- ROLE_SALES
- ROLE_USER

## Authentication
All endpoints require JWT authentication except `/update-password` which requires any authenticated user. Most endpoints are restricted to ADMIN role only.

## Notes
- All user management operations (except password update) require ADMIN privileges
- The system supports OAuth2 authentication with Google and GitHub
- User images are stored using MinIO service
- Account status can be controlled through various lock/enable/expiry endpoints