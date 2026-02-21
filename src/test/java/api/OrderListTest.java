package api.tests;

import api.TestBase;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class OrderListTest extends TestBase {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что возвращается список заказов")
    public void testGetOrderList() {
        orderSteps.getOrders()
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

        orderSteps.getOrdersWithParams(null, limit, null)
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

        orderSteps.getOrdersWithParams(null, limit, page)
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

        orderSteps.getOrdersWithParams(nonExistentCourierId, null, null)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором " + nonExistentCourierId + " не найден"));
    }
}