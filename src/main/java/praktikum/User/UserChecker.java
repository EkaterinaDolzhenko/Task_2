package praktikum.User;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.net.HttpURLConnection;

public class UserChecker {

    @Step("Успешное создание пользователя")
    public void createdSuccessfully(ValidatableResponse createResponse, User expectedUser) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(expectedUser.getEmail()))
                .body("user.name", equalTo(expectedUser.getName()));
    }

    @Step("Проверка ошибки при создании существующего пользователя")
    public void creationConflictForExistingUser(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Проверка ошибки при отсутствии обязательных полей")
    public void creationFailedWithMissingFields(ValidatableResponse createResponse) {
        createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Успешная авторизация пользователя")
    public void loginSuccessfully(ValidatableResponse loginResponse, User expectedUser) {
        loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(expectedUser.getEmail()))
                .body("user.name", equalTo(expectedUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("accessToken", org.hamcrest.Matchers.startsWith("Bearer "));
    }

    @Step("Проверка ошибки авторизации с неверными учетными данными")
    public void loginFailedWithInvalidCredentials(ValidatableResponse loginResponse) {
        loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Успешное удаление пользователя")
    public void deletedSuccessfully(ValidatableResponse deleteResponse) {
        deleteResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_ACCEPTED)
                .body("success", equalTo(true));
    }

    @Step("Успешное обновление данных пользователя")
    public void updatedSuccessfully(ValidatableResponse updateResponse, User expectedUser) {
        updateResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(expectedUser.getEmail()))
                .body("user.name", equalTo(expectedUser.getName()));
    }

    @Step("Проверка ошибки при обновлении данных без авторизации")
    public void updateFailedWithoutAuth(ValidatableResponse updateResponse) {
        updateResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED) // 401
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Проверка ошибки при обновлении данных с некорректными данными")
    public void updateFailedWithInvalidData(ValidatableResponse updateResponse) {
        updateResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN) // 401
                .body("success", equalTo(false));
    }
}
