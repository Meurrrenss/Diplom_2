package rest.steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.responsemodels.UserResponse;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.equalTo;

public class CheckResponseSteps {
    @Step("Проверка кода ответа")
    public void checkStatusCode(Response response, int code) {
        Allure.addAttachment("Ответ", response.getStatusLine());
        response.then().statusCode(code);
    }

    @Step("Проверка успешности обращения")
    public void checkLabelSuccess(Response response, String expectedValue) {
        MatcherAssert.assertThat(
                "Значение поля success не сопадает с ожидаемым",
                expectedValue,
                equalTo(response.body().as(UserResponse.class).getSuccess())
        );
    }
    @Step("Проверка сообщения ответа")
    public void checkLabelMessage(Response response, String expectedMessage) {
        MatcherAssert.assertThat(
                "Значение поля message не сопадает с ожидаемым",
                expectedMessage,
                equalTo(response.body().as(UserResponse.class).getMessage())
        );
    }
}
