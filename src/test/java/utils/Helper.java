package utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import requests.GetPageRevisionsRequest;

public final class Helper {
  public static String getRandomAlphanumeric(final int length) {
    return StringUtils.capitalize(RandomStringUtils.randomAlphanumeric(length));
  }

  public static String getBaseTimeStamp(final RequestSpecification requestSpecification,
                                        final String pageTitle,
                                        final String pageId) {
    Response getPageRevisionResponse = GetPageRevisionsRequest.getPageRevisionByPageTitle(requestSpecification, pageTitle, pageId);
    JsonPath body = new JsonPath(getPageRevisionResponse.then().extract().body().asString());
    return body.getString(String.format("query.pages.%s.revisions[0].timestamp", pageId));
  }
}
