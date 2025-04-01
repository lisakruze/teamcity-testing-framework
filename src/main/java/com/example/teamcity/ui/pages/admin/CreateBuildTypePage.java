package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String PROJECT_SHOW_MODE = "createBuildTypeMenu";
    private final SelenideElement errorErl = $("#error_url");

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public CreateBuildTypePage createFormUnvalidated(String url) {
        baseCreateFormUnvalidated(url);
        return this;
    }

    public void submitBuildType() {
        submitButton.shouldBe(Condition.enabled, BASE_WAITING).click();
    }

    public String getRepoUrlValidationErrorText() {
        errorErl.should(Condition.appear, BASE_WAITING);
        return errorErl.text();
    }

    public SelenideElement getRepoUrlValidationErrorElement() {
        return errorErl;
    }

}
