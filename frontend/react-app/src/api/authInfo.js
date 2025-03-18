const BASE_URL = "http://localhost:8080/auth-info"; 

export const login = async (accountId, password) => {
    try {
        const response = await fetch(`${BASE_URL}/login`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({accountId, password})
        });

        if (!response.ok) {
            console.error(`Login failed: ${response.status} ${response.statusText}`);
            return null;
        }

        return await response.json();
    }
    catch (error) {
        console.error("Error during login: ", error);
        throw error;
    }
};

export const isAdmin = async (teamMemberId) => {
    try {
        const response = await fetch(`${BASE_URL}/${teamMemberId}/is-admin`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json"
            },
            //body: JSON.stringify({teamMemberId})
        });

        if (!response.ok) {
            console.error(`Failed to check member role: ${response.status} ${response.statusText}`);
            return false;
        }

        const data = await response.json();
        return data.isAdmin === true;
    }
    catch (error) {
        console.error("Error checking member role: ", error);
        return false;
    }
};