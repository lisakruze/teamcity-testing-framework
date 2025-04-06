package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Configuration;
import com.example.teamcity.api.models.User;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;
import java.nio.file.Paths;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

public class Specifications {
    private static RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.addFilter(new RequestLoggingFilter());
        requestBuilder.addFilter(new ResponseLoggingFilter());
        requestBuilder.addFilter(new SwaggerCoverageRestAssured(
                new FileSystemOutputWriter(
                        Paths.get("target/" + OUTPUT_DIRECTORY)
                )
        ));
        requestBuilder.addFilter(new AllureRestAssured());
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder;
    }

    public static RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s/httpAuth".formatted("", Configuration.getProperty("superUserToken"), Configuration.getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();
        return requestBuilder.build();
    }

    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Configuration.getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification mockSpec() {
        return reqBuilder()
                .setBaseUri("http://localhost:8081")
                .build();
    }
}

