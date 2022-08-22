package requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class EditPageRequests {
  private EditPageRequests() { }

  public static Response post(final RequestSpecification requestSpecification,
                              final Map<String, ?> formParams) {
    return given(requestSpecification)
        .filter(BaseRequestSpecification.COOKIE_FILTER)
        .queryParam("action", "edit")
        .formParams(formParams)
        .when()
        .post().prettyPeek()
        .then()
        .extract()
        .response();
  }
}
