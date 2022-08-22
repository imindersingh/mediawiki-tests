package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public final class GetTokenRequests {
  private GetTokenRequests() { }

  public static Response get(final RequestSpecification requestSpecification,
                             final Map<String, ?> queryParams) {
    return given(requestSpecification)
        .filter(BaseRequestSpecification.COOKIE_FILTER)
        .queryParams(queryParams)
        .when()
        .get().prettyPeek()
        .then()
        .extract()
        .response();
  }

  public static String getTokenByName(final RequestSpecification requestSpecification,
                                      final String type,
                                      final String tokenName) {
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", type);
    }};
    final String tokenPath = String.format("query.tokens.%s", tokenName);
    Response getTokenResponse = get(requestSpecification, queryParams);
    return getTokenResponse.then().assertThat()
        .statusCode(200)
        .body(String.format("%s.size()", tokenPath), is(42))
        .extract().body().path(tokenPath);
  }
}
