package rest.httpclient;

import io.restassured.response.Response;
import models.Endpoints;
import models.requestmodels.User;

public class UserHTTPClient extends BaseHTTPClient {
    public Response createUser(User user) {
        return postRequest(
                Endpoints.HOST + Endpoints.REGISTER_USER,
                user,
                "application/json"
        );
    }
    public Response deleteUser(String token) {
        return deleteRequest(
                Endpoints.HOST + Endpoints.USER_INFO,
                token
        );
    }
    public Response loginUser(User user) {
        return postRequest(
                Endpoints.HOST + Endpoints.LOGIN_USER,
                user,
                "application/json"
        );
    }
    public Response updateUser(User user, String token) {
        return patchRequest(
                Endpoints.HOST + Endpoints.USER_INFO,
                user,
                "application/json",
                token
        );
    }
}
