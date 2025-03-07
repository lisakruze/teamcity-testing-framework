package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest{


    @Test(description = "Project with correct data can be created by Super user", groups = {"Positive", "CRUD"})
    public void superAdminCanCreateProject() {
        var superuserCheckedRequests = new CheckedRequests(Specifications.superUserSpec());
        var createdProject = superuserCheckedRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        softy.assertEquals(createdProject, testData.getProject(), "Actual project does not match expected");
    }
}
