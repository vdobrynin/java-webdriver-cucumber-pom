package support;//package support;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RestWrapper {

    private static String baseUrl = "https://skryabin.com/recruit/api/v1/";
    private static String loginToken;
    private static Map<String, Object> lastPosition;


    public static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json";
    public static final String AUTH = "Authorization";

    public static Map<String, Object> getLastPosition() {

        return lastPosition;
    }

    public void login(Map<String, String> credentials) {

        // prepare
        RequestSpecification request = RestAssured
                .given()
                .log().all()
                .baseUri(baseUrl)
                .header(CONTENT_TYPE, JSON)
                .body(credentials);

        // execute
        Response response = request
                .when()
                //.post(baseUrl + "login");
                .post("login");

        //verify and extract
        Map<String, Object> result = response
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");

//    loginToken = "Bearer " + result.get("token".toString());
        loginToken = "Bearer " + result.get("token");
    }

    public Map<String, Object> createPosition(Map<String, String> position) {

        String dateOpen = position.get("dateOpen");
        String isoDateOpen = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dateOpen));
        position.put("dateOpen", isoDateOpen);

        String title = position.get("title");
        String uniqueTitle = title + new SimpleDateFormat("-yyyy-MM-dd-HH-mm-ss").format(new Date());
        position.put("title", uniqueTitle);

        //prepare
        RequestSpecification request = RestAssured
                .given()
                .log().all()
                .baseUri(baseUrl)
                .header(CONTENT_TYPE, JSON)
                .header(AUTH, loginToken)
                .body(position);

        //execute
        Response response = request.when()
                .when()
                //.post(baseUrl + "positions");
                .post("positions");

        //verify and extract
        Map<String, Object> result = response
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getMap("");

        lastPosition = result;
        return result;
    }

    public static List<Map<String, Object>> getPositions() {

        //prepare
        RequestSpecification request = RestAssured
                .given()
                .baseUri(baseUrl)
                .log().all();

        //execute
        Response response = request
                .when()
                .get("positions");

        //verify and extract
        List<Map<String, Object>> result = response
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("");

        return result;
    }
}
