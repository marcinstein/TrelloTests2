package utils;

import base.BaseTest;

import static io.restassured.RestAssured.given;

public class Utils extends BaseTest {

    public static void deleteElement(String id){
        given()
                .spec(reqSpec)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + id)
                .then()
                .statusCode(200);
    }
}