import { createTask, deleteTask, editTask, assignMemberToTask, changePassword, resetPassword, getTeamsForMember, getAssignedTasks } from '../../api/teamMemberApi';

const BASE_URL = "http://localhost:8080/api/tasks";

beforeEach(() => {
    fetch.resetMocks();
});

describe('Team Member API', () => {
    //test: creating a task
    test('createTask should return task data on sucess', async () => {
        const mockTask = {
            id: 1,
            title: "New Task",
            description: "Task description",
            isLocked: false,
            status: "To-Do",
            dueDate: "4-14-2025",
            teamId: 2
        };

        fetch.mockResponseOnce(JSON.stringify(mockTask), {status: 201});

        const result = await createTask("New Task", "Task description", false, "To-Do", "4-14-2025",2);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title: "New Task",
                description: "Task description",
                isLocked: false,
                status: "To-Do",
                dueDate: "4-14-2025",
                teamId: 2
            })
        });

        expect(result).toEqual(mockTask);
    });

    //test: deleting a task
    test('deleteTask should return true on success', async () => {
        fetch.mockResponseOnce('', { status: 204 });

        const result = await deleteTask(1);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1`, {
            method: 'DELETE'
        });

        expect(result).toBe(true);
    });

    //test: editing a task
    test('editTask should return updated task data on success', async () => {
        const updatedTask = {
            id: 1,
            title: "Updated task",
            description: "Updated description",
            isLocked: true,
            status: "In progress",
            dueDate: "2025-03-01"
        };

        fetch.mockResponseOnce(JSON.stringify(updatedTask), { status: 200 });

        const result = await editTask(1, "Updated Task", "Updated description", true, "In progress", "2025-03-01");

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/1`, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title: "Updated Task",
                description: "Updated description",
                isLocked: true,
                status: "In progress",
                dueDate: "2025-03-01"
            })
        });

        expect(result).toEqual(updatedTask);
    });

    //test: assigning a memebr to a task
    test('assignMemberToTask should return task assignment data on sucess', async () => {
        const mockResponse = {
            taskId: 2,
            teamMemberId: 7,
            assigned: true
        };

        fetch.mockResponseOnce(JSON.stringify(mockResponse), { status: 200 });

        const result = await assignMemberToTask(2, 7);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/2/assign/7`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" }
        });

        expect(result).toEqual(mockResponse);
    });

    //test: changing a password
    test('changePassword should return success on valid password change', async () => {
        const mockResponse = true;

        const result = await changePassword(5, "oldPassword", "newPassword");

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/team-members/5/change-password`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({oldPassword: "oldPassword", newPassword: "newPassword"})
        });

        expect(result.ok).toEqual(mockResponse);
    });

    //test: resetting a password
    test('resetPassword should return success on valid password reset', async () => {
        const mockResponse = true;

        const result = await resetPassword(5, "newPassword");

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/team-members/5/reset-password`, {
            method: 'POST',
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({newPassword: "newPassword"})
        });

        expect(result.ok).toEqual(mockResponse);
    });

    //test: getting all teams for a team member
    test('getTeamsForMember should return json body on success', async () => {
        const mockResponse = { teams: ["Team 1", "Team 2"] };
        const teamMemberId = 1;

        fetch.mockResponseOnce(JSON.stringify(mockResponse), { status: 200 });

        const result = await getTeamsForMember(teamMemberId);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/${teamMemberId}/teams`, {
            method: 'GET'
        });

        expect(result).toEqual(mockResponse);
    });

    //test: getting all assigned tasks to a team member
    test('getAssignedTasks should return json body on success', async () => {
        const mockResponse = {tasks: ["Task 1", "Task 2"]};
        const teamMemberId = 1;

        fetch.mockResponseOnce(JSON.stringify(mockResponse), { status: 200 });

        const result = await getAssignedTasks(teamMemberId);

        expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/${teamMemberId}/tasks`, {
            method: 'GET'
        });

        expect(result).toEqual(mockResponse);
    });
});