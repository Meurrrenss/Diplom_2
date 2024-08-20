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
import rest.steps.CheckResponseSteps;
import rest.steps.UserSteps;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.fail;

public class OrderListTests {
    private String email, password, name, token;
    private boolean isOrderCreated = false;
    private final OrderSteps orderApi = new OrderSteps();
    private final UserSteps userApi = new UserSteps();
    private final CheckResponseSteps baseApi = new CheckResponseSteps();

    @Before
    @Step("Подготовка тестовых данных")
    public void createTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        baseApi.checkStatusCode(response, HttpStatus.SC_OK);

        // Получение токена авторизации
        if (response.getStatusCode() == 200) {
            token = userApi.getToken(response);
        }

        //Получение списка ингредиентов
        response = orderApi.getIngredientList();
        baseApi.checkStatusCode(response, HttpStatus.SC_OK);

        List<Ingredient> ingredients = response.body().as(IngredientsResponse.class).getData();

        // Создание заказа
        response = orderApi.createOrder(
                List.of(ingredients.get(0).get_id(), ingredients.get(ingredients.size() - 1).get_id()),
                token
        );
        baseApi.checkStatusCode(response, HttpStatus.SC_OK);

        if(response.getStatusCode() == 200) {
            isOrderCreated = true;
        }
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if(token == null)
            return;

        baseApi.checkStatusCode(userApi.deleteUser(token), HttpStatus.SC_ACCEPTED);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя: авторизованный пользователь")
    public void getOrderListWithAuthIsSuccess() {
        if (token == null || !isOrderCreated)
            fail("Не создан тестовый пользователь или заказ");

        Response response = orderApi.getOrderList(token);

        baseApi.checkStatusCode(response, HttpStatus.SC_OK);
        baseApi.checkLabelSuccess(response, "true");
    }
    @Test
    @DisplayName("Получение заказов конкретного пользователя: неавторизованный пользователь")
    public void getOrderListWithoutAuthIsFailed() {
        if (token == null || !isOrderCreated)
            fail("Не создан тестовый пользователь или заказ");

        Response response = orderApi.getOrderList("");

        baseApi.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        baseApi.checkLabelSuccess(response, "false");
        baseApi.checkLabelMessage(response, "You should be authorised");
    }

    @Test
    @DisplayName("Получение всех заказов")
    public void getOrderListAllIsSuccess() {
        Response response = orderApi.getOrderListAll();

        baseApi.checkStatusCode(response, HttpStatus.SC_OK);
        baseApi.checkLabelSuccess(response, "true");
    }
}
