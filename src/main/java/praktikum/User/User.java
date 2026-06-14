package praktikum.User;

import io.qameta.allure.Step;

import java.util.concurrent.ThreadLocalRandom;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Step("Используем пользователя с рандомным логином")
    public static User randomUser(){
        var random = ThreadLocalRandom.current();
        String randomEmail = "test-" + random.nextInt() + "@yandex.ru";
        return new User(randomEmail, "password123", "FiTestUsersh");
    }

    @Step("Используем пользователя без email")
    public static User userWithoutEmail() {
        return new User(null, "password123", "TestUser");
    }

    @Step("Используем пользователя без password")
    public static User userWithoutPassword() {
        var random = ThreadLocalRandom.current();
        String randomEmail = "test-" + random.nextInt() + "@yandex.ru";
        return new User(randomEmail, null, "TestUser");
    }

    @Step("Используем пользователя без name")
    public static User userWithoutName() {
        var random = ThreadLocalRandom.current();
        String randomEmail = "test-" + random.nextInt() + "@yandex.ru";
        return new User(randomEmail, "password123", null);
    }

    @Step("Используем пользователя с пустым email")
    public static User userWithEmptyEmail() {
        return new User("", "password123", "TestUser");
    }

    @Step("Используем пользователя с пустым password")
    public static User userWithEmptyPassword() {
        var random = ThreadLocalRandom.current();
        String randomEmail = "test-" + random.nextInt() + "@yandex.ru";
        return new User(randomEmail, "", "TestUser");
    }

    @Step("Используем пользователя с пустым name")
    public static User userWithEmptyName() {
        var random = ThreadLocalRandom.current();
        String randomEmail = "test-" + random.nextInt() + "@yandex.ru";
        return new User(randomEmail, "password123", "");
    }

    @Step("Используем пользователя с неверным email для логина")
    public static User userWithInvalidEmail(User existingUser) {
        return new User("invalid_" + existingUser.getEmail(), existingUser.getPassword(), existingUser.getName());
    }

    @Step("Используем пользователя с неверным паролем для логина")
    public static User userWithInvalidPassword(User existingUser) {
        return new User(existingUser.getEmail(), "wrong_password_" + System.currentTimeMillis(), existingUser.getName());
    }

    @Step("Используем пользователя с обновленным email")
    public static User updatedUserWithNewEmail(User originalUser) {
        var random = ThreadLocalRandom.current();
        String newEmail = "updated-" + random.nextInt() + "@yandex.ru";
        return new User(newEmail, originalUser.getPassword(), originalUser.getName());
    }

    @Step("Используем пользователя с обновленным name")
    public static User updatedUserWithNewName(User originalUser) {
        return new User(originalUser.getEmail(), originalUser.getPassword(), "UpdatedName_" + System.currentTimeMillis());
    }

    @Step("Используем пользователя с обновленным password")
    public static User updatedUserWithNewPassword(User originalUser) {
        return new User(originalUser.getEmail(), "newPassword123", originalUser.getName());
    }
}