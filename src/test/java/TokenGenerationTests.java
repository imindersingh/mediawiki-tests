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
  public static final String BASE_URL = "https://test.wikipedia.org/w/api.php";
  private static final int OK = 200;
  private static RequestSpecification requestSpec;

  @BeforeAll
  public static void setUp() {
    requestSpec = BaseRequestSpecification.requestSpecification(BASE_URL);
  }

  @AfterEach
  public void tearDown() {
    final Map<String, ?> getTokenQueryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", "csrf");
    }};

    String csrfToken = GetTokenRequests.get(requestSpec, getTokenQueryParams)
        .then()
        .assertThat()
        .statusCode(OK)
        .extract()
        .path("query.tokens.csrftoken");

    if (!csrfToken.equals("+\\")) {
      LogoutRequests.post(requestSpec, csrfToken)
          .then()
          .assertThat()
          .statusCode(OK);
    }
  }

  @Test
  void whenNotLoggedInThenOnlyCreateAccountAndLoginTokenAreReturned() {
    final String tokenType = "*";
    final Map<String, ?> getTokenQueryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", tokenType);
    }};

    final int validTokenLength = 42;
    final String defaultToken = "+\\";

    GetTokenRequests.get(requestSpec, getTokenQueryParams)
        .then()
        .assertThat()
        .statusCode(OK)
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
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, "login", "logintoken");
    final Map<String, ?> loginFormParameters = new HashMap<>() {{
      put("lgpassword", "6ker6i5itf0rhm7mfi08vrrvtjmfcnsg");
      put("lgtoken", loginToken);
      put("lgname", "Mytestuser12345@Mytestuserbot12345");
    }};
    LoginRequests.login(requestSpec, loginFormParameters);

    final String tokenType = "*";
    final Map<String, ?> getTokenQueryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", tokenType);
    }};

    final int validTokenLength = 42;

    GetTokenRequests.get(requestSpec, getTokenQueryParams)
        .then()
        .assertThat()
        .statusCode(OK)
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
