package com.example.teamcity.api;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;
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

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

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
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        Project project = superUserCheckRequest.<Project>getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(new Roles(List.of(new Role("PROJECT_ADMIN", "p:" + project.getId()))));
        User user = (User) superUserCheckRequest.getRequest(USERS).create(testData.getUser());
        user.setPassword(testData.getUser().getPassword());
        CheckedRequests userCheckRequests = new CheckedRequests(Specifications.authSpec(user));
        BuildType buildTypeProject = generate(List.of(project), BuildType.class);
        userCheckRequests.getRequest(BUILD_TYPES).create(buildTypeProject);
        BuildType createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(buildTypeProject.getId());
        softy.assertEquals(buildTypeProject.getName(), createdBuildType.getName(), "BuildType name is not correct");
        softy.assertEquals(user.getRoles().getRole().get(0).getRoleId(), "PROJECT_ADMIN", "User role is not PROJECT_ADMIN");
    }

    @Test(description = "Project admin should not be able to create build type for a project they do not manage", groups = {"Negative", "Roles"})
    public void projectAdminCannotCreateBuildTypeForOtherProjectTest() {
        Project adminProject = generate(Project.class);
        superUserCheckRequest.getRequest(PROJECTS).create(adminProject);
        Project otherProject = generate(Project.class);
        superUserCheckRequest.getRequest(PROJECTS).create(otherProject);
        testData.getUser().setRoles(new Roles(List.of(new Role("PROJECT_ADMIN", "p:" + adminProject.getId()))));
        User user = (User) superUserCheckRequest.getRequest(USERS).create(testData.getUser());
        user.setPassword(testData.getUser().getPassword());
        BuildType buildTypeProject = generate(List.of(otherProject), BuildType.class);
        new UncheckedBase(Specifications.authSpec(user), BUILD_TYPES)
                .create(buildTypeProject)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(String.format("You do not have enough permissions to edit project with id: %s", otherProject.getId())));
    }

}
