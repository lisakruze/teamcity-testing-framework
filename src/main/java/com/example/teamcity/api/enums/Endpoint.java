package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.BuildType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPE("/app/rest/buildTypes", BuildType.class);
    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
