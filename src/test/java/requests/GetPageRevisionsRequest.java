package requests;

import java.util.Map;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class GetPageRevisionsRequest {

  public static Response get(final RequestSpecification requestSpecification,
               final CookieFilter cookieFilter,
               final Map<String, ?> queryParams ) {

    return given(requestSpecification)
        .filter(cookieFilter)
        .queryParams(queryParams)
        .when()
        .get().prettyPeek()
        .then()
        .extract()
        .response();
  }
}
