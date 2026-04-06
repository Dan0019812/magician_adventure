package com.magician.adventure.controller;

import com.magician.adventure.dto.*;
import com.magician.adventure.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // Permitir llamadas desde cualquier frontend
public class GameController {

    @Autowired
    private GameService gameService;

    // ==================== ENDPOINTS PRINCIPALES ====================

    @PostMapping("/new")
    public ResponseEntity<BattleStateDTO> createNewGame(@RequestBody CreateGameRequest request) {
        try {
            BattleStateDTO game = gameService.createNewGame(request);
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{battleId}/action")
    public ResponseEntity<BattleStateDTO> processAction(
            @PathVariable String battleId,
            @RequestBody GameActionRequest request) {
        try {
            BattleStateDTO result = gameService.processAction(battleId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{battleId}/play-card/{cardIndex}")
    public ResponseEntity<BattleStateDTO> playCard(
            @PathVariable String battleId,
            @PathVariable int cardIndex) {
        GameActionRequest request = new GameActionRequest();
        request.setActionType("play_card");
        request.setCardIndex(cardIndex);
        return processAction(battleId, request);
    }

    @PostMapping("/{battleId}/physical-attack")
    public ResponseEntity<BattleStateDTO> physicalAttack(@PathVariable String battleId) {
        GameActionRequest request = new GameActionRequest("physical_attack");
        return processAction(battleId, request);
    }

    @PostMapping("/{battleId}/recharge-mana")
    public ResponseEntity<BattleStateDTO> rechargeMana(@PathVariable String battleId) {
        GameActionRequest request = new GameActionRequest("recharge_mana");
        return processAction(battleId, request);
    }

    @PostMapping("/{battleId}/end-turn")
    public ResponseEntity<BattleStateDTO> endTurn(@PathVariable String battleId) {
        GameActionRequest request = new GameActionRequest("end_turn");
        return processAction(battleId, request);
    }

    // ==================== GESTIÓN DE STATS ====================

    @PostMapping("/player/{playerId}/spend-point")
    public ResponseEntity<PlayerDTO> spendStatPoint(
            @PathVariable String playerId,
            @RequestParam String stat) {
        try {
            PlayerDTO player = gameService.spendStatPoint(playerId, stat);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Magician Adventure Backend is running!");
    }
}