package org.example.testngApi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.dto.Player;
import org.example.testngApi.dto.PlayersList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

@Slf4j
@Epic("Player Management")
@Feature("Get All Players")
public class GetAllPlayerTest {
    PlayerClient playerClient;

    @BeforeMethod
    public void setup() {
        playerClient = new PlayerClient();
        log.info("PlayerClient initialized");
    }

    @Test(description = "Positive Test: Retrieve all players")
    @Description("Test verifies that API returns a list of players with correct status code")
    @Severity(SeverityLevel.NORMAL)
    public void getAllPlayers() throws IOException {
        Response response = sendGetAllPlayersRequest();

        validateStatusCode(response, 200);

        PlayersList playerList = parsePlayerList(response);

        validatePlayerList(playerList.getPlayers());
    }

    @Test(description = "Negative test: Invalid parameters for GET request /player/get/all")
    @Description("Test verifies that API returns 403 when requesting players with invalid parameters")
    @Severity(SeverityLevel.MINOR)
    public void getAllPlayersWithParams() {
        Player player = Player.builder()
                .login("InvalidUser")
                .build();

        Map<String, String> queryParams = player.toQueryParams();

        log.info("Sending invalid get-all request with params: {}", queryParams);
        Response response = playerClient.getAllPlayersWithParams(queryParams);

        assertEquals(response.getStatusCode(), 403, "Status code is not 403");
        log.info("Received expected status code 403 for invalid request");
    }

    @Step("Send GET /player/get/all request")
    private Response sendGetAllPlayersRequest() {
        log.info("Sending GET request to retrieve all players");
        Response response = playerClient.getAllPlayers();
        log.info("Received response: {}", response.getStatusCode());
        return response;
    }

    @Step("Validate response status code = {expected}")
    private void validateStatusCode(Response response, int expected) {
        assertEquals(response.getStatusCode(), expected, "Unexpected status code");
    }

    @Step("Parse response body into PlayersList")
    private PlayersList parsePlayerList(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PlayersList list = mapper.readValue(response.getBody().asString(), PlayersList.class);
        log.info("Parsed PlayersList with {} players", list.getPlayers().size());
        return list;
    }

    @Step("Validate all players' age is in range (17–59)")
    private void validatePlayerList(List<Player> players) {
        assertFalse(players.isEmpty(), "Player list is empty");

        for (Player player : players) {
            Integer age = player.getAge();
            assertNotNull(age, "Player age is null");

            log.info("Validating age for player ID {}: {}", player.getId(), age);

            assertTrue(age > 16, String.format("Player with ID %s has age <= 16: %d", player.getId(), age));
            assertTrue(age < 60, String.format("Player with ID %s has age >= 60: %d", player.getId(), age));
        }
    }
}
