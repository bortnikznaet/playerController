package org.example.testngApi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.utils.Utils;
import org.example.testngApi.dto.DeleteResponse;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

@Slf4j
@Epic("Player Management")
@Feature("Delete Player")
public class DeletePlayerTest {

    PlayerClient client = new PlayerClient();
    Utils utils = new Utils();

    @Test(description = "Positive Test: Delete a player Role with Supervisor")
    @Description("Test verifies that API allows deleting a player")
    @Severity(SeverityLevel.CRITICAL)
    public void deletePlayerTestPositive() throws IOException {
        String editor = utils.getLoginSupervision();
        String playerId = utils.getLastIdPlayer();

        Response response = sendDeleteRequest(playerId, editor);
        validateDeleteResponse(response);
    }

    @Test(description = "Negative Test: Sending DELETE with invalid player ID")
    @Description("Test verifies that API returns 403 when deleting non-existing player")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerTestNegative() throws IOException {
        String editor = utils.getLoginSupervision();
        String playerId = "***";

        log.info("Sending DELETE request with invalid playerId '{}'", playerId);
        Response response = client.deletePlayer(playerId, editor);

        log.info("Received status: {}, body: {}", response.getStatusCode(), response.getBody().asString());
        assertEquals(response.getStatusCode(), 403, "Expected status code 403 for invalid ID");
    }

    @Step("Send DELETE request for playerId {playerId} by editor {editor}")
    private Response sendDeleteRequest(String playerId, String editor) {
        log.info("Sending DELETE request for player ID '{}' by '{}'", playerId, editor);
        Response response = client.deletePlayer(playerId, editor);
        log.info("Received response with status: {}", response.getStatusCode());
        return response;
    }

    @Step("Validate DELETE response based on status")
    private void validateDeleteResponse(Response response) throws IOException {
        int statusCode = response.getStatusCode();
        log.info("Validating delete response with status: {}", statusCode);

        if (statusCode == 200) {
            log.info("Parsing response body for 200 OK");
            ObjectMapper mapper = new ObjectMapper();
            DeleteResponse deleteResponse = mapper.readValue(response.getBody().asString(), DeleteResponse.class);

            assertNotNull(deleteResponse, "Response body is null");
            assertNotNull(deleteResponse.getBody(), "Field 'body' is missing");
            assertEquals(deleteResponse.getStatusCode(), "100 CONTINUE", "Unexpected statusCode");
            assertEquals(deleteResponse.getStatusCodeValue(), 0, "Unexpected statusCodeValue");

        } else if (statusCode == 204) {
            log.info("Received 204 No Content");
            assertEquals(statusCode, 204, "Expected status code 204");
        } else {
            fail("Unexpected status code: " + statusCode);
        }
    }
}
