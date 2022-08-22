package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public final class LogoutRequests {
  private LogoutRequests() { }

  public static Response post(final RequestSpecification requestSpecification,
                              final String csrfToken) {
    return given(requestSpecification)
        .filter(BaseRequestSpecification.COOKIE_FILTER)
        .queryParam("action", "logout")
        .formParam("token", csrfToken)
        .when()
        .post().prettyPeek()
        .then()
        .extract()
        .response();
  }
}
