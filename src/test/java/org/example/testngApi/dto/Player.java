package org.example.testngApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private Integer id;
    private Integer age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    @JsonIgnore
    public Map<String, String> toQueryParams() {
        Map<String, String> result = new java.util.HashMap<>();
        if (age != null) result.put("age", String.valueOf(age));
        if (gender != null) result.put("gender", gender);
        if (login != null) result.put("login", login);
        if (password != null) result.put("password", password);
        if (role != null) result.put("role", role);
        if (screenName != null) result.put("screenName", screenName);
        if (id != null) result.put("playerId", String.valueOf(id));
        return result ;

    }

}
