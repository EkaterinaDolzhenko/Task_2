package praktikum.User;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.EnvConfig;
import praktikum.Client;

public class UserClient {

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return Client.spec()
                .body(user)
                .when()
                .post(EnvConfig.REGISTER_ENDPOINT)
                .then().log().all();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(User user) {
        return Client.spec()
                .body(user)
                .when()
                .post(EnvConfig.LOGIN_ENDPOINT)
                .then().log().all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse delete(String accessToken) {
        return Client.spec()
                .header("Authorization", accessToken)
                .when()
                .delete(EnvConfig.USER_ENDPOINT)
                .then().log().all();
    }

    @Step("Обновление данных пользователя с авторизацией")
    public ValidatableResponse updateWithAuth(String accessToken, User updatedUser) {
        return Client.spec()
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch(EnvConfig.USER_ENDPOINT)
                .then().log().all();
    }

    @Step("Обновление данных пользователя без авторизации")
    public ValidatableResponse updateWithoutAuth(User updatedUser) {
        return Client.spec()
                .body(updatedUser)
                .when()
                .patch(EnvConfig.USER_ENDPOINT)
                .then().log().all();
    }
}
