package requests;

import java.util.Map;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginRequests {
  public static void login(final RequestSpecification requestSpecification,
                           final CookieFilter cookieFilter,
                           final Map<String, ?> formParams) {

    Response loginResponse = post(requestSpecification, cookieFilter, formParams);

    loginResponse
        .then()
        .assertThat()
        .statusCode(200)
        .body("login.result", equalTo("Success"))
        .body("login.lguserid", equalTo(54882))
        .body("login.lgusername", equalTo("Mytestuser12345"));
  }
  public static Response post(final RequestSpecification requestSpecification,
                              final CookieFilter cookieFilter,
                              final Map<String, ?> formParams) {
    return given(requestSpecification)
        .filter(cookieFilter)
        .queryParam("action", "login")
        .formParams(formParams)
        .when()
        .post().prettyPeek()
        .then()
        .extract()
        .response();
  }

}
