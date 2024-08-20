package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.requestmodels.Ingredient;
import models.responsemodels.IngredientsResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rest.steps.OrderSteps;
import rest.steps.UserSteps;
import rest.steps.CheckResponseSteps;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.fail;

public class CreateOrderTests {
    private String email, password, name, token;
    private List<Ingredient> ingredients = new ArrayList<>();
    private final OrderSteps orderApi = new OrderSteps();
    private final UserSteps userApi = new UserSteps();
    private final CheckResponseSteps checks = new CheckResponseSteps();

    @Before
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass_" + UUID.randomUUID();
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, HttpStatus.SC_OK);

        // Получение токена
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }

        // Получение списка ингредиентов
        response = orderApi.getIngredientList();
        checks.checkStatusCode(response, HttpStatus.SC_OK);

        ingredients = response.body().as(IngredientsResponse.class).getData();

        if(token == null || ingredients.isEmpty())
            fail("Отсутствует токен или не получен список ингредиентов");
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if(token == null)
            return;

        checks.checkStatusCode(userApi.deleteUser(token), HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией, с ингредиентами")
    public void createOrderWithAuthAndIngredientsIsSuccess() {
        Response response = orderApi.createOrder(
                List.of(ingredients.get(0).get_id(), ingredients.get(ingredients.size() - 1).get_id()),
                token
        );

        checks.checkStatusCode(response, HttpStatus.SC_OK);
        checks.checkLabelSuccess(response, "true");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации, с ингредиентами")
    public void createOrderWithoutAuthAndWithIngredientsIsSuccess() {
        Response response = orderApi.createOrder(
                List.of(ingredients.get(0).get_id(), ingredients.get(ingredients.size() - 1).get_id()),
                ""
        );

        checks.checkStatusCode(response, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией, без ингредиентов")
    public void createOrderWithAuthAndWithoutIngredients() {
        Response response = orderApi.createOrder(
                List.of(),
                token
        );

        checks.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        checks.checkLabelSuccess(response, "false");
        checks.checkLabelMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: с неверным хешем ингредиентов")
    public void createOrderWithAuthAndIncorrectIngredientsIsFailed() {
        Response response = orderApi.createOrder(
                List.of(ingredients.get(0).get_id(), UUID.randomUUID().toString()),
                token
        );

        checks.checkStatusCode(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
