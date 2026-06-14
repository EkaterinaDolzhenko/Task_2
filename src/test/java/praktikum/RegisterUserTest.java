package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.User.User;
import praktikum.User.UserChecker;
import praktikum.User.UserClient;

public class RegisterUserTest {

    private final UserClient userClient = new UserClient();
    private final UserChecker check = new UserChecker();
    private User createdUser; // Для хранения созданного пользователя
    private String accessToken;

    @AfterEach
    public void cleanUp() {
        if (accessToken != null && createdUser != null) {
            ValidatableResponse deleteResponse = userClient.delete(accessToken);
            check.deletedSuccessfully(deleteResponse);
            System.out.println("User deleted: " + createdUser.getEmail());
        }
    }

    @DisplayName("Успешное создание пользователя")
    @Test
    public void createUserReturnSuccess() {
        User newUser = User.randomUser();
        createdUser = newUser;

        ValidatableResponse response = userClient.create(newUser);
        check.createdSuccessfully(response, newUser);

        // Извлекаем токен из ответа для последующего удаления
        accessToken = response.extract().path("accessToken");
    }

    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Test
    public void createUserWithExistingEmailReturnForbidden() {
        // Создаем первого пользователя
        User firstUser = User.randomUser();
        createdUser = firstUser;

        ValidatableResponse firstResponse = userClient.create(firstUser);
        check.createdSuccessfully(firstResponse, firstUser);
        accessToken = firstResponse.extract().path("accessToken");

        // Пытаемся создать второго пользователя с тем же email
        User duplicateUser = new User(
                firstUser.getEmail(),
                "anotherPassword123",
                "AnotherName"
        );

        ValidatableResponse duplicateResponse = userClient.create(duplicateUser);
        check.creationConflictForExistingUser(duplicateResponse);
    }

    @DisplayName("Создание пользователя без email - ожидается ошибка")
    @Test
    public void createUserWithoutEmailReturnForbidden() {
        User userWithoutEmail = User.userWithoutEmail();

        ValidatableResponse response = userClient.create(userWithoutEmail);
        check.creationFailedWithMissingFields(response);
    }

    @DisplayName("Создание пользователя без password - ожидается ошибка")
    @Test
    public void createUserWithoutPasswordReturnForbidden() {
        User userWithoutPassword = User.userWithoutPassword();

        ValidatableResponse response = userClient.create(userWithoutPassword);
        check.creationFailedWithMissingFields(response);
    }

    @DisplayName("Создание пользователя без name - ожидается ошибка")
    @Test
    public void createUserWithoutNameReturnForbidden() {
        User userWithoutName = User.userWithoutName();

        ValidatableResponse response = userClient.create(userWithoutName);
        check.creationFailedWithMissingFields(response);
    }

    @DisplayName("Создание пользователя с пустым email - ожидается ошибка")
    @Test
    public void createUserWithEmptyEmailReturnForbidden() {
        User userWithEmptyEmail = User.userWithEmptyEmail();

        ValidatableResponse response = userClient.create(userWithEmptyEmail);
        check.creationFailedWithMissingFields(response);
    }

    @DisplayName("Создание пользователя с пустым password - ожидается ошибка")
    @Test
    public void createUserWithEmptyPasswordReturnForbidden() {
        User userWithEmptyPassword = User.userWithEmptyPassword();

        ValidatableResponse response = userClient.create(userWithEmptyPassword);
        check.creationFailedWithMissingFields(response);
    }

    @DisplayName("Создание пользователя с пустым name - ожидается ошибка")
    @Test
    public void createUserWithEmptyNameReturnForbidden() {
        User userWithEmptyName = User.userWithEmptyName();

        ValidatableResponse response = userClient.create(userWithEmptyName);
        check.creationFailedWithMissingFields(response);
    }
}