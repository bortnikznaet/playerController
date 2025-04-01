package org.example.testngApi.dto;
import lombok.Getter;
import lombok.Setter;
import org.example.testngApi.dto.Player;
import java.util.List;

@Getter
@Setter
public class PlayersList {
    private List<Player> players;
}
