package api;

import api.steps.CourierSteps;
import api.steps.OrderSteps;
import com.github.javafaker.Faker;
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
import java.util.Locale;

public class TestBase {

    protected static String baseUri = "https://qa-scooter.praktikum-services.ru";
    protected List<Integer> courierIdsToDelete = new ArrayList<>();
    protected List<Integer> orderTracksToDelete = new ArrayList<>();

    protected static Faker faker;
    protected CourierSteps courierSteps;
    protected OrderSteps orderSteps;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = baseUri;
        faker = new Faker(new Locale("ru"));

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000)
                        .setParam("http.socket.timeout", 45000));

        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void prepareTest() {
        courierSteps = new CourierSteps();
        orderSteps = new OrderSteps();
        courierIdsToDelete.clear();
        orderTracksToDelete.clear();
    }

    @After
    public void cleanup() {
        // Удаление созданных курьеров
        for (Integer courierId : courierIdsToDelete) {
            try {
                courierSteps.deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Failed to delete courier: " + courierId);
            }
        }

        // Удаление созданных заказов
        for (Integer track : orderTracksToDelete) {
            try {
                orderSteps.cancelOrder(track);
            } catch (Exception e) {
                System.out.println("Failed to cancel order: " + track);
            }
        }
    }

    protected String generateUniqueLogin() {
        return faker.name().username() + "_" + System.currentTimeMillis();
    }

    protected String generatePassword() {
        return faker.internet().password(6, 10);
    }

    protected String generateFirstName() {
        return faker.name().firstName();
    }

    protected String generateLastName() {
        return faker.name().lastName();
    }

    protected String generateAddress() {
        return faker.address().streetAddress() + ", " + faker.address().buildingNumber();
    }

    protected String generatePhone() {
        return faker.phoneNumber().phoneNumber();
    }
}