import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.GetTokenRequests;
import requests.EditPageRequests;
import requests.BaseRequestSpecification;
import requests.LoginRequests;
import requests.LogoutRequests;
import requests.GetPageRevisionsRequest;
import utils.Helper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreatePageTests {
  private static final String BASE_URL = "https://test.wikipedia.org/w/api.php";
  private static final int OK = 200;
  private static RequestSpecification requestSpec;
  private static String csrfToken;

  @BeforeAll
  public static void setUp() {
    requestSpec = BaseRequestSpecification.requestSpecification(BASE_URL);
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, "login", "logintoken");
    final User user = User.builder()
            .name("Mytestuser12345@Mytestuserbot12345")
            .password("6ker6i5itf0rhm7mfi08vrrvtjmfcnsg")
            .build();
    final Map<String, ?> loginFormParameters = new HashMap<>() {{
      put("lgpassword", user.password());
      put("lgtoken", loginToken);
      put("lgname", user.name());
    }};
    LoginRequests.login(requestSpec, loginFormParameters);
    csrfToken = GetTokenRequests.getTokenByName(requestSpec, "csrf", "csrftoken");
  }

  @AfterAll
  public static void tearDown() {
    LogoutRequests.post(requestSpec, csrfToken)
        .then()
        .assertThat()
        .statusCode(OK);
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

    Response createPageResponse = EditPageRequests.post(requestSpec, createPage);
    createPageResponse.then().assertThat()
        .statusCode(OK)
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

    Response createPageResponse = EditPageRequests.post(requestSpec, createPage);
    createPageResponse.then().assertThat()
        .statusCode(OK)
        .body("error.code", equalTo("badtoken"))
        .body("error.info", equalTo("Invalid CSRF token."));
  }
}
