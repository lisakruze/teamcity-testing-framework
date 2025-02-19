//package com.example.teamcity.api;
//
//import com.example.teamcity.api.models.User;
//import com.example.teamcity.api.spec.Specifications;
//import io.restassured.RestAssured;
//import org.testng.annotations.Test;
//
//public class DummyTest extends BaseApiTest {
//    //именовать тесты через требования
//    @Test
//    public void userShouldBeAbleGetAllProjects() {
//        RestAssured
//                .given()
//                .spec(Specifications.getSpec()
//                        .authSpec(User.builder()
//                                .username("admin").password("admin")
//                                .build()))
//                .get("/app/rest/projects");
//    }
//}
