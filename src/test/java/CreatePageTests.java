import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import requests.BaseRequestSpecification;
import requests.EditPageRequests;
import requests.GetTokenRequests;
import requests.LoginRequests;
import requests.LogoutRequests;
import utils.Helper;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreatePageTests {
  private static final CookieFilter COOKIE_FILTER = new CookieFilter();
  private static RequestSpecification requestSpec;
  private static String csrfToken;

  @BeforeAll
  public static void setUp() {
    requestSpec = BaseRequestSpecification.requestSpecification("https://test.wikipedia.org/w/api.php", COOKIE_FILTER);
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER, "login", "logintoken");
    final Map<String, ?> loginFormParameters = new HashMap<>() {{
      put("lgpassword", "6ker6i5itf0rhm7mfi08vrrvtjmfcnsg");
      put("lgtoken", loginToken);
      put("lgname", "Mytestuser12345@Mytestuserbot12345");
    }};
    LoginRequests.login(requestSpec, COOKIE_FILTER, loginFormParameters);
    csrfToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER, "csrf", "csrftoken");
  }
  @AfterAll
  public static void tearDown() {
    LogoutRequests.post(requestSpec, COOKIE_FILTER, csrfToken)
        .then()
        .assertThat()
        .statusCode(200);
  }
  @Test
  void givenValidCsrfTokenThenCanCreatePage() {
    final String pageTitle = Helper.getRandomAlphanumeric(15);

    final Map<String, ?> createPage = new HashMap<>() {{
      put("title", pageTitle);
      put("text", "This is my new page 123!");
      put("summary", "my summary");
      put("baserevid", "1234567");
      put("createonly", "1");
      put("curtimestamp", "1");
      put("token", csrfToken);
    }};

    Response createPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, createPage);

    createPageResponse.then().assertThat()
        .statusCode(200)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));
  }

  @ParameterizedTest
  @ValueSource(strings = {"5a469c984f28f000bacd4908acd187dd62fe3492+\\", "  ", "invalid"})
  void givenInvalidCsrfTokenThenCannotCreatePage(final String token) {
    final String pageTitle = Helper.getRandomAlphanumeric(15);

    final Map<String, ?> createPage = new HashMap<>() {{
      put("title", pageTitle);
      put("text", "This is my new page 123!");
      put("summary", "my summary");
      put("baserevid", "1234567");
      put("createonly", "1");
      put("curtimestamp", "1");
      put("token", token);
    }};

    Response createPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, createPage);

    createPageResponse.then().assertThat()
        .statusCode(200)
        .body("error.code", equalTo("badtoken"))
        .body("error.info", equalTo("Invalid CSRF token."));
  }
}
