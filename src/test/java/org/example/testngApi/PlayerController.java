package org.example.testngApi;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class PlayerController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    private static String BASE_URL;
    private static int THREAD_COUNT;
    public List<Long> listIdPlayer;
    public String listLoginPlayer;

    @BeforeClass
    public void setup() {
        loadConfig();
        RestAssured.baseURI = BASE_URL;
        logger.info("Base URI set: {}", BASE_URL);
    }

    private void loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fileInputStream);
            BASE_URL = properties.getProperty("base.url");
            THREAD_COUNT = Integer.parseInt(properties.getProperty("thread.count", "3"));
        } catch (IOException e) {
            logger.error("Configuration loading error", e);
            throw new RuntimeException("Failed to load configuration");
        }
    }

    @Test(priority = 1, description = "Positive Test: Retrieve all players")
    @Feature("Players")
    @Description("Test verifies that API returns a list of players with correct status code")
    public void getAllPlayers() {
        logAndStep("Sending GET request to /getAllPlayers");
        Response response = getAllPlayersRequest();
        listIdPlayer = response.jsonPath().getList("players.id", Long.class);
        logAndStep("Validating that response body contains a list of players");
        assertFalse(response.getBody().asString().isEmpty(), "Player list is empty");
    }

    @Step("Sending GET request to fetch all players")
    private Response getAllPlayersRequest() {
        logAndStep("Executing GET request on /getAllPlayers");
        return given().when().get("/player/get/all").then().extract().response();
    }

    @Test(priority = 2, description = "Negative Test: Invalid parameters for retrieving players")
    @Feature("Players")
    @Description("Test verifies that API returns 403 when requesting players with invalid parameters")
    public void testGetAllPlayersNegative() {
        logAndStep("Sending GET request with invalid parameters");
        Response response = given()
                .queryParam("login", "InvalidUser")
                .when()
                .get("/player/get/all")
                .then()
                .extract()
                .response();
        logAndStep("Validating that response status code is 403");
        assertEquals(response.getStatusCode(), 403, "Expected status code 403");
    }

    @Test(priority = 3, description = "Positive Test: Create a new player")
    @Feature("Players")
    @Description("Test verifies that API allows creating a new player and returns status 200")
    public void testCreatePlayerPositive() {
        logAndStep("Sending request to create a new player");
        Response response = given()
                .queryParam("age", "30")
                .queryParam("gender", "male")
                .queryParam("login", "admin")
                .queryParam("password", "password123")
                .queryParam("role", "user")
                .queryParam("screenName", "Admin")
                .when()
                .get("/player/create/Generated_login34.06.1139")
                .then().extract().response();
        logAndStep("Validating that response status code is 200");
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
    }

    @Test(priority = 4, description = "Negative Test: Creating player without a required parameter")
    @Feature("Players")
    @Description("Test verifies that API returns an error when creating a player without a required parameter")
    public void testCreatePlayerNegative() {
        logAndStep("Sending request to create player with missing parameter");
        Response response = given()
                .queryParam("age", "25")
                .queryParam("gender", "")
                .queryParam("login", "InvalidUser")
                .queryParam("password", "password123")
                .queryParam("role", "player")
                .queryParam("screenName", "TestUser22")
                .when()
                .get("/player/create/admin")
                .then().extract().response();
        logAndStep("Validating that response status code is 400");
        assertEquals(response.getStatusCode(), 400, "Expected status code 400");
    }

    @Test(priority = 5, description = "Positive Test: Delete a player Role with Admin")
    @Feature("Players")
    @Description("Test verifies that API allows deleting a player")
    public void testDeletePlayer() {
        logAndStep("Sending request to delete a player");
        int playerId = listIdPlayer.get(listIdPlayer.size() - 1).intValue();
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"playerId\": "+playerId+"}")
                .when()
                .delete("/player/delete/admin")
                .then().extract().response();
        logAndStep("Validating that response status code is 200");
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
    }

    @Test(priority = 6, description = "Negative Test: Delete player Role with user")
    @Feature("Players")
    @Description("Test verifies that API returns an error when deleting a non-existing player")
    public void testDeleteNonExistingPlayer() {
        logAndStep("Sending request to delete a non-existing player");
        int playerId = listIdPlayer.size() > 0 ? listIdPlayer.get(listIdPlayer.size() - 2).intValue() : -1;
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"playerId\": "+ playerId+"}")
                .when()
                .delete("/player/delete/user")
                .then().extract().response();
        logAndStep("Validating that response status code is 403");
        assertEquals(response.getStatusCode(), 403, "Expected status code 403");
    }

    @Test(priority = 7, description = "Positive Test: Get info By PlayerId with valid player ID ")
    @Feature("Players")
    @Description("Test verifies that API allows get full information a player")
    public void testGetPlayerByIdPositive() {
        logAndStep("Sending POST request to /player/get with valid player ID");
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"playerId\": 1}")
                .when()
                .post("/player/get");
        logAndStep("Validating that response status code is 200 and player exists");
        response.then().statusCode(200);
    }

    @Test(priority = 8, description = "Negative Test: Sending POST  with invalid player ID")
    @Feature("Players")
    @Description("Test verifies that API returns an error when  a non-existing player")
    public void testGetPlayerByIdNegative() {
        logAndStep("Sending POST request to /player/get with invalid player ID");
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"playerId\": -1}")
                .when()
                .post("/player/get");
        logAndStep("Validating that response status code is 404 and player does not exist");
        response.then().statusCode(404);
    }

    @Test(priority = 9, description = "Positive Test: Update information players with role Supervisor ")
    @Feature("Players")
    public void testUpdateInfoPositive() {
        logAndStep("Sending PATCH request to update/user/1 for update information supervisor ");
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"age\": 999,\n" +
                        "  \"gender\": \"male\",\n" +
                        "  \"login\": \"mazzy\",\n" +
                        "  \"password\": \"12345678Abc\",\n" +
                        "  \"role\": \"supervisor\",\n" +
                        "  \"screenName\": \"GreenMazzyMonster\"}")
                .when()
                .patch("player/update/Generated_login34.06.1139/"+listIdPlayer.get(listIdPlayer.size() - 1).intValue());
        logAndStep("Validating that response status code is 200 and player exists");
        response.then().statusCode(200);
    }

    @Test(priority = 10, description = "Negative Test: Update information players invalid id Player ")
    @Feature("Players")
    public void testUpdateInfoNegative() {
        logAndStep("Sending PATCH request to update/player/id for update information Player ");
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"age\": 55,\n" +
                        "  \"gender\": \"male\",\n" +
                        "  \"login\": \"madam\",\n" +
                        "  \"password\": \"@12345678Abc\",\n" +
                        "  \"role\": \"user\",\n" +
                        "  \"screenName\": \"LadyIce\"}")
                .when()
                .patch("player/update/Generated_login34.06.1139/-12");
        logAndStep("Validating that response status code is 403 and player exists");
        response.then().statusCode(403);
    }

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        XmlSuite suite = new XmlSuite();
        suite.setName("ParallelTestSuite");
        suite.setParallel(XmlSuite.ParallelMode.METHODS);
        suite.setThreadCount(THREAD_COUNT);

        XmlTest test = new XmlTest(suite);
        test.setName("Test Execution");
        test.setXmlClasses(Arrays.asList(new XmlClass(PlayerController.class)));

        testng.setXmlSuites(Arrays.asList(suite));
        testng.run();
    }

    private void logAndStep(String message) {
        logger.info(message);
        step(message);
    }
}
