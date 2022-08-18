import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.specification.RequestSpecification;
import requests.BaseRequestSpecification;
import requests.GetTokenRequests;
import requests.LoginRequests;
import requests.LogoutRequests;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginTests {

  private static final CookieFilter COOKIE_FILTER = new CookieFilter();
  private static RequestSpecification requestSpec;

  @BeforeAll
  public static void setUp() {
    requestSpec = BaseRequestSpecification.requestSpecification("https://test.wikipedia.org/w/api.php", COOKIE_FILTER);
  }

  @AfterAll
  public static void tearDown() {
    String csrfToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER, "csrf", "csrftoken");
    LogoutRequests.post(requestSpec, COOKIE_FILTER, csrfToken)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  void givenValidLoginTokenCanLoginSuccessfully() {
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER,"login", "logintoken");

    final Map<String, ?> body = new HashMap<>() {{
      put("lgpassword", "6ker6i5itf0rhm7mfi08vrrvtjmfcnsg");
      put("lgtoken", loginToken);
      put("lgname", "Mytestuser12345@Mytestuserbot12345");
    }};

    LoginRequests.post(requestSpec, COOKIE_FILTER, body)
        .then()
        .assertThat()
        .statusCode(200)
        .body("login.result", equalTo("Success"))
        .body("login.lguserid", equalTo(54882))
        .body("login.lgusername", equalTo("Mytestuser12345"));
  }
}
