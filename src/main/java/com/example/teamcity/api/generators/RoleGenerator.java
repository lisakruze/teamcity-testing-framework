package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.Role;

public class RoleGenerator {

    public static Role generateProjectAdmin(String projectId) {
        return new Role("PROJECT_ADMIN", "p:" + projectId);
    }
}
