package com.magician.adventure.service;

import com.magician.adventure.dto.*;
import com.magician.adventure.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    // Almacenar partidas activas en memoria (para MVP)
    private final Map<String, BattleState> activeBattles = new ConcurrentHashMap<>();
    private final Map<String, Player> activePlayers = new ConcurrentHashMap<>();

    // Generador de IDs únicos
    private String generateBattleId() {
        return "battle_" + UUID.randomUUID().toString().substring(0, 8);
    }

    // ==================== CREAR NUEVA PARTIDA ====================

    public BattleStateDTO createNewGame(CreateGameRequest request) {
        // Crear jugador
        Player player = new Player(request.getPlayerName(), request.getSelectedElement());
        player.drawInitialHand();

        // Guardar jugador
        activePlayers.put(player.getId(), player);

        // Crear enemigo inicial (nivel 1, elemento aleatorio)
        Enemy enemy = createEnemyForLevel(1, player.getElement());

        // Crear estado de batalla
        String battleId = generateBattleId();
        BattleState battle = new BattleState(battleId, player, enemy);

        activeBattles.put(battleId, battle);

        return convertToDTO(battle);
    }

    private Enemy createEnemyForLevel(int level, Element playerElement) {
        // Enemigos básicos por nivel
        String[] prefixes = {"Joven", "Salvaje", "Feroz", "Anciano", "Legendario"};
        String prefix = prefixes[Math.min(level / 4, prefixes.length - 1)];

        // Elemento aleatorio diferente al del jugador (para variedad)
        Element[] allElements = Element.values();
        Element enemyElement;
        do {
            enemyElement = allElements[(int)(Math.random() * allElements.length)];
        } while (enemyElement == playerElement && allElements.length > 1);

        String name = prefix + " " + enemyElement.getDisplayName();

        int baseHealth = 50 + (level * 30);
        int baseAttack = 5 + (level * 2);
        int expReward = 50 + (level * 25);

        return new Enemy(name, enemyElement, level, baseHealth, baseAttack, expReward);
    }

    // ==================== ACCIONES DE BATALLA ====================

    public BattleStateDTO processAction(String battleId, GameActionRequest request) {
        BattleState battle = activeBattles.get(battleId);
        if (battle == null) {
            throw new RuntimeException("Batalla no encontrada: " + battleId);
        }

        if (battle.isBattleOver()) {
            return convertToDTO(battle);
        }

        // Solo procesar si es turno del jugador
        if (!"player".equals(battle.getCurrentTurn())) {
            return convertToDTO(battle);
        }

        Player player = battle.getPlayer();
        Enemy enemy = battle.getEnemy();

        switch (request.getActionType()) {
            case "play_card":
                playCard(battle, request.getCardIndex());
                break;
            case "physical_attack":
                player.physicalAttack(enemy);
                battle.addToLog(player.getName() + " usa Ataque Físico por " +
                        Math.max(0, player.getAttackPower() - enemy.getDefense()) + " daño");
                break;
            case "recharge_mana":
                int manaBefore = player.getMana();
                player.rechargeMana();
                int manaRecovered = player.getMana() - manaBefore;
                battle.addToLog(player.getName() + " recarga " + manaRecovered + " de maná");
                break;
            case "end_turn":
                // Solo termina turno, no hace nada más
                break;
            default:
                throw new RuntimeException("Acción desconocida: " + request.getActionType());
        }

        // Verificar si enemigo derrotado
        if (!enemy.isAlive()) {
            endBattle(battle, "player");
            return convertToDTO(battle);
        }

        // Turno del enemigo
        processEnemyTurn(battle);

        // Verificar si jugador derrotado
        if (!player.isAlive()) {
            endBattle(battle, "enemy");
        }

        return convertToDTO(battle);
    }

    private void playCard(BattleState battle, int cardIndex) {
        Player player = battle.getPlayer();
        Enemy enemy = battle.getEnemy();

        if (cardIndex < 0 || cardIndex >= player.getHand().size()) {
            battle.addToLog("Índice de carta inválido");
            return;
        }

        Card card = player.getHand().get(cardIndex);

        if (player.getMana() < card.getManaCost()) {
            battle.addToLog("No hay suficiente maná para " + card.getName());
            return;
        }

        // Calcular efectividad elemental
        double effectiveness = ElementalEffectiveness.getEffectiveness(
                card.getElement(), enemy.getElement()
        );

        // Jugar carta
        boolean success = player.playCard(cardIndex, enemy);

        if (success) {
            String effectivenessText = ElementalEffectiveness.getEffectivenessText(effectiveness);
            String message = player.getName() + " usa " + card.getName();
            if (!effectivenessText.isEmpty()) {
                message += " - " + effectivenessText;
            }
            battle.addToLog(message);

            if (card.getEffectType() != null) {
                battle.addToLog("Efecto: " + card.getEffectType());
            }
        }
    }

    private void processEnemyTurn(BattleState battle) {
        battle.setCurrentTurn("enemy");

        Player player = battle.getPlayer();
        Enemy enemy = battle.getEnemy();

        // Procesar efectos de estado del enemigo primero
        enemy.processStatusEffects();

        // IA del enemigo
        Map<String, Object> action = enemy.takeTurn(player);

        String actionName = (String) action.get("name");
        int damage = (int) action.get("damage");
        boolean healed = (boolean) action.get("healed");
        String state = (String) action.get("state");

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Enemigo ").append(enemy.getElement().getDisplayName());
        logMessage.append(" usa ").append(actionName);

        if (healed) {
            logMessage.append(" y se cura");
        }
        logMessage.append(" por ").append(damage).append(" daño");

        battle.addToLog(logMessage.toString());

        // Procesar efectos de estado del jugador
        player.processStatusEffects();

        // Cambiar turno de vuelta al jugador
        battle.setCurrentTurn("player");
        player.startTurn(); // Robar carta, recuperar maná, etc.
    }

    private void endBattle(BattleState battle, String winner) {
        battle.setBattleOver(true);
        battle.setWinner(winner);

        if ("player".equals(winner)) {
            Player player = battle.getPlayer();
            Enemy enemy = battle.getEnemy();

            int expGained = enemy.getExpValue();
            player.gainExp(expGained);

            battle.addToLog("¡VICTORIA! Ganas " + expGained + " EXP");

            if (player.getExp() >= player.getExpToNextLevel()) {
                battle.addToLog("¡SUBES DE NIVEL! Nivel " + player.getLevel());
            }
        } else {
            battle.addToLog("DERROTA... Intenta de nuevo");
        }
    }

    // ==================== GESTIÓN DE STATS ====================

    public PlayerDTO spendStatPoint(String playerId, String stat) {
        Player player = activePlayers.get(playerId);
        if (player == null) {
            throw new RuntimeException("Jugador no encontrado");
        }

        boolean success = player.spendStatPoint(stat);
        if (!success) {
            throw new RuntimeException("No se pudo gastar punto en " + stat);
        }

        return new PlayerDTO(player);
    }

    // ==================== CONVERSORES ====================

    private BattleStateDTO convertToDTO(BattleState battle) {
        BattleStateDTO dto = new BattleStateDTO();
        dto.setBattleId(battle.getId());
        dto.setPlayer(new PlayerDTO(battle.getPlayer()));
        dto.setEnemy(new EnemyDTO(battle.getEnemy()));
        dto.setCurrentTurn(battle.getCurrentTurn());
        dto.setBattleOver(battle.isBattleOver());
        dto.setWinner(battle.getWinner());
        dto.setBattleLog(new ArrayList<>(battle.getBattleLog()));
        dto.setPlayerCanAct("player".equals(battle.getCurrentTurn()) && !battle.isBattleOver());

        return dto;
    }

    // ==================== CLASE INTERNA BattleState ====================

    private static class BattleState {
        private String id;
        private Player player;
        private Enemy enemy;
        private String currentTurn = "player"; // player empieza
        private boolean battleOver = false;
        private String winner;
        private List<String> battleLog = new ArrayList<>();

        public BattleState(String id, Player player, Enemy enemy) {
            this.id = id;
            this.player = player;
            this.enemy = enemy;
        }

        // Getters y setters
        public String getId() { return id; }
        public Player getPlayer() { return player; }
        public Enemy getEnemy() { return enemy; }
        public String getCurrentTurn() { return currentTurn; }
        public void setCurrentTurn(String turn) { this.currentTurn = turn; }
        public boolean isBattleOver() { return battleOver; }
        public void setBattleOver(boolean over) { this.battleOver = over; }
        public String getWinner() { return winner; }
        public void setWinner(String winner) { this.winner = winner; }
        public List<String> getBattleLog() { return battleLog; }
        public void addToLog(String message) {
            battleLog.add(message);
            // Mantener solo últimos 10 mensajes
            if (battleLog.size() > 10) {
                battleLog.remove(0);
            }
        }
    }
}