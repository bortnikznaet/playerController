package org.example.testngApi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.dto.Player;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Slf4j
@Epic("Player Management")
@Feature("Get Player by ID")
public class GetPlayerByPlayerIdTest {

    PlayerClient client;
    ObjectMapper mapper;

    @BeforeMethod
    public void setup() {
        client = new PlayerClient();
        mapper = new ObjectMapper();
    }

    @Test(description = "Positive Test: Get player by valid ID")
    @Description("Test verifies that API returns player details for valid ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPlayerByIdPositive() throws IOException {
        Player requestPlayer = Player.builder().id(1).build();
        Map<String, String> queryParams = requestPlayer.toQueryParams();

        Response response = sendPostPlayerRequest(queryParams);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");

        Player responsePlayer = deserializePlayer(response);
        validateFullPlayerResponse(responsePlayer);
    }

    @Test(description = "Negative Test: Get player by invalid ID")
    @Description("Test verifies that API returns 403 for invalid player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerByInvalidId() throws IOException {
        Player player = Player.builder().id(0).build();
        Map<String, String> queryParams = player.toQueryParams();

        log.info("Sending request with invalid ID: {}", player.getId());
        Response response = client.postPlayer(queryParams);

        log.info("Received response with status: {}", response.getStatusCode());
        assertEquals(response.getStatusCode(), 403, "Expected status code 403 for invalid player ID");
    }

    @Step("Send POST /player/get with params {queryParams}")
    private Response sendPostPlayerRequest(Map<String, String> queryParams) {
        log.info("Sending POST to get player by ID with params: {}", queryParams);
        return client.postPlayer(queryParams);
    }

    @Step("Deserialize response to Player DTO")
    private Player deserializePlayer(Response response) throws IOException {
        String body = response.getBody().asString();
        log.info("Deserializing response: {}", body);
        return mapper.readValue(body, Player.class);
    }

    @Step("Validate player response contains all fields")
    private void validateFullPlayerResponse(Player player) {
        SoftAssert sa = new SoftAssert();

        sa.assertNotNull(player.getId(), "Field 'id' is missing");
        sa.assertNotNull(player.getAge(), "Field 'age' is missing");
        sa.assertNotNull(player.getGender(), "Field 'gender' is missing");
        sa.assertNotNull(player.getLogin(), "Field 'login' is missing");
        sa.assertNotNull(player.getPassword(), "Field 'password' is missing");
        sa.assertNotNull(player.getRole(), "Field 'role' is missing");
        sa.assertNotNull(player.getScreenName(), "Field 'screenName' is missing");

        log.info("All field presence validated successfully");
        sa.assertAll();
    }
}
