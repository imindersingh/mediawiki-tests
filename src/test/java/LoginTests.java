import io.restassured.specification.RequestSpecification;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.BaseRequestSpecification;
import requests.GetTokenRequests;
import requests.LoginRequests;
import requests.LogoutRequests;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginTests {
  public static final String BASE_URL = "https://test.wikipedia.org/w/api.php";
  private static final int OK = 200;
  private static RequestSpecification requestSpec;

  @BeforeAll
  public static void setUp() {
    //requestSpec = BaseRequestSpecification.requestSpecification(BASE_URL, COOKIE_FILTER);
    requestSpec = BaseRequestSpecification.requestSpecification(BASE_URL);
  }

  @AfterAll
  public static void tearDown() {
    String csrfToken = GetTokenRequests.getTokenByName(requestSpec, "csrf", "csrftoken");
    LogoutRequests.post(requestSpec, csrfToken)
        .then()
        .assertThat()
        .statusCode(OK);
  }

  @Test
  void givenValidLoginTokenCanLoginSuccessfully() {
    final String loginToken = GetTokenRequests.getTokenByName(requestSpec, "login", "logintoken");
    final User user = User.builder()
            .name("Mytestuser12345@Mytestuserbot12345")
            .password("6ker6i5itf0rhm7mfi08vrrvtjmfcnsg")
            .userId(54882)
            .username("Mytestuser12345")
            .build();
    final Map<String, ?> body = new HashMap<>() {{
      put("lgpassword", user.password());
      put("lgtoken", loginToken);
      put("lgname", user.name());
    }};

    LoginRequests.post(requestSpec, body)
        .then()
        .assertThat()
        .statusCode(OK)
        .body("login.result", equalTo("Success"))
        .body("login.lguserid", equalTo(user.userId()))
        .body("login.lgusername", equalTo(user.username()));
  }
}
