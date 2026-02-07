package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class TestBase {

    protected static String baseUri = "https://qa-scooter.praktikum-services.ru";
    protected List<Integer> courierIdsToDelete = new ArrayList<>();
    protected List<Integer> orderIdsToDelete = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = baseUri;

        // Правильная конфигурация для RestAssured 5.x
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 120000)     // 120 секунд на соединение
                        .setParam("http.socket.timeout", 180000));       // 180 секунд на чтение ответа         // 45 секунд на чтение ответа

        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void prepareTest() {
        courierIdsToDelete.clear();
        orderIdsToDelete.clear();
    }

    @After
    public void cleanup() {
        // Удаление созданных курьеров
        for (Integer courierId : courierIdsToDelete) {
            deleteCourier(courierId);
        }

        // Удаление созданных заказов
        for (Integer orderId : orderIdsToDelete) {
            cancelOrder(orderId);
        }
    }

    private void deleteCourier(Integer courierId) {
        try {
            given()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        } catch (Exception e) {
            System.out.println("Failed to delete courier: " + courierId);
        }
    }

    private void cancelOrder(Integer orderId) {
        try {
            given()
                    .put("/api/v1/orders/cancel?track=" + orderId)
                    .then()
                    .statusCode(200);
        } catch (Exception e) {
            System.out.println("Failed to cancel order: " + orderId);
        }
    }

    protected int createCourier(String login, String password, String firstName) {
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

        // Логинимся, чтобы получить ID
        String loginBody = String.format(
                "{\"login\": \"%s\", \"password\": \"%s\"}",
                login, password
        );

        int courierId = given()
                .header("Content-type", "application/json")
                .body(loginBody)
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        courierIdsToDelete.add(courierId);
        return courierId;
    }
}