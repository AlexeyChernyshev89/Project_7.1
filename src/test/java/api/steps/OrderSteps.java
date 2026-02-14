package api.steps;

import api.models.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа для клиента: {order.firstName} {order.lastName}")
    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }

    @Step("Получение списка заказов с параметрами")
    public Response getOrdersWithParams(Integer courierId, Integer limit, Integer page) {
        return given()
                .queryParam("courierId", courierId)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get("/api/v1/orders");
    }

    @Step("Отмена заказа с трек-номером: {track}")
    public Response cancelOrder(int track) {
        return given()
                .when()
                .put("/api/v1/orders/cancel?track=" + track);
    }

    @Step("Получение трек-номера из ответа")
    public int getTrack(Response response) {
        return response.then().extract().path("track");
    }
}