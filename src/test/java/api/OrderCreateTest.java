package api.tests;

import api.TestBase;
import api.models.Order;
import api.models.OrderColor;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest extends TestBase {

    private final List<String> colors;

    public OrderCreateTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {List.of(OrderColor.BLACK.name())},           // Один цвет BLACK
                {List.of(OrderColor.GREY.name())},            // Один цвет GREY
                {List.of(OrderColor.BLACK.name(), OrderColor.GREY.name())}, // Оба цвета
                {List.of()}                                    // Без цвета
        });
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Параметризованный тест создания заказа с разными комбинациями цветов")
    public void testCreateOrderWithDifferentColors() {
        Order order = new Order(
                generateFirstName(),
                generateLastName(),
                generateAddress(),
                4,  // metroStation
                generatePhone(),
                5,  // rentTime
                "2024-12-31",
                "Тестовый комментарий",
                colors
        );

        int track = orderSteps.getTrack(orderSteps.createOrder(order));
        orderTracksToDelete.add(track);

        orderSteps.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}