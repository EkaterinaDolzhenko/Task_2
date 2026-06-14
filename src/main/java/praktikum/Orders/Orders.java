package praktikum.Orders;

import io.qameta.allure.Step;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Orders {
    private List<String> ingredients;

    public Orders(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Step("Создание заказа с валидными ингредиентами")
    public static Orders withValidIngredients() {
        // Реальные ID ингредиентов из запроса ингредиентов
        List<String> validIngredients = List.of(
                "61c0c5a71d1f82001bdaaa6d",  // Флюоресцентная булка R2-D3
                "609646e4dc916e00276b2870"   // Говяжий метеорит (отбивная)
        );
        return new Orders(validIngredients);
    }

    @Step("Создание заказа с одним ингредиентом")
    public static Orders withOneIngredient() {
        List<String> singleIngredient = List.of("61c0c5a71d1f82001bdaaa6c"); // Краторная булка N-200i
        return new Orders(singleIngredient);
    }

    @Step("Создание заказа без ингредиентов")
    public static Orders withoutIngredients() {
        return new Orders(null);
    }

    @Step("Создание заказа с пустым списком ингредиентов")
    public static Orders withEmptyIngredients() {
        return new Orders(List.of());
    }

    @Step("Создание заказа с неверным хешем ингредиентов")
    public static Orders withInvalidIngredientHash() {
        List<String> invalidIngredients = List.of("invalid_hash_123", "wrong_hash_456");
        return new Orders(invalidIngredients);
    }

    @Step("Создание заказа с частично неверным хешем")
    public static Orders withPartiallyInvalidIngredients() {
        List<String> mixedIngredients = List.of(
                "61c0c5a71d1f82001bdaaa77",  // валидный
                "invalid_hash_123"           // невалидный
        );
        return new Orders(mixedIngredients);
    }
}