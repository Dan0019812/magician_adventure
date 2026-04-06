package com.magician.adventure.dto;

import lombok.Data;

@Data
public class GameActionRequest {
    private String actionType; // "play_card", "physical_attack", "recharge_mana", "end_turn"
    private int cardIndex; // Para "play_card"
    private String targetId; // Para futuro multiplayer

    public GameActionRequest() {}

    public GameActionRequest(String actionType) {
        this.actionType = actionType;
    }
}