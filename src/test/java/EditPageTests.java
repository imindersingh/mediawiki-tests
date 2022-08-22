import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.*;
import utils.Helper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;

public class EditPageTests {
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
  void givenPageIsCreatedWhenCreateOnlyFlagIsSetThenCannotEdit() {
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

    createPageResponse = EditPageRequests.post(requestSpec, createPage);

    createPageResponse.then().assertThat()
        .statusCode(OK)
        .body("error.code", equalTo("articleexists"))
        .body("error.info", equalTo("The article you tried to create has been created already."));
  }

  @Test
  void givenPageCreatedThenCanEditSuccessfully() {
    final String pageTitle = Helper.getRandomAlphanumeric(20);

    final Map<String, ?> createPage = new HashMap<>() {{
      put("title", pageTitle);
      put("text", "This is my new page 123!");
      put("summary", "my summary");
      put("baserevid", "1234567");
      put("curtimestamp", "1");
      put("token", csrfToken);
    }};

    Response createPageResponse = EditPageRequests.post(requestSpec, createPage);

    createPageResponse.then().assertThat()
        .statusCode(OK)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));

    final JsonPath body = new JsonPath(createPageResponse.body().asString());
    final String currentTimeStamp = body.getString("curtimestamp");
    final String pageId = body.getString("edit.pageid");

    final String baseTimeStamp = Helper.getBaseTimeStamp(requestSpec, pageTitle, pageId);
    final String expectedUpdatedText = "EDIT!! This is my new page 123!";

    final Map<String, ?> editPageFormParams = new HashMap<>() {{
      put("title", pageTitle);
      put("text", expectedUpdatedText);
      put("summary", "my summary");
      put("baserevid", "1234567");
      put("basetimestamp", baseTimeStamp);
      put("starttimestamp", currentTimeStamp);
      put("curtimestamp", "1");
      put("token", csrfToken);
    }};

    Response editPageResponse = EditPageRequests.post(requestSpec, editPageFormParams);

    editPageResponse.then().assertThat()
        .statusCode(OK)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));

    Response getPageRevisions = GetPageRevisionsRequest.getPageRevisionByPageTitle(requestSpec, pageTitle, pageId);
    JsonPath getUpdatedPageRevisionsBody = new JsonPath(getPageRevisions.then().extract().asString());

    final String actualUpdatedText = getUpdatedPageRevisionsBody
        .getString(String.format("query.pages.%s.revisions[0].slots.main", pageId));

    Assertions.assertTrue(actualUpdatedText.contains(expectedUpdatedText));
  }
}
