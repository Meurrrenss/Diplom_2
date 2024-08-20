package rest.httpclient;

import io.restassured.response.Response;
import models.Endpoints;
import models.requestmodels.Order;

public class OrderHTTPClient extends BaseHTTPClient {
    public Response createOrder(Order order, String token) {
        return postRequest(
                Endpoints.HOST + Endpoints.ORDERS,
                order,
                "application/json",
                token
        );
    }
    public Response getIngredientList() {
        return getRequest(
                Endpoints.HOST + Endpoints.INGREDIENTS
        );
    }

    public Response getOrderList(String token) {
        return getRequest(
                Endpoints.HOST + Endpoints.ORDERS,
                token
        );
    }
    public Response getOrderListAll() {
        return getRequest(
                Endpoints.HOST + Endpoints.ORDERS_ALL
        );
    }
}
