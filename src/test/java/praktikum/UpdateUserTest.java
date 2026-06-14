package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.User.User;
import praktikum.User.UserChecker;
import praktikum.User.UserClient;

public class UpdateUserTest {

    private final UserClient userClient = new UserClient();
    private final UserChecker check = new UserChecker();
    private User createdUser;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        // Создаем пользователя
        createdUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(createdUser);
        check.createdSuccessfully(createResponse, createdUser);

        // Получаем токен для авторизованных запросов
        accessToken = createResponse.extract().path("accessToken");

        System.out.println("User created: " + createdUser.getEmail());
        System.out.println("AccessToken: " + accessToken);
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null && createdUser != null) {
            ValidatableResponse deleteResponse = userClient.delete(accessToken);
            check.deletedSuccessfully(deleteResponse);
            System.out.println("User deleted: " + createdUser.getEmail());
        }
    }

    @DisplayName("Изменение email пользователя с авторизацией")
    @Test
    public void updateEmailWithAuthReturnSuccess() {
        User updatedUser = User.updatedUserWithNewEmail(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithAuth(accessToken, updatedUser);
        check.updatedSuccessfully(updateResponse, updatedUser);

        // Проверяем, что данные действительно обновились
        System.out.println("Email updated from " + createdUser.getEmail() + " to " + updatedUser.getEmail());
    }

    @DisplayName("Изменение name пользователя с авторизацией")
    @Test
    public void updateNameWithAuthReturnSuccess() {
        User updatedUser = User.updatedUserWithNewName(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithAuth(accessToken, updatedUser);
        check.updatedSuccessfully(updateResponse, updatedUser);

        System.out.println("Name updated from " + createdUser.getName() + " to " + updatedUser.getName());
    }

    @DisplayName("Изменение password пользователя с авторизацией")
    @Test
    public void updatePasswordWithAuthReturnSuccess() {
        User updatedUser = User.updatedUserWithNewPassword(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithAuth(accessToken, updatedUser);
        check.updatedSuccessfully(updateResponse, updatedUser);

        // Проверяем, что с новым паролем можно авторизоваться
        ValidatableResponse loginResponse = userClient.login(updatedUser);
        check.loginSuccessfully(loginResponse, updatedUser);

        System.out.println("Password updated for user: " + createdUser.getEmail());
    }

    @DisplayName("Изменение email пользователя без авторизации - ожидается ошибка")
    @Test
    public void updateEmailWithoutAuthReturnUnauthorized() {
        User updatedUser = User.updatedUserWithNewEmail(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithoutAuth(updatedUser);
        check.updateFailedWithoutAuth(updateResponse);
    }

    @DisplayName("Изменение name пользователя без авторизации - ожидается ошибка")
    @Test
    public void updateNameWithoutAuthReturnUnauthorized() {
        User updatedUser = User.updatedUserWithNewName(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithoutAuth(updatedUser);
        check.updateFailedWithoutAuth(updateResponse);
    }

    @DisplayName("Изменение password пользователя без авторизации - ожидается ошибка")
    @Test
    public void updatePasswordWithoutAuthReturnUnauthorized() {
        User updatedUser = User.updatedUserWithNewPassword(createdUser);

        ValidatableResponse updateResponse = userClient.updateWithoutAuth(updatedUser);
        check.updateFailedWithoutAuth(updateResponse);
    }

    @DisplayName("Изменение данных с неверным токеном - ожидается ошибка")
    @Test
    public void updateWithInvalidTokenReturnUnauthorized() {
        User updatedUser = User.updatedUserWithNewEmail(createdUser);
        String invalidToken = "Bearer invalid_token_12345";

        ValidatableResponse updateResponse = userClient.updateWithAuth(invalidToken, updatedUser);
        check.updateFailedWithInvalidData(updateResponse);
    }

    @DisplayName("Изменение email на уже существующий - ожидается ошибка")
    @Test
    public void updateEmailToExistingEmailReturnError() {
        // Создаем второго пользователя
        User anotherUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(anotherUser);
        check.createdSuccessfully(createResponse, anotherUser);
        String anotherUserToken = createResponse.extract().path("accessToken");

        // Пытаемся обновить первого пользователя, используя email второго
        User updatedUser = new User(anotherUser.getEmail(), createdUser.getPassword(), createdUser.getName());
        ValidatableResponse updateResponse = userClient.updateWithAuth(accessToken, updatedUser);
        check.updateFailedWithInvalidData(updateResponse);

        // Удаляем второго пользователя
        userClient.delete(anotherUserToken);
    }
}