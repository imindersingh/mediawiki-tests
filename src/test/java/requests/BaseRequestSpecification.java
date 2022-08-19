package requests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public class BaseRequestSpecification {
  private static final String BASE_URL = "https://test.wikipedia.org/w/api.php";

  public static RequestSpecification requestSpecification(final String baseUrl, final CookieFilter cookieFilter) {
    return new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .addFilter(cookieFilter)
            .addQueryParam("format", "json")
            .log(LogDetail.ALL)
            .build();
  }
}
