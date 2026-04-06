package com.magician.adventure.dto;

import lombok.Data;
import java.util.*;

@Data
public class BattleStateDTO {
    private String battleId;
    private PlayerDTO player;
    private EnemyDTO enemy;
    private String currentTurn; // "player" o "enemy"
    private boolean battleOver;
    private String winner; // "player", "enemy", o null
    private List<String> battleLog; // Historial de mensajes
    private String lastAction; // Última acción realizada
    private boolean playerCanAct; // Si es turno del jugador y puede hacer algo

    public BattleStateDTO() {
        this.battleLog = new ArrayList<>();
    }
}