import {login, isAdmin} from '../../api/authInfoApi'; 

const BASE_URL = "http://localhost:8080/api/auth";

beforeEach(() => {
    fetchMock.resetMocks();
});

describe('AuthInfo API', () => {
    //test: logging in
    test('login should return response body on success', async () => {
        const mockUser = {
            id: 1,
            name: "Test User",
            isAdmin: true
        };

        fetch.mockResponseOnce(JSON.stringify(mockUser), {status: 200});

        const response = await login(1, "password");

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/login`, {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({accountId: 1, password: "password"})
        });

        expect(response).toEqual(mockUser);
    });

    //test: check if user is an admin
    test('isAdmin should return true when user is an admin', async () => {
        fetch.mockResponseOnce(JSON.stringify("ADMIN"), { status: 200 });

        const response = await isAdmin(1);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1/is-admin`, {
            method: 'GET',
            headers: { "Content-Type": "application/json" }
        });

        expect(response).toBe("ADMIN");
    });

    //test: check if user is not an admin
    test('isAdmin should return false when user is not an admin', async () => {
        fetch.mockResponseOnce(JSON.stringify("TEAM_MEMBER"), { status: 200 });

        const response = await isAdmin(1);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1/is-admin`, {
            method: 'GET',
            headers: { "Content-Type": "application/json" }
        });

        expect(response).toBe("TEAM_MEMBER");
    });
});
