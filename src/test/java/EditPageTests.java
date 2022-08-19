import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import io.restassured.filter.cookie.CookieFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import requests.BaseRequestSpecification;
import requests.EditPageRequests;
import requests.GetPageRevisionsRequest;
import requests.GetTokenRequests;
import requests.LoginRequests;
import requests.LogoutRequests;
import utils.Helper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class EditPageTests {
  private static final CookieFilter COOKIE_FILTER = new CookieFilter();
  private static RequestSpecification requestSpec;
  private static String csrfToken;

  @SuppressWarnings("checkstyle:WhitespaceAfter")
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
    csrfToken = GetTokenRequests.getTokenByName(requestSpec, COOKIE_FILTER,"csrf", "csrftoken");
  }

  @AfterAll
  public static void tearDown() {
    LogoutRequests.post(requestSpec, COOKIE_FILTER, csrfToken)
        .then()
        .assertThat()
        .statusCode(200);
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

    Response createPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, createPage);

    createPageResponse.then().assertThat()
        .statusCode(200)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));

    createPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, createPage);

    createPageResponse.then().assertThat()
        .statusCode(200)
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

    Response createPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, createPage);

    createPageResponse.then().assertThat()
        .statusCode(200)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));

    JsonPath body = new JsonPath(createPageResponse.body().asString());
    final String currentTimeStamp = body.getString("curtimestamp");
    final String pageId = body.getString("edit.pageid");

    final String baseTimeStamp = getBaseTimeStamp(pageTitle, pageId);
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

    Response editPageResponse = EditPageRequests.post(requestSpec, COOKIE_FILTER, editPageFormParams);

    editPageResponse.then().assertThat()
        .statusCode(200)
        .body("edit.result", equalTo("Success"))
        .body("edit.title", equalTo(pageTitle));

    Response getPageRevisions = getPageRevision(pageTitle, pageId);
    JsonPath getUpdatedPageRevisionsBody = new JsonPath(getPageRevisions.then().extract().asString());

    final String actualUpdatedText = getUpdatedPageRevisionsBody
        .getString(String.format("query.pages.%s.revisions[0].slots.main", pageId));

    Assertions.assertTrue(actualUpdatedText.contains(expectedUpdatedText));
  }

  private static String getBaseTimeStamp(String pageTitle, String pageId) {
    Response getPageRevisionResponse = getPageRevision(pageTitle, pageId);
    JsonPath body = new JsonPath(getPageRevisionResponse.then().extract().body().asString());
    return body.getString(String.format("query.pages.%s.revisions[0].timestamp"
        , pageId));
  }

  private static Response getPageRevision(String pageTitle, String pageId) {
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("prop", "revisions");
      put("titles", pageTitle);
      put("rvslots", "*");
      put("rvprop", "timestamp|content");
    }};

    Response getPageRevisionResponse = GetPageRevisionsRequest.get(requestSpec, COOKIE_FILTER, queryParams);

    getPageRevisionResponse.then().assertThat()
        .statusCode(200)
        .body("query.pages", Matchers.hasKey(pageId))
        .body(String.format("query.pages.%s.title", pageId), equalTo(pageTitle));

    return getPageRevisionResponse;
  }

  private static String getToken(final String type, final String tokenName) {
    final Map<String, ?> queryParams = new HashMap<>() {{
      put("action", "query");
      put("meta", "tokens");
      put("type", type);
    }};
    final String tokenPath = String.format("query.tokens.%s", tokenName);
    Response getTokenResponse = GetTokenRequests.get(requestSpec, COOKIE_FILTER, queryParams);

    return getTokenResponse.then().assertThat()
        .statusCode(200)
        .body(String.format("%s.size()", tokenPath), is(42))
        .extract().body().path(tokenPath);
  }

//  private static void login(final String loginToken) {
//    final Map<String, ?> loginFormParameters = new HashMap<>() {{
//      put("lgpassword", "6ker6i5itf0rhm7mfi08vrrvtjmfcnsg");
//      put("lgtoken", loginToken);
//      put("lgname", "Mytestuser12345@Mytestuserbot12345");
//    }};
//
//    Response loginResponse = PostLoginRequest.post(requestSpec, COOKIE_FILTER, loginFormParameters);
//
//    loginResponse
//        .then()
//        .assertThat()
//        .statusCode(200)
//        .body("login.result", equalTo("Success"))
//        .body("login.lguserid", equalTo(54882))
//        .body("login.lgusername", equalTo("Mytestuser12345"));
//  }
}
