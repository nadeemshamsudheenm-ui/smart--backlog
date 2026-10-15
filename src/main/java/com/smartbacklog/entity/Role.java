package com.smartbacklog.entity;

public enum Role {
    ROLE_ADMIN("Admin"),
    ROLE_HOD("Head of Department"),
    ROLE_STUDENT("Student"),
    ROLE_PARENT("Parent"),
    ROLE_TUTOR("Tutor / Faculty Advisor");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
