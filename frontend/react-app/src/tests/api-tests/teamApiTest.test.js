import { createTeam, deleteTeam, changeTeamLead, getTeamMembers, getTeamTasks } from '../../api/teamApi';

const BASE_URL = "http://localhost:8080/api/teams";

beforeEach(() => {
    fetch.resetMocks();
});

describe('Team API', () => {
    //test: creating a team
    test('createTeam should return team data on success', async () => {
        const mockTeam = {
            teamLeadId: 1,
            teamName: "Team Name"
        };

        fetch.mockResponseOnce(JSON.stringify(mockTeam), { status: 200 });

        const result = await createTeam(1, "Team Name");
        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                teamLeadId: 1,
                teamName: "Team Name"
            })
        });

        expect(result).toEqual(mockTeam);
    });

    //test: deleting a team
    test('deleteTeam should return true on success', async () => {
        fetch.mockResponseOnce('', { status: 204 });

        const result = await deleteTeam(1);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1`, {
            method: 'DELETE'
        });

        expect(result).toBe(true);
    });

    //test: changing a team lead
    test('changeTeamLead should return new team data on success', async () => {
        const updatedTeam = {
            teamId: 1,
            teamLeadId: 1,
            teamName: "New Team Name"
        };

        fetch.mockResponseOnce(JSON.stringify(updatedTeam), { status: 200 });

        const result = await changeTeamLead(1, 1, "New Team Name");

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1/change-lead`, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                teamLeadId: 1,
                teamName: "New Team Name"
            })
        });

        expect(result).toEqual(updatedTeam);
    });

    //test: getting all team members
    test('getTeamMembers should return team data on success', async () => {
        fetch.mockResponseOnce(JSON.stringify(true), { status: 200 });

        const result = await getTeamMembers(1);
        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1/members`, {
            method: 'GET'
        });

        expect(result).toEqual(true);
    });

    //test: get all tasks connected to a team
    test('getTeamTasks should rteurn task data on success', async () => {
        fetch.mockResponseOnce(JSON.stringify(true), { status: 200 });

        const result = await getTeamTasks(1);
        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1/tasks`, {
            method: 'GET'
        });

        expect(result).toEqual(true);
    });
});