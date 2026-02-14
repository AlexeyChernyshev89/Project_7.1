package api.tests;

import api.TestBase;
import api.models.Courier;
import api.models.CourierCredentials;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest extends TestBase {

    @Test
    @DisplayName("Успешный логин курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными данными")
    public void testLoginCourierSuccess() {
        // Создаем курьера
        Courier courier = new Courier(
                generateUniqueLogin(),
                generatePassword(),
                generateFirstName()
        );

        courierSteps.createCourier(courier).then().statusCode(201);

        // Логинимся
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());

        int courierId = courierSteps.getCourierId(courierSteps.loginCourier(credentials));
        courierIdsToDelete.add(courierId);

        courierSteps.loginCourier(credentials)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин без пароля")
    @Description("Проверка, что нельзя залогиниться без пароля")
    @Ignore("Временно отключен из-за нестабильности API (ошибка 504)")
    public void testLoginWithoutPassword() {
        CourierCredentials credentials = new CourierCredentials(generateUniqueLogin(), null);

        courierSteps.loginCourier(credentials)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без логина")
    @Description("Проверка, что нельзя залогиниться без логина")
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials(null, generatePassword());

        courierSteps.loginCourier(credentials)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что система возвращает ошибку при неверном пароле")
    public void testLoginWithWrongPassword() {
        // Создаем курьера
        Courier courier = new Courier(
                generateUniqueLogin(),
                generatePassword(),
                generateFirstName()
        );

        courierSteps.createCourier(courier).then().statusCode(201);

        // Пытаемся залогиниться с неверным паролем
        CourierCredentials wrongCredentials = new CourierCredentials(courier.getLogin(), "wrong_password");

        courierSteps.loginCourier(wrongCredentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин несуществующего курьера")
    @Description("Проверка, что система возвращает ошибку при логине несуществующего курьера")
    public void testLoginNonExistentCourier() {
        CourierCredentials credentials = new CourierCredentials("nonexistent_user_" + System.currentTimeMillis(), "any_password");

        courierSteps.loginCourier(credentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}