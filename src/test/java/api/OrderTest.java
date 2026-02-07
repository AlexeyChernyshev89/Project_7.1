package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTest extends TestBase {

    private final String[] colors;

    public OrderTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {new String[]{"BLACK"}},           // Один цвет BLACK
                {new String[]{"GREY"}},            // Один цвет GREY
                {new String[]{"BLACK", "GREY"}},   // Оба цвета
                {new String[]{}}                   // Без цвета
        });
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Параметризованный тест создания заказа с разными комбинациями цветов")
    public void testCreateOrderWithDifferentColors() {
        String requestBody;

        if (colors.length == 0) {
            // Заказ без указания цвета
            requestBody = "{\"firstName\": \"Naruto\", " +
                    "\"lastName\": \"Uzumaki\", " +
                    "\"address\": \"Konoha, 142 apt.\", " +
                    "\"metroStation\": 4, " +
                    "\"phone\": \"+7 800 355 35 35\", " +
                    "\"rentTime\": 5, " +
                    "\"deliveryDate\": \"2024-12-31\", " +
                    "\"comment\": \"Saske, come back to Konoha\"}";
        } else {
            // Заказ с цветами
            String colorsJson = "\"" + String.join("\", \"", colors) + "\"";
            requestBody = String.format("{\"firstName\": \"Naruto\", " +
                    "\"lastName\": \"Uzumaki\", " +
                    "\"address\": \"Konoha, 142 apt.\", " +
                    "\"metroStation\": 4, " +
                    "\"phone\": \"+7 800 355 35 35\", " +
                    "\"rentTime\": 5, " +
                    "\"deliveryDate\": \"2024-12-31\", " +
                    "\"comment\": \"Saske, come back to Konoha\", " +
                    "\"color\": [%s]}", colorsJson);
        }

        int track = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");

        orderIdsToDelete.add(track);
    }
}