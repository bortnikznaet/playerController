package org.example.testngApi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.example.testngApi.client.PlayerClient;
import org.example.testngApi.dto.Player;
import org.example.testngApi.dto.PlayersList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Utils {
    PlayerClient client = new PlayerClient();

    public String getLoginSupervision() throws IOException {

        Player player = Player.builder()
                .id(1)
                .build();
        Map<String, String> queryParams = player.toQueryParams();

        Response responsePost = client.postPlayer(queryParams);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(responsePost.getBody().asPrettyString());

        return json.get("login").asText();
    }

    public String getLastIdPlayer() throws IOException {
        Response responseGetAllPlayers = client.getAllPlayers();

        ObjectMapper objectMapper = new ObjectMapper();

        PlayersList playerListResponse = objectMapper.readValue(responseGetAllPlayers.getBody().asString(), PlayersList.class);
        List<Player> players = playerListResponse.getPlayers();

        return players.get(players.size() - 1).getId().toString();
    }


}
