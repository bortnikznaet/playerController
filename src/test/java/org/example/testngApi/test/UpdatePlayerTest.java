package org.example.testngApi.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.utils.Preconditions;
import org.example.testngApi.utils.Utils;
import org.example.testngApi.dto.Player;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

@Slf4j
@Epic("Player Management")
@Feature("Update Player Info")
public class UpdatePlayerTest {

    Utils utils;
    PlayerClient client;
    ObjectMapper mapper;
    Player updatePlayer;

    @BeforeMethod
    public void setup() {
        utils = new Utils();
        client = new PlayerClient();
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        updatePlayer = Preconditions.buildUpdatePlayerInfo();

        log.info("Setup complete: utils, client, player data initialized");
    }

    @Test(description = "Positive Test: Update player info with Supervisor role")
    @Description("Checks that a player can be updated by a supervisor with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void positiveTestUpdateInfo() throws IOException {
        String editor = utils.getLoginSupervision();
        String playerId = utils.getLastIdPlayer();

        String bodyJson = prepareUpdateBody(updatePlayer);
        System.out.println(bodyJson);

        Response response = sendUpdateRequest(editor, playerId, bodyJson);

        validateUpdateResponse(response, playerId);
    }

    @Test(description = "Negative Test: Update with invalid player ID")
    @Description("Ensures the system returns 403 when trying to update a non-existent player")
    @Severity(SeverityLevel.NORMAL)
    public void negativeTestUpdateInfo() throws IOException {
        String editor = utils.getLoginSupervision();
        String invalidPlayerId = "-1";
        String bodyJson = prepareUpdateBody(updatePlayer);

        Response response = sendUpdateRequest(editor, invalidPlayerId, bodyJson);

        log.info("Received response status: {}", response.getStatusCode());
        assertEquals(response.getStatusCode(), 403, "Expected status code 403 for invalid ID");
    }

    @Step("Prepare JSON body for update")
    private String prepareUpdateBody(Player player) throws IOException {
        String json = mapper.writeValueAsString(player);
        log.info("Serialized update player body: {}", json);
        return json;
    }

    @Step("Send PUT request to update player with ID {playerId}")
    private Response sendUpdateRequest(String editor, String playerId, String bodyJson) {
        log.info("Sending update request for player ID {} by editor {}", playerId, editor);
        Response response = client.updatePlayer(editor, playerId, bodyJson);
        log.info("Received response with status: {}", response.getStatusCode());
        return response;
    }

    @Step("Validate update response matches request")
    private void validateUpdateResponse(Response response, String expectedId) {
        assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        JsonPath jsonPath = response.jsonPath();
        SoftAssert sa = new SoftAssert();

        sa.assertEquals(jsonPath.getString("age"), updatePlayer.getAge().toString(), "Age mismatch");
        sa.assertEquals(jsonPath.getString("gender"), updatePlayer.getGender(), "Gender mismatch");
        sa.assertEquals(jsonPath.getString("login"), updatePlayer.getLogin(), "Login mismatch");
        sa.assertEquals(jsonPath.getString("role"), updatePlayer.getRole(), "Role mismatch");
        sa.assertEquals(jsonPath.getString("screenName"), updatePlayer.getScreenName(), "ScreenName mismatch");
        sa.assertEquals(jsonPath.getString("id"), expectedId, "ID mismatch");

        log.info("Update validation complete");
        sa.assertAll();
    }
}
