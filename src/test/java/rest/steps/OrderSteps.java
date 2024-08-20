package rest.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.requestmodels.Order;
import rest.httpclient.OrderHTTPClient;

import java.util.List;

public class OrderSteps extends OrderHTTPClient {
    @Step("Отправка запроса на создание заказа")
    public Response createOrder(List<String> ingredients, String token) {
        return super.createOrder(new Order(ingredients), token);
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredientList() {
        return super.getIngredientList();
    }

    @Step("Получение списка заказов")
    public Response getOrderList(String token) {
        return super.getOrderList(token);
    }

    @Step("Получение списка всех заказов")
    public Response getOrderListAll() {
        return super.getOrderListAll();
    }
}
