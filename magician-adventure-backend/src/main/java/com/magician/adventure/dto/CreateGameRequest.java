package com.magician.adventure.dto;

import com.magician.adventure.model.Element;
import lombok.Data;

@Data
public class CreateGameRequest {
    private String playerName;
    private Element selectedElement;

    public CreateGameRequest() {}

    public CreateGameRequest(String playerName, Element element) {
        this.playerName = playerName;
        this.selectedElement = element;
    }
}