package requests;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public final class LoginRequests {
  private LoginRequests() { }
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
  public static void login(final RequestSpecification requestSpecification,
                           final CookieFilter cookieFilter,
                           final Map<String, ?> formParams) {
    Response loginResponse = post(requestSpecification, cookieFilter, formParams);
    loginResponse.then().assertThat()
        .statusCode(200)
        .body("login.result", equalTo("Success"));
  }
}
