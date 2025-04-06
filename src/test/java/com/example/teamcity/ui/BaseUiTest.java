package com.example.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.example.teamcity.BaseTest;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.User;
import com.example.teamcity.ui.pages.LoginPage;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import java.util.Map;

public class BaseUiTest extends BaseTest {
    @BeforeSuite(alwaysRun = true)
    public void setupUiTest() {
        Configuration.browser = com.example.teamcity.api.config.Configuration.getProperty("browser");
        Configuration.baseUrl = "http://" + com.example.teamcity.api.config.Configuration.getProperty("host");
        Configuration.remote = com.example.teamcity.api.config.Configuration.getProperty("remote");
        Configuration.browserSize = com.example.teamcity.api.config.Configuration.getProperty("browserSize");
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }

    @AfterMethod(alwaysRun = true)
    public void closeWebDriver() {
        Selenide.closeWebDriver();
    }

    protected void loginAs(User user) {
        superUserCheckRequest.getRequest(Endpoint.USERS).create(testData.getUser());
        LoginPage.open().login(testData.getUser());
    }
}
