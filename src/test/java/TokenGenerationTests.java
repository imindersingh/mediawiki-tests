import io.restassured.filter.cookie.CookieFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.BaseRequestSpecification;
import requests.GetTokenRequests;
import requests.LoginRequests;
import requests.LogoutRequests;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

public class TokenGenerationTests {
  private static final CookieFilter COOKIE_FILTER = new CookieFilter();
  public static final String BASE_URL = "https://test.wikipedia.org/w/api.php";
  private static RequestSpecification requestSpec;

  @BeforeAll
  public static void setUp() {
    requestSpec = BaseRequestSpecification.requestSpecification(BASE_URL, COOKIE_FILTER);
  }

  @AfterEach
  public void tearDown() {
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", "csrf");
    }};

    String csrfToken = GetTokenRequests.get(requestSpec, COOKIE_FILTER, queryParams)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .path("query.tokens.csrftoken");

    if (!csrfToken.equals("+\\")) {
      LogoutRequests.post(requestSpec, COOKIE_FILTER, csrfToken)
          .then()
          .assertThat()
          .statusCode(200);
    }
  }

  @Test
  void whenNotLoggedInThenCreateAccountAndLoginTokenAreAvailable() {
    final String tokenType = "*";
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", tokenType);
    }};

    final int validTokenLength = 42;
    final String defaultToken = "+\\";

    GetTokenRequests.get(requestSpec, COOKIE_FILTER, queryParams)
        .then()
        .assertThat()
        .statusCode(200)
        .body("query.tokens.size()", is(9))
        .body("query.tokens.createaccounttoken.size()", is(validTokenLength))
        .body("query.tokens.logintoken.size()", is(validTokenLength))
        .body("query.tokens.csrftoken", is(defaultToken))
        .body("query.tokens.deleteglobalaccounttoken", is(defaultToken))
        .body("query.tokens.patroltoken", is(defaultToken))
        .body("query.tokens.rollbacktoken", is(defaultToken))
        .body("query.tokens.setglobalaccountstatustoken", is(defaultToken))
        .body("query.tokens.userrightstoken", is(defaultToken))
        .body("query.tokens.watchtoken", is(defaultToken));
  }

  @Test
  void whenLoggedInThenCanGetAllTokens() {
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER, "login", "logintoken");
    final Map<String, ?> loginFormParameters = new HashMap<>() {{
      put("lgpassword", "6ker6i5itf0rhm7mfi08vrrvtjmfcnsg");
      put("lgtoken", loginToken);
      put("lgname", "Mytestuser12345@Mytestuserbot12345");
    }};
    LoginRequests.login(requestSpec, COOKIE_FILTER, loginFormParameters);

    final String tokenType = "*";
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", tokenType);
    }};

    final int validTokenLength = 42;

    GetTokenRequests.get(requestSpec, COOKIE_FILTER, queryParams)
        .then()
        .assertThat()
        .statusCode(200)
        .body("query.tokens.size()", is(9))
        .body("query.tokens.createaccounttoken.size()", is(validTokenLength))
        .body("query.tokens.logintoken.size()", is(validTokenLength))
        .body("query.tokens.csrftoken.size()", is(validTokenLength))
        .body("query.tokens.deleteglobalaccounttoken.size()", is(validTokenLength))
        .body("query.tokens.patroltoken.size()", is(validTokenLength))
        .body("query.tokens.rollbacktoken.size()", is(validTokenLength))
        .body("query.tokens.setglobalaccountstatustoken.size()", is(validTokenLength))
        .body("query.tokens.userrightstoken.size()", is(validTokenLength))
        .body("query.tokens.watchtoken.size()", is(validTokenLength));
  }
}
