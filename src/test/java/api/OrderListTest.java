package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListTest extends TestBase {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что возвращается список заказов")
    public void testGetOrderList() {
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("pageInfo", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("pageInfo.page", greaterThanOrEqualTo(0))
                .body("pageInfo.total", greaterThanOrEqualTo(0))
                .body("pageInfo.limit", greaterThan(0));
    }

    @Test
    @DisplayName("Получение списка заказов с лимитом")
    @Description("Проверка, что можно получить ограниченное количество заказов")
    public void testGetOrderListWithLimit() {
        int limit = 5;

        given()
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders.size()", lessThanOrEqualTo(limit))
                .body("pageInfo.limit", equalTo(limit));
    }

    @Test
    @DisplayName("Получение списка заказов с пагинацией")
    @Description("Проверка работы пагинации")
    public void testGetOrderListWithPagination() {
        int page = 0;
        int limit = 3;

        given()
                .queryParam("page", page)
                .queryParam("limit", limit)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("pageInfo.page", equalTo(page))
                .body("pageInfo.limit", equalTo(limit));
    }

    @Test
    @DisplayName("Получение списка заказов с несуществующим courierId")
    @Description("Проверка, что при запросе с несуществующим courierId возвращается ошибка")
    public void testGetOrderListWithNonExistentCourierId() {
        int nonExistentCourierId = 999999;

        given()
                .queryParam("courierId", nonExistentCourierId)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором " + nonExistentCourierId + " не найден"));
    }

    @Test
    @DisplayName("Получение списка заказов с существующим courierId")
    @Description("Проверка, что можно получить заказы для конкретного курьера")
    public void testGetOrderListWithExistingCourierId() {
        // Сначала создаем курьера
        String login = "courier_orders_" + System.currentTimeMillis();
        String password = "password123";
        String firstName = "Courier For Orders";

        int courierId = createCourier(login, password, firstName);

        given()
                .queryParam("courierId", courierId)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}