package praktikum.Orders;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.EnvConfig;
import praktikum.Client;

public class OrdersClient {

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createWithAuth(String accessToken, Orders orders) {
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        return Client.spec()
                .header("Authorization", authHeader)
                .body(orders)
                .when()
                .post(EnvConfig.ORDERS_ENDPOINT)
                .then().log().all();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createWithoutAuth(Orders orders) {
        return Client.spec()
                .body(orders)
                .when()
                .post(EnvConfig.ORDERS_ENDPOINT)
                .then().log().all();
    }

    @Step("Получение заказов пользователя с авторизацией")
    public ValidatableResponse getUserOrdersWithAuth(String accessToken) {
        String authHeader = accessToken.startsWith("Bearer ") ? accessToken : "Bearer " + accessToken;

        return Client.spec()
                .header("Authorization", authHeader)
                .when()
                .get(EnvConfig.ORDERS_ENDPOINT)
                .then().log().all();
    }

    @Step("Получение заказов пользователя без авторизации")
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return Client.spec()
                .when()
                .get(EnvConfig.ORDERS_ENDPOINT)
                .then().log().all();
    }
}