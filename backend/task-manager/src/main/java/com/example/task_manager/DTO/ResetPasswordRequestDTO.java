package com.example.task_manager.DTO;

public class ResetPasswordRequestDTO {
    private int teamMemberId;
    private String newPassword;

    public ResetPasswordRequestDTO() {}

    public ResetPasswordRequestDTO(int teamMemberId, String newPassword) {
        this.teamMemberId = teamMemberId;
        this.newPassword = newPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(int teamMemberId) {
        this.teamMemberId = teamMemberId;
    }
}
