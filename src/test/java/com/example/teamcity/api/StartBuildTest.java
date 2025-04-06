package com.example.teamcity.api;

import com.example.teamcity.api.common.WireMock;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.BUILDS;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class StartBuildTest extends BaseApiTest {


    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .id("1")
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILDS.getUrl()), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with WireMock)", groups = {"localOnly"}, enabled = false)
    public void userStartsBuildWithWireMockTest() {

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILDS);

        var build = checkedBuildQueueRequest.create(Build.builder()
                .id("123")
                .buildType(testData.getBuildType())
                .build());

//        softy.assertThat(build.getState()).as("buildState").isEqualTo("finished");
//        softy.assertThat(build.getStatus()).as("buildStatus").isEqualTo("SUCCESS");
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}
