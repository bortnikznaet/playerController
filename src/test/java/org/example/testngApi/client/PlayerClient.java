package org.example.testngApi.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Slf4j
public class PlayerClient {

    private static final String BASE_URL = "http://3.68.165.45";
    private static final String PLAYER_CREATION_URI = String.format("%s/player/create/%%s", BASE_URL);
    public static final String URL_STATUS_BODY_RESPONSE_MESSAGE = "Request to url '%s' response status '%d' with body %s";
    private static final String ALL_PLAYERS_URI = String.format("%s/player/get/all", BASE_URL);
    private static final String PLAYER_DELETION_URI = String.format("%s/player/delete/%%s", BASE_URL);
    private static final String PLAYER_RECEIVING_URI = String.format("%s/player/get", BASE_URL);
    private static final String PLAYER_UPDATE_URI = String.format("%s/player/update/%%s/%%s", BASE_URL);

    public Response createPlayer(Map<String, String> params, String editor) {
        String uri = String.format(PLAYER_CREATION_URI, editor);
        Response response = given()
                .queryParams(params)
                .get(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), params.toString()));
        return response;
    }

    public Response getAllPlayers() {
        String uri = ALL_PLAYERS_URI;
        Response response = given()
                .get(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), response.then().extract().body().toString()));
        return response;
    }

    public Response getAllPlayersWithParams(Map<String, String> params) {
        String uri = ALL_PLAYERS_URI;
        Response response = given()
                .queryParams(params)
                .get(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), params.toString()));
        return response;
    }

    public Response deletePlayer(String playerId, String editor) {
        String uri = String.format(PLAYER_DELETION_URI, editor);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .delete(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), playerId));
        return response;
    }

    public Response postPlayer(Map<String, String> playerId) {
        String uri = PLAYER_RECEIVING_URI;
        Response response = given()
                .contentType(ContentType.JSON)
                .body(playerId)
                .post(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), playerId));
        return response;
    }

    public Response updatePlayer(String editor, String playerId, String updatePlayerInfo) {

        String uri = String.format(PLAYER_UPDATE_URI, editor, playerId);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(updatePlayerInfo)
                .patch(uri);
        log.info(String.format(URL_STATUS_BODY_RESPONSE_MESSAGE, uri, response.getStatusCode(), playerId));
        return response;
    }
}
