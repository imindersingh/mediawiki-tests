package requests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class BaseRequestSpecification {
  public static final CookieFilter COOKIE_FILTER = new CookieFilter();
  private BaseRequestSpecification() { }

  public static RequestSpecification requestSpecification(final String baseUrl) {
    return new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .addFilter(COOKIE_FILTER)
            .addQueryParam("format", "json")
            .log(LogDetail.ALL)
            .build();
  }
}
