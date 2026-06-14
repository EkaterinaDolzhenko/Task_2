package praktikum.Orders;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import java.net.HttpURLConnection;
import static org.hamcrest.Matchers.*;

public class OrdersChecker {

    @Step("Проверка успешного создания заказа")
    public void createdSuccessfully(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Step("Проверка создания заказа без авторизации (должен создаться)")
    public void createdWithoutAuthSuccessfully(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Step("Проверка ошибки при создании заказа без ингредиентов")
    public void creationFailedWithoutIngredients(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST) // 400
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Проверка ошибки при создании заказа с неверным хешем ингредиентов")
    public void creationFailedWithInvalidHash(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR); // 500

    }

    @Step("Проверка успешного получения заказов пользователя")
    public void getUserOrdersSuccessfully(ValidatableResponse response, int expectedOrdersCount) {
        response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", equalTo(expectedOrdersCount))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Step("Проверка получения заказов пользователя без авторизации")
    public void getUserOrdersFailedWithoutAuth(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Проверка получения заказов авторизованного пользователя с пустым списком")
    public void getUserOrdersEmpty(ValidatableResponse response) {
        response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders", emptyIterable())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Step("Проверка, что заказы разных пользователей не перемешиваются")
    public void verifyOrdersIsolation(ValidatableResponse firstUserResponse,
                                      ValidatableResponse secondUserResponse,
                                      int firstUserExpectedCount,
                                      int secondUserExpectedCount) {
        firstUserResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(firstUserExpectedCount));

        secondUserResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(secondUserExpectedCount));
    }
}