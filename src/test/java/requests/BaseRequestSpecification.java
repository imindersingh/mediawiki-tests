package requests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class BaseRequestSpecification {
  private BaseRequestSpecification() { }
  public static RequestSpecification requestSpecification(final String baseUrl, final CookieFilter cookieFilter) {
    return new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .addFilter(cookieFilter)
            .addQueryParam("format", "json")
            .log(LogDetail.ALL)
            .build();
  }
}
