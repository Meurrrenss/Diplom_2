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

public class ChangeUserDataTests {
    private String email, password, name, token;
    private final CheckResponseSteps checks = new CheckResponseSteps();
    private final UserSteps userApi = new UserSteps();

    @Before
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        //Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, HttpStatus.SC_OK);

        // Получение токена
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }
        if(token == null)
            fail("Тестовый пользователь не создан");
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if(token == null)
            return;

        checks.checkStatusCode(userApi.deleteUser(token), HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Изменение данных пользователя: с авторизацией")
    public void changeUserDataWithAuthIsSuccess() {
        String newEmail = "new_" + email;
        String newPassword = "new_" + password;
        String newName = "new_" + name;

        Response response = userApi.updateUser(newEmail, newPassword, newName, token);

        checks.checkStatusCode(response, HttpStatus.SC_OK);
        checks.checkLabelSuccess(response, "true");
        userApi.checkUser(response, newEmail, newPassword, newName);
    }

    @Test
    @DisplayName("Изменение данных пользователя: без авторизации")
    public void changeUserDataWithoutAuthIsFailed() {
        String newEmail = "new_" + email;
        String newPassword = "new_" + password;
        String newName = "new_" + name;

        Response response = userApi.updateUser(newEmail, newPassword, newName, "");

        checks.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "You should be authorised");
    }
}
