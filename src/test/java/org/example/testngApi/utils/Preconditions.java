package org.example.testngApi.utils;

import org.example.testngApi.dto.Player;

public class Preconditions {
    public static Player buildValidPlayer() {
        return Player.builder()
                .age(55)
                .gender("male")
                .login("user")
                .password("1234568")
                .role("user")
                .screenName("BigMazzy")
                .build();
    }

    public static Player buildInvalidPlayer() {
        return Player.builder()
                .age(20)
                .gender("male")
                .login(" ")
                .password("1234567")
                .role("user")
                .screenName("BadLogin")
                .build();
    }

    public static Player buildPlayerRoleUser() {
        return Player.builder()
                .age(22)
                .gender("female")
                .login("user")
                .password("1234567")
                .role("user")
                .screenName("User")
                .build();
    }
    public static Player buildUpdatePlayerInfo() {
        return Player.builder()
                .age(55)
                .gender("female")
                .login("admin69")
                .password("123456789")
                .role("admin")
                .screenName("Burger0")
                .build();
    }
}
