package trello;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.Utils;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class OrganizationTest extends BaseTest {
    private static String organizationId;

    private static Stream<Arguments> createOrganizationData() {
        return Stream.of(
                Arguments.of("Display Name", "Team description", "teamname", "https://website.team.com"),
                Arguments.of("Display Name", "Team description", "teamname", "http://website.team.com"),
                Arguments.of("Display Name", "Team description", "ttt", "http://website.team.com"),
                Arguments.of("Display Name", "Team description", "team_name", "http://website.team.com"),
                Arguments.of("Display Name", "Team description", "teamname123", "https://website.team.com"),
                Arguments.of("Display Name", "Team description", "teamname123", "http://website.team.com"),
                Arguments.of("Display Name", "", "teamname123", "http://website.team.com")
        );
    }

    private static Stream<Arguments> createOrganizationInvalidData() {
        return Stream.of(
                Arguments.of("", "Team description", "teamname", "https://website.team.com"),
                Arguments.of(" Start with space", "Team description", "teamname", "http://website.team.com"),
                Arguments.of("End with space ", "Team description", "!@#$%", "http://website.team.com")
//                ,
//                Arguments.of("Display Name", "Team description", "space space", "http://website.team.com"),
//                Arguments.of("Display Name", "Team description", "1", "http://website.team.com"),
//                Arguments.of("Display Name", "Team description", "teamname", "fake"),
//                Arguments.of("Display Name", "Team description", "teamname", "!@#$%")
        );
    }

    private static Stream<Arguments> updateOrganizationInvalidData() {
        return Stream.of(
                Arguments.of("", "updatedname", "https://updated.website"),
                Arguments.of(" Start with space", "updatedname", "https://updated.website"),
                Arguments.of("End with space ", "updatedname", "https://updated.website"),
                Arguments.of("Updated Display Name", "!@#$%", "https://updated.website"),
                Arguments.of("Updated Display Name", "1", "https://updated.website"),
                Arguments.of("Updated Display Name", "CAPITAL", "https://updated.website"),
                Arguments.of("Updated Display Name", "", "https://updated.website"),
                Arguments.of("Updated Display Name", "space space", "https://updated.website")
//                ,
//                Arguments.of("Updated Display Name", "updatedname", "!@#$%"),
//                Arguments.of("Updated Display Name", "updatedname", "!@#$%")
        );
    }


    @DisplayName("Create organization with valid data")
    @ParameterizedTest(name = "Display name: {0}, desc: {1}, name: {2}, website {3} ")
    @MethodSource("createOrganizationData")
    public void createOrganization(String displayname, String desc, String name, String website) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", displayname)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("displayName")).isEqualTo(displayname);
        assertThat(json.getString("desc")).isEqualTo(desc);
        assertThat(json.getString("name")).startsWith(name);
        assertThat(json.getString("website")).isEqualTo(website);

        organizationId = json.getString("id");

        Utils.deleteElement(organizationId);
    }

    @DisplayName("Create organization with invalid data")
    @ParameterizedTest(name = "Display name: {0}, desc: {1}, name: {2}, website {3} ")
    @MethodSource("createOrganizationInvalidData")
    public void createOrganizationInvalidData(String displayname, String desc, String name, String website) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", displayname)
                .queryParam("desc", desc)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(400)
                .extract()
                .response();
    }

    @DisplayName("Update organization with invalid data")
    @ParameterizedTest(name = "Display name: {0}, name: {1}, website {2} ")
    @MethodSource("updateOrganizationInvalidData")
    public void updateOrganizationInvalidData(String displayname, String name, String website) {

        Response response = given()
                .spec(reqSpec)
                .queryParam("displayName", "Display to edit")
                .queryParam("desc", "Description to edit")
                .queryParam("name", "nametoedit")
                .queryParam("website", "https://email.to.edit")
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(200)
                .extract()
                .response();


        JsonPath json = response.jsonPath();
        organizationId = json.getString("id");

        given()
                .spec(reqSpec)
                .queryParam("displayName", displayname)
                .queryParam("name", name)
                .queryParam("website", website)
                .when()
                .put(BASE_URL + "/" + ORGANIZATIONS + "/" + organizationId)
                .then()
                .statusCode(400);

        Utils.deleteElement(organizationId);

    }

}