package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rest.steps.CheckResponseSteps;
import rest.steps.UserSteps;

import java.util.ArrayList;
import java.util.UUID;

public class CreateUserTests {
    private String email, password, name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final UserSteps userApi = new UserSteps();
    private final CheckResponseSteps checks = new CheckResponseSteps();

    @Before
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass_" + UUID.randomUUID();
        name = "name";
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void deletingUser() {
        if(tokens.isEmpty())
            return;
        for (String token: tokens) {
            checks.checkStatusCode(userApi.deleteUser(token), HttpStatus.SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createNewUserIsSuccess() {
        Response response = userApi.createUser(email, password, name);
        if (response.getStatusCode() == 200) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, HttpStatus.SC_OK);
        checks.checkLabelSuccess(response, "true");
    }

    @Test
    @DisplayName("Создание двух одинаковых пользователей")
    public void createSameUsersIsFailed() {
        Response responseFirstUser = userApi.createUser(email, password, name);
        Response responseSecondUser = userApi.createUser(email, password, name);
        if (responseFirstUser.getStatusCode() == 200) {
            tokens.add(userApi.getToken(responseFirstUser));
        }
        if (responseSecondUser.getStatusCode() == 200) {
            tokens.add(userApi.getToken(responseSecondUser));
        }

        checks.checkStatusCode(responseSecondUser, HttpStatus.SC_FORBIDDEN);
        checks.checkLabelSuccess(responseSecondUser, "false");
        checks.checkLabelMessage(responseSecondUser, "User already exists");
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createNewUserMissedEmailIsFailed() {
        Response response = userApi.createUser("", password, name);
        if (response.getStatusCode() == 200) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без password")
    public void createNewUserMissedPasswordIsFailed() {
        Response response = userApi.createUser(email, "", name);
        if (response.getStatusCode() == 200) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без name")
    public void createNewUserMissedNameIsFailed() {
        Response response = userApi.createUser(email, password, "");
        if (response.getStatusCode() == 200) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя, поля не заполнены")
    public void createNewUserMissedAllParamsIsFailed() {
        Response response = userApi.createUser("", "", "");
        if (response.getStatusCode() == 200) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }
}
