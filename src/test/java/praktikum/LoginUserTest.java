package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.User.User;
import praktikum.User.UserChecker;
import praktikum.User.UserClient;

public class LoginUserTest {

    private final UserClient userClient = new UserClient();
    private final UserChecker check = new UserChecker();
    private User createdUser;
    private String accessToken;
    //private String refreshToken;  // может пригодиться для logout

    @BeforeEach
    public void setUp() {
        createdUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(createdUser);
        check.createdSuccessfully(createResponse, createdUser);

        // Сохраняем токены для удаления
        accessToken = createResponse.extract().path("accessToken");
        //refreshToken = createResponse.extract().path("refreshToken");

        System.out.println("User created: " + createdUser.getEmail());
        System.out.println("AccessToken: " + accessToken);
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null && createdUser != null) {
            ValidatableResponse deleteResponse = userClient.delete(accessToken);
            check.deletedSuccessfully(deleteResponse);
            System.out.println("User deleted: " + createdUser.getEmail());
        } else {
            System.out.println("Cannot delete user - missing token or user data");
        }
    }

    @DisplayName("Логин под существующим пользователем - успешная авторизация")
    @Test
    public void loginWithExistingUserReturnSuccess() {
        ValidatableResponse loginResponse = userClient.login(createdUser);
        check.loginSuccessfully(loginResponse, createdUser);
    }

    @DisplayName("Логин с неверным логином - ожидается ошибка")
    @Test
    public void loginWithInvalidEmailReturnUnauthorized() {
        User userWithInvalidEmail = User.userWithInvalidEmail(createdUser);

        ValidatableResponse loginResponse = userClient.login(userWithInvalidEmail);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин с неверным паролем - ожидается ошибка")
    @Test
    public void loginWithInvalidPasswordReturnUnauthorized() {
        User userWithInvalidPassword = User.userWithInvalidPassword(createdUser);

        ValidatableResponse loginResponse = userClient.login(userWithInvalidPassword);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин с несуществующим пользователем - ожидается ошибка")
    @Test
    public void loginWithNonExistentUserReturnUnauthorized() {
        User nonExistentUser = User.randomUser();

        ValidatableResponse loginResponse = userClient.login(nonExistentUser);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин без пароля - ожидается ошибка")
    @Test
    public void loginWithoutPasswordReturnUnauthorized() {
        User userWithoutPassword = new User(createdUser.getEmail(), null, createdUser.getName());

        ValidatableResponse loginResponse = userClient.login(userWithoutPassword);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин без email - ожидается ошибка")
    @Test
    public void loginWithoutEmailReturnUnauthorized() {
        User userWithoutEmail = new User(null, createdUser.getPassword(), createdUser.getName());

        ValidatableResponse loginResponse = userClient.login(userWithoutEmail);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин с пустым email - ожидается ошибка")
    @Test
    public void loginWithEmptyEmailReturnUnauthorized() {
        User userWithEmptyEmail = new User("", createdUser.getPassword(), createdUser.getName());

        ValidatableResponse loginResponse = userClient.login(userWithEmptyEmail);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }

    @DisplayName("Логин с пустым паролем - ожидается ошибка")
    @Test
    public void loginWithEmptyPasswordReturnUnauthorized() {
        User userWithEmptyPassword = new User(createdUser.getEmail(), "", createdUser.getName());

        ValidatableResponse loginResponse = userClient.login(userWithEmptyPassword);
        check.loginFailedWithInvalidCredentials(loginResponse);
    }
}