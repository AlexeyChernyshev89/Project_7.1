package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierTest extends TestBase {

    @Test
    @DisplayName("Создание курьера с валидными данными")
    @Description("Проверка, что курьера можно создать с валидными данными")
    public void testCreateCourierSuccess() {
        String login = "test_courier_" + System.currentTimeMillis();
        String password = "password123";
        String firstName = "Test Courier";

        String requestBody = String.format(
                "{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                login, password, firstName
        );

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать двух курьеров с одинаковым логином")
    public void testCreateDuplicateCourier() {
        String login = "duplicate_courier_" + System.currentTimeMillis();
        String password = "password123";
        String firstName = "Duplicate Courier";

        // Создаем первого курьера
        String requestBody = String.format(
                "{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                login, password, firstName
        );

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        // Пытаемся создать второго с тем же логином
        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка, что нельзя создать курьера без логина")
    public void testCreateCourierWithoutLogin() {
        String requestBody = "{\"password\": \"password123\", \"firstName\": \"No Login Courier\"}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        String requestBody = "{\"login\": \"nopassword\", \"firstName\": \"No Password Courier\"}";

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("Проверка, что можно создать курьера без имени (если имя необязательно)")
    public void testCreateCourierWithoutFirstName() {
        String login = "noname_courier_" + System.currentTimeMillis();
        String password = "password123";

        String requestBody = String.format(
                "{\"login\": \"%s\", \"password\": \"%s\"}",
                login, password
        );

        given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }
}