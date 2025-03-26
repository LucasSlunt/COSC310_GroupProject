const BASE_URL = "http://localhost:8080/api/tasks";

//Create a task
export const createTask = async (title, description, isLocked, status, dueDate, teamId) => {
    try {
        console.log("Sending this: ", JSON.stringify({title, description, isLocked, status, dueDate, teamId}))
        const response = await fetch(`${BASE_URL}`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({title, description, isLocked, status, dueDate, teamId})
        });

        if (!response.ok) {
            console.error(`Failed to create task: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error creating task: ", error);
        throw error;
    }
};

//Delete a task
export const deleteTask = async (taskId) => {
    try {
        const response = await fetch(`${BASE_URL}/${taskId}`, {
            method: 'DELETE'
        }); 

        if (!response.ok) {
            console.error(`Failed to delete task: ${response.status} ${response.statusText}`);
            return null;
        }

        return true;
    }
    catch (error) {
        console.error("Error deleting task: ", error);
        throw error;
    }
};

//Edit task
export const editTask = async (taskId, title, description, isLocked, status, dueDate) => {
    try {
        const response = await fetch(`${BASE_URL}/${taskId}`, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({taskId, title, description, isLocked, status, dueDate})
        });

        if (!response.ok) {
            console.error(`Failed to edit task: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error editing task: ", error);
        throw error;
    }
};

//Assign member to task
export const assignMemberToTask = async (taskId, teamMemberId) => {
    try {
        const response = await fetch(`${BASE_URL}/${taskId}/assign/${teamMemberId}`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) {
            console.error(`Failed to assign member to task: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error assigning member to task: ", error);
        throw error;
    }
};

//Change password
export const changePassword = async (teamMemberId, oldPassword, newPassword) => {
    try {
        const response = await fetch(`${BASE_URL}/team-members/${teamMemberId}/change-password`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({oldPassword, newPassword})
        });

        if (!response.ok) {
            console.error(`Failed to change password: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error changing password: ", error);
        throw error;
    }
};

//getting all teams for a specific team member
export const getTeamsForMember = async (accountId) => {
    try {
        const response = await fetch(`${BASE_URL}/${accountId}/teams`, {
            method: 'GET'
        });

        if (!response.ok) {
            console.error(`Failed to retrieve teams: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    }
    catch (error) {
        console.error(`Failed to retrieve teams: `, error);
        throw error;
    }
};

//getting all tasks for a team member
export const getAssignedTasks = async (teamMemberId) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamMemberId}/tasks`, {
            method: 'GET'
        });

        if (!response.ok) {
            console.error(`Failed to retrieve tasks: ${response.status} ${response.statusText}`);    
        }

        return await response.json();
    }
    catch (error) {
        console.error(`Failed to retrieve tasks: `, error);
        throw error;
    }
};