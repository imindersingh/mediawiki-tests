package requests;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class LogoutRequests {

  public static Response post(final RequestSpecification requestSpecification,
                              final CookieFilter cookieFilter,
                              final String csrfToken) {
    return given(requestSpecification)
        .filter(cookieFilter)
        .queryParam("action", "logout")
        .formParam("token", csrfToken)
        .when()
        .post().prettyPeek()
        .then()
        .extract()
        .response();
  }

}
