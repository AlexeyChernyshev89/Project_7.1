package api.steps;

import api.models.Courier;
import api.models.CourierCredentials;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierSteps {

    @Step("Создание курьера с логином: {courier.login}")
    public Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин курьера с логином: {credentials.login}")
    public Response loginCourier(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера с ID: {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Получение ID курьера после логина")
    public int getCourierId(Response loginResponse) {
        return loginResponse.then().extract().path("id");
    }
}