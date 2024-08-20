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

import java.util.UUID;

import static org.junit.Assert.fail;

public class LoginUserTests {
    private String email, password, name, token;
    private final UserSteps userApi = new UserSteps();
    private final CheckResponseSteps checks = new CheckResponseSteps();

    @Before
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, HttpStatus.SC_OK);

        // Получение токена авторизации
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }
        if (token == null)
            fail("Не создался тестовый пользователь");
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if(token.isEmpty())
            return;

        checks.checkStatusCode(userApi.deleteUser(token), HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    public void loginUserIsSuccess() {
        Response response = userApi.loginUser(email, password);

        checks.checkStatusCode(response, HttpStatus.SC_OK);
        checks.checkLabelSuccess(response, "true");
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным email")
    public void loginUserIncorrectEmailIsFailed() {
        Response response = userApi.loginUser("newE-mail_" + UUID.randomUUID() + "@mail.com", password);

        checks.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void loginUserIncorrectPasswordIsFailed() {
        Response response = userApi.loginUser(email, password  + UUID.randomUUID());

        checks.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация пользователя без email")
    public void loginUserMissedEmailIsFailed() {
        Response response = userApi.loginUser("", password);

        checks.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "email or password are incorrect");
    }
    @Test
    @DisplayName("Авторизация пользователя без пароля")
    public void loginUserMissedPasswordIsFailed() {
        Response response = userApi.loginUser(email, "");

        checks.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "email or password are incorrect");
    }
}
