package utils;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class Utils {

    public static void deleteElement(String url, String key, String token){
        given()
                .queryParam("key", key)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete(url)
                .then()
                .statusCode(200);
    }
}