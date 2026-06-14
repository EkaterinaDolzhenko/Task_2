package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.Orders.Orders;
import praktikum.Orders.OrdersChecker;
import praktikum.Orders.OrdersClient;
import praktikum.User.User;
import praktikum.User.UserChecker;
import praktikum.User.UserClient;

public class GetUserOrdersTest {

    private final UserClient userClient = new UserClient();
    private final UserChecker userChecker = new UserChecker();
    private final OrdersClient ordersClient = new OrdersClient();
    private final OrdersChecker ordersChecker = new OrdersChecker();

    private User createdUser;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        createdUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(createdUser);
        userChecker.createdSuccessfully(createResponse, createdUser);
        accessToken = createResponse.extract().path("accessToken");

        System.out.println("User created for orders test: " + createdUser.getEmail());
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null && createdUser != null) {
            try {
                ValidatableResponse deleteResponse = userClient.delete(accessToken);
                userChecker.deletedSuccessfully(deleteResponse);
                System.out.println("User deleted: " + createdUser.getEmail());
            } catch (Exception e) {
                System.err.println("Failed to delete user: " + e.getMessage());
            }
        }
    }

    @DisplayName("Получение заказов авторизованного пользователя без заказов")
    @Test
    public void getUserOrdersWithAuthAndNoOrdersReturnEmptyList() {
        ValidatableResponse response = ordersClient.getUserOrdersWithAuth(accessToken);
        ordersChecker.getUserOrdersEmpty(response);
    }

    @DisplayName("Получение заказов авторизованного пользователя с одним заказом")
    @Test
    public void getUserOrdersWithAuthAndOneOrderReturnSuccess() {
        Orders orders = Orders.withValidIngredients();
        ordersClient.createWithAuth(accessToken, orders);

        ValidatableResponse response = ordersClient.getUserOrdersWithAuth(accessToken);
        ordersChecker.getUserOrdersSuccessfully(response, 1);
    }

    @DisplayName("Получение заказов авторизованного пользователя с несколькими заказами")
    @Test
    public void getUserOrdersWithAuthAndMultipleOrdersReturnSuccess() {
        Orders firstOrder = Orders.withValidIngredients();
        ordersClient.createWithAuth(accessToken, firstOrder);

        Orders secondOrder = Orders.withOneIngredient();
        ordersClient.createWithAuth(accessToken, secondOrder);

        Orders thirdOrder = Orders.withValidIngredients();
        ordersClient.createWithAuth(accessToken, thirdOrder);

        ValidatableResponse response = ordersClient.getUserOrdersWithAuth(accessToken);
        ordersChecker.getUserOrdersSuccessfully(response, 3);
    }

    @DisplayName("Получение заказов неавторизованного пользователя - ожидается ошибка")
    @Test
    public void getUserOrdersWithoutAuthReturnUnauthorized() {
        ValidatableResponse response = ordersClient.getUserOrdersWithoutAuth();
        ordersChecker.getUserOrdersFailedWithoutAuth(response);
    }

    @DisplayName("Проверка, что заказы разных пользователей не перемешиваются")
    @Test
    public void getUserOrdersForDifferentUsersAreIsolated() {
        String firstUserToken = accessToken;

        Orders firstUserOrder1 = Orders.withValidIngredients();
        ordersClient.createWithAuth(firstUserToken, firstUserOrder1);

        Orders firstUserOrder2 = Orders.withOneIngredient();
        ordersClient.createWithAuth(firstUserToken, firstUserOrder2);

        User secondUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(secondUser);
        userChecker.createdSuccessfully(createResponse, secondUser);
        String secondUserToken = createResponse.extract().path("accessToken");

        Orders secondUserOrder = Orders.withValidIngredients();
        ordersClient.createWithAuth(secondUserToken, secondUserOrder);

        ValidatableResponse firstUserOrders = ordersClient.getUserOrdersWithAuth(firstUserToken);
        ValidatableResponse secondUserOrders = ordersClient.getUserOrdersWithAuth(secondUserToken);

        ordersChecker.verifyOrdersIsolation(firstUserOrders, secondUserOrders, 2, 1);

        userClient.delete(secondUserToken);
    }
}