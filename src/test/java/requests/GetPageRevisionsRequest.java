package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public final class GetPageRevisionsRequest {
  private GetPageRevisionsRequest() { }

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

  public static Response getPageRevisionByPageTitle(final RequestSpecification requestSpecification,
                                                    final String pageTitle,
                                                    final String pageId) {
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("prop", "revisions");
      put("titles", pageTitle);
      put("rvslots", "*");
      put("rvprop", "timestamp|content");
    }};

    Response getPageRevisionResponse = get(requestSpecification, queryParams);

    getPageRevisionResponse.then().assertThat()
            .statusCode(200)
            .body("query.pages", Matchers.hasKey(pageId))
            .body(String.format("query.pages.%s.title", pageId), equalTo(pageTitle));

    return getPageRevisionResponse;
  }
}
