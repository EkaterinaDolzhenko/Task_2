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

public class CreateOrderTest {

    private final UserClient userClient = new UserClient();
    private final UserChecker userChecker = new UserChecker();
    private final OrdersClient ordersClient = new OrdersClient();
    private final OrdersChecker ordersChecker = new OrdersChecker();

    private User createdUser;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        // Создаем пользователя для тестов с авторизацией
        createdUser = User.randomUser();
        ValidatableResponse createResponse = userClient.create(createdUser);
        userChecker.createdSuccessfully(createResponse, createdUser);
        accessToken = createResponse.extract().path("accessToken");
    }

    @AfterEach
    public void cleanUp() {
        if (accessToken != null && createdUser != null) {
            ValidatableResponse deleteResponse = userClient.delete(accessToken);
            userChecker.deletedSuccessfully(deleteResponse);
            System.out.println("User deleted: " + createdUser.getEmail());
        }
    }

    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Test
    public void createOrderWithAuthAndValidIngredientsReturnSuccess() {
        Orders orders = Orders.withValidIngredients();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.createdSuccessfully(response);
    }

    @DisplayName("Создание заказа без авторизации с валидными ингредиентами")
    @Test
    public void createOrderWithoutAuthAndValidIngredientsReturnSuccess() {
        Orders orders = Orders.withValidIngredients();

        ValidatableResponse response = ordersClient.createWithoutAuth(orders);
        ordersChecker.createdWithoutAuthSuccessfully(response);
    }

    @DisplayName("Создание заказа с авторизацией и одним ингредиентом")
    @Test
    public void createOrderWithAuthAndOneIngredientReturnSuccess() {
        Orders orders = Orders.withOneIngredient();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.createdSuccessfully(response);
    }

    @DisplayName("Создание заказа с авторизацией без ингредиентов - ожидается ошибка")
    @Test
    public void createOrderWithAuthWithoutIngredientsReturnBadRequest() {
        Orders orders = Orders.withoutIngredients();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.creationFailedWithoutIngredients(response);
    }

    @DisplayName("Создание заказа с авторизацией и пустым списком ингредиентов - ожидается ошибка")
    @Test
    public void createOrderWithAuthAndEmptyIngredientsReturnBadRequest() {
        Orders orders = Orders.withEmptyIngredients();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.creationFailedWithoutIngredients(response);
    }

    @DisplayName("Создание заказа без авторизации без ингредиентов - ожидается ошибка")
    @Test
    public void createOrderWithoutAuthWithoutIngredientsReturnBadRequest() {
        Orders orders = Orders.withoutIngredients();

        ValidatableResponse response = ordersClient.createWithoutAuth(orders);
        ordersChecker.creationFailedWithoutIngredients(response);
    }

    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов - ожидается ошибка 500")
    @Test
    public void createOrderWithAuthAndInvalidHashReturnInternalServerError() {
        Orders orders = Orders.withInvalidIngredientHash();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.creationFailedWithInvalidHash(response);
    }

    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов - ожидается ошибка 500")
    @Test
    public void createOrderWithoutAuthAndInvalidHashReturnInternalServerError() {
        Orders orders = Orders.withInvalidIngredientHash();

        ValidatableResponse response = ordersClient.createWithoutAuth(orders);
        ordersChecker.creationFailedWithInvalidHash(response);
    }

    @DisplayName("Создание заказа с авторизацией и частично неверным хешем - ожидается ошибка 500")
    @Test
    public void createOrderWithAuthAndPartiallyInvalidHashReturnInternalServerError() {
        Orders orders = Orders.withPartiallyInvalidIngredients();

        ValidatableResponse response = ordersClient.createWithAuth(accessToken, orders);
        ordersChecker.creationFailedWithInvalidHash(response);
    }

    @DisplayName("Создание нескольких заказов одним пользователем")
    @Test
    public void createMultipleOrdersWithSameUserReturnSuccess() {
        Orders firstOrder = Orders.withValidIngredients();
        ValidatableResponse firstResponse = ordersClient.createWithAuth(accessToken, firstOrder);
        ordersChecker.createdSuccessfully(firstResponse);

        Orders secondOrder = Orders.withOneIngredient();
        ValidatableResponse secondResponse = ordersClient.createWithAuth(accessToken, secondOrder);
        ordersChecker.createdSuccessfully(secondResponse);
    }
}