const BASE_URL = "http://localhost:8080/api/teams";

//Create a team
export const createTeam = async (teamLeadId, teamName) => {
    try {
        const response = await fetch(`${BASE_URL}`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({teamLeadId, teamName})
        });

        if (!response.ok) {
            console.error(`Failed to create team: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error creating team: ", error);
        return null;
    }
};

//Delete a team
export const deleteTeam = async (teamId) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            console.error(`Failed to delete team: ${response.status} ${response.statusText}`);
            return null;
        }

        return true;
    }
    catch (error) {
        console.error("Error deleting team: ", error);
        return null;
    }
};

//Change team lead
export const changeTeamLead = async (teamId, teamLeadId, teamName) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamId}/change-lead`, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({teamLeadId, teamName})
        });

        if (!response.ok) {
            console.error(`Failed to change team lead: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error changing team lead: ", error);
        return null;
    }
};

//Get all team members of team
export const getTeamMembers = async (teamId) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamId}/members`, {
            method: 'GET'
        });

        if (!response.ok) {
            console.error(`Failed to return all team members: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error getting all team members: ", error);
        return null;
    }
};

export const getTeamTasks = async (teamId) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamId}/tasks`, {
            method: 'GET'
        });

        if (!response.ok) {
            console.error(`Failed to return all tasks connected to a team: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }  
    catch (error) {
        console.error("Error returning all tasks connected to a team: ", error);
        return null;
    }
};