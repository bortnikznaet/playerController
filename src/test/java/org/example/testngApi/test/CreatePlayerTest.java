package org.example.testngApi.test;

import io.qameta.allure.*;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.dto.Player;

import static org.testng.Assert.assertEquals;

import org.example.testngApi.utils.Preconditions;
import org.example.testngApi.utils.Utils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Epic("Player Management")
@Feature("Create Player")
public class CreatePlayerTest {

    PlayerClient client = new PlayerClient();
    Utils utils = new Utils();
    ObjectMapper objectMapper = new ObjectMapper();
    String editor;

    @Test(description = "Positive Test: Create a new player")
    @Description("Test verifies that API allows creating a new player and returns status 200")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreatePlayerPositive() throws IOException {
        editor = utils.getLoginSupervision();

        Player newPlayer = Preconditions.buildValidPlayer();
        Map<String, String> queryParams = newPlayer.toQueryParams();

        Response response = sendCreatePlayerRequest(queryParams, editor);
        assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        Player result = objectMapper.readValue(response.getBody().asString(), Player.class);
        validatePlayerData(result, newPlayer);
    }

    @Test(description = "Negative Test: Creating a player with role 'user'")
    @Description("Test verifies that a player with role 'user' is not allowed to create a new player")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerNegative() throws IOException {

        editor = createTempPlayerRoleUser();

        Player newPlayer = Preconditions.buildInvalidPlayer();

        Map<String, String> queryParams = newPlayer.toQueryParams();

        log.info("Sending request to create player with invalid data: {}", newPlayer);
        Response response = client.createPlayer(queryParams, editor);
        assertEquals(response.getStatusCode(), 403, "Expected status code 403 for invalid player");

        deleteTempPlayerRoleUser();
    }


    @Step("Send request to create player")
    private Response sendCreatePlayerRequest(Map<String, String> queryParams, String editor) {
        log.info("Sending request to create player with editor: {}", editor);
        Response response = client.createPlayer(queryParams, editor);
        log.info("Received response: status {}, body {}", response.getStatusCode(), response.getBody().asString());
        return response;
    }

    @Step("Validate created player matches expected data")
    private void validatePlayerData(Player actual, Player expected) {
        SoftAssert softAssert = new SoftAssert();

        log.info("Validating player data: actual={}, expected={}", actual.toString(), expected.toString());
        softAssert.assertEquals(actual.getLogin(), expected.getLogin(), "Login mismatch");
        softAssert.assertEquals(actual.getPassword(), expected.getPassword(), "Password mismatch");
        softAssert.assertEquals(actual.getAge(), expected.getAge(), "Age mismatch");
        softAssert.assertEquals(actual.getRole(), expected.getRole(), "Role mismatch");
        softAssert.assertEquals(actual.getGender(), expected.getGender(), "Gender mismatch");
        softAssert.assertEquals(actual.getScreenName(), expected.getScreenName(), "ScreenName mismatch");

        log.info("Validation complete. Soft asserts executed.");
        softAssert.assertAll();
    }

    @Step("Create temp Player with Role User")
    private String createTempPlayerRoleUser() {
        Player tmpPlayer = Preconditions.buildPlayerRoleUser();
        Map<String, String> queryParams = tmpPlayer.toQueryParams();
        sendCreatePlayerRequest(queryParams, editor);
        return tmpPlayer.getLogin();
    }

    @Step("Delete temp Player with Role User")
    private void deleteTempPlayerRoleUser() throws IOException {
        editor = utils.getLoginSupervision();
        String playerId = utils.getLastIdPlayer();
        client.deletePlayer(playerId, editor);
    }
}
