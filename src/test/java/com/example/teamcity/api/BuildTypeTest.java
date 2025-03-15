package com.example.teamcity.api;

import com.example.teamcity.api.generators.RoleGenerator;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;


@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequest.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read("id:" + testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same ID", groups = {"Negative", "CRUD"})
    public void userCreatesBuildTypeWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequest.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().spec(ValidationResponseSpecifications.checkBuildConfigurationAlreadyUsed(testData.getBuildType().getId()));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        Project createdProject = superUserCheckRequest.<Project>getRequest(PROJECTS).create(testData.getProject());
        Role projectAdmin = RoleGenerator.generateProjectAdmin(createdProject.getId());
        testData.getUser().setRoles(new Roles(List.of(projectAdmin)));

        User user = (User) superUserCheckRequest.getRequest(USERS).create(testData.getUser());
        user.setPassword(testData.getUser().getPassword());

        CheckedRequests userCheckRequests = new CheckedRequests(Specifications.authSpec(user));
        BuildType buildTypeProject = generate(List.of(createdProject), BuildType.class);
        userCheckRequests.getRequest(BUILD_TYPES).create(buildTypeProject);

        softy.assertEquals(createdProject, testData.getProject());
        softy.assertEquals(user.getRoles(), testData.getUser().getRoles());
    }

    @Test(description = "Project admin should not be able to create build type for a project they do not manage", groups = {"Negative", "Roles"})
    public void projectAdminCannotCreateBuildTypeForOtherProjectTest() {

        HashMap<Integer, TestData> testDataMap = TestDataGenerator.generateHashMap(2);

        Project adminProject = testDataMap.get(0).getProject();
        Project otherProject = testDataMap.get(1).getProject();

        User admin = testDataMap.get(0).getUser();

        superUserCheckRequest.getRequest(PROJECTS).create(adminProject);
        superUserCheckRequest.getRequest(PROJECTS).create(otherProject);

        Role projectAdmin = RoleGenerator.generateProjectAdmin(adminProject.getId());
        admin.setRoles(new Roles(List.of(projectAdmin)));

        User createdUser = (User) superUserCheckRequest.getRequest(USERS).create(admin);

        softy.assertEquals(admin.getRoles(), createdUser.getRoles());

        admin.setId(createdUser.getId());

        BuildType buildTypeProject = generate(List.of(otherProject), BuildType.class);
        new UncheckedBase(Specifications.authSpec(admin), BUILD_TYPES)
                .create(buildTypeProject)
                .then().spec(ValidationResponseSpecifications.checkUserCanNotEditProject(otherProject.getId()));
    }

}
