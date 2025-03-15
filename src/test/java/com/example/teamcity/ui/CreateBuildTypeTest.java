package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import com.example.teamcity.ui.pages.BuildsTypePage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import lombok.val;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

public class CreateBuildTypeTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String WRONG_REPO_URL = "123";

    @Test(description = "User should be able to create build type for project", groups = {"Positive"})
    public void userCreatesBuildType() {
        step("UI: Login as user");
        loginAs(testData.getUser());

        step("API: Create project");
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        step("UI: Create build type");
        CreateBuildTypePage.open(createdProject.getId())
                .createForm(REPO_URL)
                .submitBuildType();


        step("API: Check build types");
        val createdBuildTypeByProject = userCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read("project:" + createdProject.getId());
        val buildTypeId = createdProject.getId() + "_Build";
        val createdBuildTypeById = userCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + buildTypeId);
        softy.assertEquals(createdBuildTypeByProject, createdBuildTypeById);


        step("UI: Check build type");
        BuildsTypePage.open(buildTypeId)
                .title.shouldHave(Condition.exactText(createdBuildTypeByProject.getName()));

    }

    @Test(description = "User should not be able to create build type for project with wrong repo url", groups = {"Negative"})
    public void userCreatesBuildTypeWithWrongRepoUrl() {
        step("Login as user");
        loginAs(testData.getUser());
        step("API: Create project");
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        step("UI: Create build type and validate error");
        val errorText = CreateBuildTypePage.open(createdProject.getId())
                .createFormUnvalidated(WRONG_REPO_URL)
                .getRepoUrlValidationErrorText();
        softy.assertEquals(errorText, "Cannot create a project using the specified URL. The URL is not recognized.");
        step("API: Check build type was not created");
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .read("project:" + createdProject.getId())
                .then().spec(ValidationResponseSpecifications.checkNoBuildTypeFound(createdProject.getId()));

    }

}
