package support;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RestWrapper {
    private static final String BASE_URL = "https://skryabin.com/recruit/api/v1/";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON = "application/json";
    public static final String AUTH = "Authorization";

    private static String loginToken;
    private static Map<String, Object> lastPosition;
    private static Map<String, Object> lastCandidate;

    public static Map<String, Object> getLastPosition() {
        return lastPosition;
    }

    public static Map<String, Object> getLastCandidate() {
        return lastCandidate;
    }

    public void login(Map<String, String> source) {
        Map<String, String> credentials = loginCredentials(source);
        Response response = loginRequest(credentials);

        if (response.statusCode() != 200) {
            Map<String, String> signup = new HashMap<>(source);
            signup.putIfAbsent("firstName", "Rest");
            signup.putIfAbsent("lastName", "Recruiter");

            Response createResponse = createCandidateRequest(signup);
            if (createResponse.statusCode() != 201) {
                signup.put("email", uniqueEmail(source.get("email")));
                createResponse = createCandidateRequest(signup);
            }
            createResponse.then().log().ifError().statusCode(201);

            response = loginRequest(loginCredentials(signup));
        }

        Map<String, Object> result = response
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");

        assertThat(result.get("authenticated")).isEqualTo(true);
        loginToken = "Bearer " + result.get("token");
    }

    public Map<String, Object> createPosition(Map<String, String> source) {
        Map<String, String> position = new HashMap<>(source);
        position.put("dateOpen", toIsoDate(position.get("dateOpen")));
        position.put("title", position.get("title") + "-" + timestamp());

        Map<String, Object> result = authRequest()
                .body(position)
                .when()
                .post("positions")
                .then()
                .log().ifError()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getMap("");

        lastPosition = result;
        return result;
    }

    public static List<Map<String, Object>> getPositions() {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .when()
                .get("positions")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("");
    }

    public static Map<String, Object> getPositionById(Object id) {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .when()
                .get("positions/" + id)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");
    }

    public Map<String, Object> updatePosition(Map<String, String> source, Object id) {
        Map<String, String> fields = new HashMap<>(source);
        Map<String, Object> result = authRequest()
                .body(fields)
                .when()
                .put("positions/" + id)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");

        lastPosition.putAll(result);
        return result;
    }

    public static void deletePositionById(Object id) {
        RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .header(AUTH, loginToken)
                .when()
                .delete("positions/" + id)
                .then()
                .log().ifError()
                .statusCode(204);
    }

    public Map<String, Object> createCandidate(Map<String, String> source) {
        Map<String, String> candidate = new HashMap<>(source);
        candidate.put("email", uniqueEmail(candidate.get("email")));
        Map<String, Object> result = createCandidateRequest(candidate)
                .then()
                .log().ifError()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getMap("");

        result.remove("password");
        lastCandidate = result;
        return result;
    }

    public static List<Map<String, Object>> getCandidates() {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .when()
                .get("candidates")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("");
    }

    public static Map<String, Object> getCandidateById(Object id) {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .when()
                .get("candidates/" + id)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");
    }

    public Map<String, Object> updateCandidate(Map<String, String> source, Object id) {
        Map<String, String> fields = new HashMap<>(source);
        Map<String, Object> result = authRequest()
                .body(fields)
                .when()
                .patch("candidates/" + id)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("");

        result.remove("password");
        lastCandidate.putAll(result);
        return result;
    }

    public static void deleteCandidateById(Object id) {
        RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .header(AUTH, loginToken)
                .when()
                .delete("candidates/" + id)
                .then()
                .log().ifError()
                .statusCode(204);
    }

    private Response loginRequest(Map<String, String> credentials) {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .header(CONTENT_TYPE, JSON)
                .body(credentials)
                .when()
                .post("login");
    }

    private Response createCandidateRequest(Map<String, String> candidate) {
        RequestSpecification request = RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .header(CONTENT_TYPE, JSON);

        if (loginToken != null) {
            request.header(AUTH, loginToken);
        }

        return request
                .body(candidate)
                .when()
                .post("candidates");
    }

    private RequestSpecification authRequest() {
        return RestAssured
                .given()
                .log().ifValidationFails()
                .baseUri(BASE_URL)
                .header(CONTENT_TYPE, JSON)
                .header(AUTH, loginToken);
    }

    private static Map<String, String> loginCredentials(Map<String, String> source) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", source.get("email"));
        credentials.put("password", source.get("password"));
        return credentials;
    }

    private static String uniqueEmail(String email) {
        int at = email.indexOf('@');
        if (at < 0) {
            return email;
        }
        return email.substring(0, at) + "+" + timestamp() + email.substring(at);
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());
    }
    private static String toIsoDate(String date) {
        if (date == null) {
            return null;
        }

        for (String pattern : new String[]{"MM/dd/yyyy", "yyyy-MM-dd"}) {
            try {
                Date parsed = new SimpleDateFormat(pattern).parse(date);
                return new SimpleDateFormat("yyyy-MM-dd").format(parsed);
            } catch (ParseException ignored) {
                // Try the next supported pattern.
            }
        }
        return date;
    }
}
