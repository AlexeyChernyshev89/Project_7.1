package api.tests;

import api.TestBase;
import api.models.Courier;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest extends TestBase {

    @Test
    @DisplayName("Создание курьера с валидными данными")
    @Description("Проверка, что курьера можно создать с валидными данными")
    public void testCreateCourierSuccess() {
        Courier courier = new Courier(
                generateUniqueLogin(),
                generatePassword(),
                generateFirstName()
        );

        courierSteps.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Проверка, что нельзя создать двух курьеров с одинаковым логином")
    public void testCreateDuplicateCourier() {
        Courier courier = new Courier(
                generateUniqueLogin(),
                generatePassword(),
                generateFirstName()
        );

        // Создаем первого курьера
        courierSteps.createCourier(courier)
                .then()
                .statusCode(201);

        // Пытаемся создать второго с тем же логином
        courierSteps.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка, что нельзя создать курьера без логина")
    public void testCreateCourierWithoutLogin() {
        Courier courier = new Courier(
                null,
                generatePassword(),
                generateFirstName()
        );

        courierSteps.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка, что нельзя создать курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        Courier courier = new Courier(
                generateUniqueLogin(),
                null,
                generateFirstName()
        );

        courierSteps.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("Проверка, что можно создать курьера без имени (если имя необязательно)")
    public void testCreateCourierWithoutFirstName() {
        Courier courier = new Courier(
                generateUniqueLogin(),
                generatePassword(),
                null
        );

        courierSteps.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }
}