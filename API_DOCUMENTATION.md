# API Documentation

This document provides an overview of the API endpoints available in the system. Each section corresponds to a controller and details the available routes, methods, parameters, and expected responses.

## Introduction

Welcome to the **Task Manager API**. This API allows developers to manage admins, teams, tasks, and assignments within the system. It is designed to be RESTful, using standard HTTP methods and response codes.

A REST API allows different systems to communicate over the internet using standard HTTP methods. Each request interacts with the API's resources, such as "admins" and "tasks".

### Base URL
All API requests should be made to the following base URL (Spring Boot's default settings): `http://localhost:8080` 

---

## Table of Contents
1. [AdminController](#admincontroller)
2. [AuthInfoController](#authinfocontroller)
3. [IsAssignedController](#isassignedcontroller)
4. [IsMemberOfController](#ismemberofcontroller)
5. [TaskController](#taskcontroller)
6. [TeamController](#teamcontroller)
7. [TeamMemberController](#teammembercontroller)
8. [NotificationController](#notificationcontroller)
9. [Path Variables](#path-variables)
10. [DTO References](#dto-references)
11. [Request and Response Examples](#request-and-response-examples)
12. [Best Practices](#best-practices)
13. [Error Codes](#error-codes)
14. [Example Error Response](#example-error-response)

---

## **AdminController**

**Base URL:** `/api/admin`

### **Endpoints**

- **Create Admin:** `POST /` 
    - **Description:** Creates a new admin in the system.
    - **Request Body:**
    ```json
    {
        "name": "Admin Name",
        "email": "admin@example.com",
        "password": "securepassword"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 1,
        "userName": "Admin Name",
        "userEmail": "admin@example.com"
    }
    ```

- **Delete Admin:** `DELETE /{adminId}`
    - **Parameters:**
        - `adminId` (integer, required): The unique ID of the admin to be deleted.  
    - **Description:** Removes an admin from the system permanently.

- **Modify Admin Name:** `PUT /{adminId}/update-name`
    - **Request Body:**
    ```json
    {
        "newName": "Updated Admin Name"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 1,
        "userName": "Updated Admin Name",
        "userEmail": "admin@example.com"
    }
    ```
    - **Description:** Updates the admin's **name** field in the database.

- **Modify Admin Email:** `PUT /{adminId}/update-email`
    - **Request Body:**
    ```json
    {
        "newEmail": "updated.admin@example.com"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 1,
        "userName": "Admin Name",
        "userEmail": "updated.admin@example.com"
    }
    ``` 
    - **Description:** Updates the admin's **email** field in the database.

- **Create Team Member:** `POST /team-member`
    - **Request Body:**
    ```json
    {
        "name": "Team Member",
        "email": "teammember@example.com",
        "password": "securepassword"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 2,
        "userName": "Team Member",
        "userEmail": "teammember@example.com"
    }
    ```
    - **Description:** Adds a new team member to the system.

- **Modify Team Member Name:** `PUT /team-member/{teamMemberId}/update-name`
    - **Request Body:**
    ```json
    {
        "newName": "Updated Team Member"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 2,
        "userName": "Updated Team Member",
        "userEmail": "teammember@example.com"
    }
    ```
    - **Description:** Updates the **name** field of the specified team member in the database.

- **Modify Team Member Email:** `PUT /team-member/{teamMemberId}/update-email`
    - **Request Body:**
    ```json
    {
        "newEmail": "updated.tm@example.com"
    }
    ```
    - **Response Body:**
    ```json
    {
        "accountId": 2,
        "userName": "Team Member",
        "userEmail": "updated.tm@example.com"
    }
    ```
    - **Description:** Updates the **email** field of the specified team member in the database.

- **Delete Team Member:** `DELETE /team-member/{teamMemberId}`
    - **Parameters:**
        - `teamMemberId` (integer, required): The unique ID of the team member to be deleted.  
    - **Description:** Removes a team member from the system permanently.

- **Assign Team Member to a Team:** `POST /team-member/{teamMemberId}/assign-to-team/{teamId}`
    - **Parameters:**  
        - `teamMemberId` (integer, required): The ID of the team member to assign.  
        - `teamId` (integer, required): The ID of the team to assign them to.  
    - **Description:** Assigns a team member to a specified team.

- **Promote Team Member to Admin:** `POST /team-member/{teamMemberId}/promote`
    - **Parameters:**  
        - `teamMemberId` (integer, required): The ID of the team member to be promoted.  
    - **Description:** Upgrades a team member to an admin role, granting them full administrative privileges.

- **Lock a Task:** `PUT /tasks/{taskId}/lock`
    - **Parameters:**  
        - `taskId` (integer, required): The ID of the task to lock.  
    - **Description:** Locks a task to prevent any modifications. Once locked, updates and deletions are restricted.

- **Unlock a Task:** `PUT /tasks/{taskId}/unlock`
    - **Parameters:**  
        - `taskId` (integer, required): The ID of the task to unlock.  
    - **Description:** Unlocks a task, allowing updates and modifications.

- **Get All Admins:** `GET /admins`
    - **Response Body:** 
    ```json
    [
        {
            "accountId": 1,
            "userName": "Admin Name",
            "userEmail": "admin@example.com"
        },
        {
            "accountId": 2,
            "userName": "Admin 2",
            "userEmail": "admin_2@example.com"
        }
    ]
    ```
    - **Description:** Returns a list of every admin in the database.

 **Get All Team Members:** `GET /team-members`
    - **Response Body:** 
    ```json
    [
        {
            "accountId": 2,
            "userName": "Team Member",
            "userEmail": "teammember@example.com"
        },
        {
            "accountId": 3,
            "userName": "Team Member3",
            "userEmail": "teammember3@example.com"
        }
    ]
    ```
    - **Description:** Returns a list of every team member in the database.

**Get All Teams:** `GET /all-teams`
    - **Response Body:**
    ```json
    [
        {
            "teamId": 1,
            "teamName": "Development Team"
        },
        {
            "teamId": 2,
            "teamName": "Marketing Team"
        }
    ]
    ```
    - **Description:** Returns a list of every team in the database.

**Get Admin by ID** `GET /{adminId}`
    - **Parameters:** 
        - `adminId` (integer, required): The ID of the admin being retrieved.
    - **Response Body:**
    ```json
    {
        "accountId": 1,
        "userName": "Admin Name",
        "userEmail": "admin@example.com"
    }
    ```
    - **Description:** Returns the id, name, and email of the requested admin.

**Get Team Member by ID** `GET /team-member/{teamMemberId}`
    - **Parameters:** 
        - `teamMemberId` (integer, required): The ID of the team member being retrieved.
    - **Response Body:**
    ```json
    {
        "accountId": 1,
        "userName": "Team Member Name",
        "userEmail": "teamMember@example.com"
    }
    ```
    - **Description:** Returns the id, name, and email of the requested team member.

---

## **AuthInfoController**

**Base URL:** `/api/auth-info`

### Endpoints
- **Login:** `POST /login`
    - **Description:** Logs a user in.
    - **Request Body:** 
    ```json
    {
        "teamMemberId": 1, 
        "password": "password"
    }
    ```
    - **Response Body:**
    ```json
    {
        "id": 1,
        "name": "Test User",
        "isAdmin": true
    }
    ```

- **IsAdmin:** `POST /is-admin`
    - **Description:** Checks if a user is an admin.
    - **Request Body:**
    ```json
    {
        "teamMemberId": 1
    }
    ```
    - **Response Body:***
    ```json
    {
        "isAdmin": true
    }   
    ```
---

## **IsAssignedController**

**Base URL:** `/api/assignments`

### Endpoints
- **Assign a Team Member to a Task:** `POST /{teamMemberId}/task/{taskId}`
    - **Description:** Assigns a team member to a task.

- **Unassign a Team Member from a Task:** `DELETE /{teamMemberId}/task/{taskId}`
    - **Description:** Unassigns a team member from a task.

- **Check if Assigned:** `GET /{teamMemberId}/task/{taskId}`
    - **Description:** Returns a boolean of whether a team member is currently assigned to a task.

---

## **IsMemberOfController**

**Base URL:** `/api/memberships`

### Endpoints
- **Add a Member to a Team:** `POST /{teamMemberId}/team/{teamId}`
    - **Description:** Adds a team member to a team.

- **Remove a Member from a Team:** `DELETE /{teamMemberId}/team/{teamId}`
    - **Description:** Removes a team member from a team.

- **Check if a Member is assigned to a Team:** `GET /{teamMemberId}/team/{teamId}`
    - **Description:** Returns a boolean of whether a team member is assigned to a team. 

---

## **TaskController**

**Base URL:** `/api/tasks`

### Endpoints
- No implemented endpoints.

---

## **TeamController**

**Base URL:** `/api/teams`

### Endpoints
- **Create a Team:** `POST`
    - **Request Body:**
    ```json
    {
        "teamId": 1, 
        "teamName": "Development Team",
        "teamLeadId": 1001
    }
    ```
    - **Response Body:**
    ```json
    {
        "teamId": 1,
        "teamName": "Development Team",
        "teamLeadId": 1001
    }
    ```
    - **Description:** Creates a team in the database.

- **Delete a Team:** `DELETE /{teamId}`
    - **Description:** Deletes a team from the database.
    
- **Change Team Lead:** `PUT /{teamId}/change-lead`
    - **Request Body:**
    ```json
    {
        "teamId": 1,
        "teamName": "Updated Team Name",
        "teamLeadId": 1002
    }
    ```
    - **Response Body:**
    ```json
    {
        "teamId": 1,
        "teamName": "Updated Team Name",
        "teamLeadId": 1002
    }
    ```
    - **Description:** Updates the team lead for a specified team by assigning a different team member.

- **Get Team Members:** `GET /{teamId}/members`
    - **Response Body:**
    ```json
    [
        {
            "accountId": 1,
            "userName": "John Doe",
            "userEmail": "john@example.com"
        },
        {
            "accountId": 2,
            "userName": "Jane Smith",
            "userEmail": "jane@example.com"
        }
    ]
    ```
    - **Description:** Returns a list of every team member in a team. Each list item contains the team member's ID, name, and email.

---

## **TeamMemberController**

**Base URL:** `/api/tasks`

### Endpoints
- **Create a Task:** `POST`
    - **Request Body:**
    ```json
    {
        "title": "Task Title",
        "description": "Task Description",
        "isLocked": false,
        "status": "To-Do",
        "dueDate": "2025-03-01",
        "teamId": 3
    }
    ```
    - **Response Body:**
    ```json
    {
        "taskId": 4,
        "title": "Task Title",
        "description": "Task Description",
        "isLocked": false,
        "status": "To-Do",
        "dueDate": "2025-03-01",
        "teamId": 3
    }
    ```
    - **Description:** Creates a task in the database.

- **Delete a Task:** `DELETE /{taskId}`
    - **Description:** Deletes a task from the database.

- **Edit a Task:** `PUT /{taskId}`
    - **Request Body:** (TaskDTO, JSON)
    ```json
    {
        "title": "Updated Title",
        "description": "Updated Description",
        "isLocked": false,
        "status": "In Progress",
        "dueDate": "2025-04-01"
    }
    ```
    - **Response Body:**
    ```json
    {
        "taskId": 4,
        "title": "Updated Title",
        "description": "Updated Description",
        "isLocked": false,
        "status": "In Progress",
        "dueDate": "2025-04-01",
        "teamId": 3
    }
    ```
    - **Description:** Updates the details of a task.

- **Assign Member to a Task:** `POST /{taskId}/assign/{teamMemberId}`
    - **Response Body:**
    ```json
    {
        "isAssignedId": 1,
        "taskId": 4,
        "teamMemberId": 2,
        "teamId": 3
    }
    ```
    - **Description:** Assigns a member (admin or team member) to a task.

- **Change Password:** `POST /team-members/{teamMemberId}/change-password`
    - **Request Body:**
    ```json
    {
        "oldPassword": "oldPass123",
        "newPassword": "newPass456"
    }
    ```
    - **Description:** Updates the password field for a team member.

- **Reset Password:** `POST /team-members/{teamMemberId}/reset-password`
    - **Request Body:**
    ```json
    {
        "newPassword": "newPass456"
    }
    ```
    - **Description:** Resets the password field for a team member.


- **Get All Teams for a Team Member:** `GET /{teamMemberId}/teams`
    - **Response Body:**
    ```json
    [
        {
            "teamId": 1,
            "teamName": "Development Team"
        },
        {
            "teamId": 2,
            "teamName": "Marketing Team"
        }
    ]
    ```
    - **Description:** Returns a list of all the teams a team member is a part of.

- **Get Assigned Tasks For a Team Member:** `GET /{teamMemberId}/tasks`
    - **Response Body:**
    ```json
    [
        {
            "taskId": 101,
            "title": "Implement Login API",
            "description": "Develop the login functionality for the app",
            "isLocked": false,
            "status": "In Progress",
            "dateCreated": "2024-03-04",
            "dueDate": "2024-04-01",
            "teamId": 1,
            "assignedMembers": 
            [
                {
                    "accountId": 1,
                    "userName": "Name",
                    "userEmail": "email@ex.com"
                },
                {
                    "accountId": 2,
                    "userName": "Name2",
                    "userEmail": "email_2@ex.com"
                }
            ]
        },
        {
            "taskId": 102,
            "title": "Design Homepage",
            "description": "Create a wireframe for the homepage",
            "isLocked": true,
            "status": "Pending",
            "dueDate": null,
            "teamId": 2,
            "assignedMembers": []
        }
    ]
    ```
    - **Description:** Returns a list of every task a team member is assigned to.

## **NotificationController**

**Base URL:** `/notifs`

### Endpoints
- **Get Read Notifications:** `GET /{teamMemberId}/read-notifs`
    - **Response Body:**
    ```json
    {
        [
            {
                "notificationId": 1,
                "message": "Task Updated",
                "type": "TASK_EDITED",
                "isRead": true,
                "createdAt": "2024-03-15T10:30:00",
                "teamMemberId": 101,
                "taskId": 5
            },
            {
                "notificationId": 4,
                "message": "Project Deadline Extended",
                "type": "TASK_DUE_DATE_EDITED",
                "isRead": true,
                "createdAt": "2024-03-16T14:45:00",
                "teamMemberId": 101,
                "taskId": 8
            }
        ] 
    }
    ```
    - **Description:** Returns a list of all read notifications for a specific team member.

- **Get Unread Notifications:** `GET /{teamMemberId}/unread-notifs`
    - **Response Body:**
    ```json
    {
        [
            {
                "notificationId": 1,
                "message": "Task Updated",
                "type": "TASK_EDITED",
                "isRead": true,
                "createdAt": "2024-03-15T10:30:00",
                "teamMemberId": 101,
                "taskId": 5
            },
            {
                "notificationId": 4,
                "message": "Project Deadline Extended",
                "type": "TASK_DUE_DATE_EDITED",
                "isRead": true,
                "createdAt": "2024-03-16T14:45:00",
                "teamMemberId": 101,
                "taskId": 8
            }
        ] 
    }
    ```
    - **Description:** Returns a list of all unread notifications for a specific team member.

- **Mark Notification as Read:** `GET /{notificationId}/mark-as-read`
    - **Response Body:**
    ```json
    {
        "message": "Notification marked as read."
    }
    ```
    - **Description:** Marks a notification as read.

- **Mark Notification as Unread:** `GET /{notificationId}/mark-as-unread`
    - **Response Body:**
    ```json
    {
        "message": "Notification marked as unread."
    }
    ```
    - **Description:** Marks a notification as unread.

- **Delete Notification:** `DELETE /{notificationId}`
    - **Response Status:**
    ```
        HTTP/1.1 204 No Content
    ```
    - **Description:** Deletes a notification from the database.

---

## **Path Variables**

- **AdminController**
    - `{adminId}` (integer, required): The ID of the admin
    - `{teamMemberId}` (integer, required): The ID of the team member
    - `{teamId}` (integer, required): The ID of the team
    - `{taskId}` (integer, required): The ID of the task
- **IsAssigned Controller**
    - `{teamMemberId}` (integer, required): The ID of the team member
    - `{taskId}` (integer, required): The ID of the task
- **IsMemberOfController**
    - `{teamMemberId}` (integer, required): The ID of the team member
    - `{teamId}` (integer, required): The ID of the team
- **TaskController**
    - `{taskId}` (integer, required): The ID of the task
- **TeamController**
    - `{teamId}` (integer, required): The ID of the team
- **TeamMemberController**
    - `{taskId}` (integer, required): The ID of the task
    - `{teamMemberId}` (integer, required): The ID of the team member

---

## **DTO References**

- **TaskDTO**
    - `title` (string, required): The title of the task.
    - `description` (string, optional): A description of the task.
    - `isLocked` (boolean, optional): Whether the task is locked. Defaults to `null` if not provided.
    - `status` (string, optional): The task status (e.g., "To-Do", "In Progress", "Done"). Defaults to `null` if not provided.
    - `dueDate` (string, optional): The due date of the task in `YYYY-MM-DD` format.

---

## **Request and Response Examples**

Below are some examples of API requests and responses.

---

#### **CreateAdmin**
```http
POST /api/admin?name=John Doe&email=john.doe@example.com
```

- **Request Parameters**
    - `name` (string, required): The name of the new admin.
    - `email` (string, required): The email of the new admin.

- **Request Response**
```json 
{
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
}
```

#### **ModifyAdminName**
```http
PUT /api/admin/1/update-name?newName=Jane Doe
```

- **Request Parameters**
    - `adminId` (integer, required): The ID of the admin changing the members name.
    - `newName` (string, required): The new name of the member.

- **Response Body**
```json
{
    "id": 1,
    "name": "Jane Doe",
    "email": "john.doe@example.com"
}
```

#### **DeleteAdmin**
```http
DELETE /api/admin/1
```

- **Notes:** 
    - No request body required. 
    - No response body returned.


#### **CreateTask**
```http
POST /api/tasks?title=The Task Title&description=This is the description, it could get long.&isLocked=false&status=To-Do&teamId=3
```

- **Request Parameters**
    - `title` (string, required): The title of the new task.
    - `description` (string, required): The description of the new task.
    - `isLocked` (boolean, required): A boolean of whether the new task is locked or not.
    - `status` (string, required): A string of the current status of the task.
    - `teamId` (integer, required): The ID of the team being assigned to the task.

- **Response Body**
```json
{
    "taskId": 4,
    "title": "Design Database Schema",
    "description": "Create the database structure.",
    "isLocked": false,
    "status": "To-Do",
    "teamId": 3
}
```

---

## **Request and Response Testing Examples**

Below are examples of some API requests and responses using Cypress.

---

- **AdminController**
    - **CreateAdmin**
    ```javascript
    cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/admin',
        qs: {
            name: 'John Doe',
            email: 'john@example.com'
        }
    }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body.name).to.eq('John Doe');
    });
    ```

    - **DeleteAdmin**
    ```javascript
    cy.request({
        method: 'DELETE',
        url: 'http://localhost:8080/api',
        qs: {
            adminId: 1
        }
    }).then((response) => {
        expect(response.status).to.eq(204);
    });
    ```

- **TeamMemberController**
    - **Assign Member To Task**
    ```javascript
    const taskId = 5;
    const teamMemberId = 7;

    cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/tasks/${taskId}/assign/${teamMemberId}'
    }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('taskId', taskId);
        expect(response.body).to.have.property('teamMemberId', teamMemberId);
    });
    ```

---

## **Best Practices**

Follow these practices to ensure efficiency and accuracy with all API requests.

### **Correct HTTP Methods**
- **GET**: Used to retrieve resources. Cannot modify data.
- **POST**: Used to create resources.
- **PUT**: Used to update existing resources.
- **DELETE**: Used to delete resources.

### **Request Formatting**
- Ensure that required parameters are included in the request body or URL path.
- Avoid sending unnecessary fields in API requests.

---

## Error Codes
| Code      | Description                               |
|-----------|-------------------------------------------|
| 200       | OK - Request succeeded                    |
| 201       | Created - Resource successfully created   |
| 204       | No Content - Resource deleted             |
| 400       | Bad Request - Invalid input               |
| 401       | Unauthorized - Authentication required    |
| 403       | Forbidden - Insufficient permissions      |
| 404       | Not Found - Resource not found            |
| 500       | Internal Server Error - Unexpected error  |

---

## Example Error Response
```json
{
    "message": "Resource not found",
    "error": "NotFoundException",
    "statusCode": 404
}