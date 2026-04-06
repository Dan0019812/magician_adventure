package com.magician.adventure.dto;

import com.magician.adventure.model.Element;
import lombok.Data;
import java.util.*;

@Data
public class PlayerDTO {
    private String id;
    private String name;
    private Element element;
    private int level;
    private int exp;
    private int expToNextLevel;

    // Stats actuales
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int attackPower;
    private int defense;
    private double critChance;

    // Progresión
    private int availablePoints;
    private Map<String, Integer> stats;

    // Cartas y habilidades
    private List<CardDTO> hand;
    private List<CardDTO> deck;
    private Set<String> unlockedSkills;
    private Set<String> equippedSkills;
    private int maxEquippedSkills;

    // Efectos activos
    private List<String> activeEffects;

    // Porcentajes para barras de UI
    private double healthPercent;
    private double manaPercent;
    private double expPercent;

    // Constructor vacío necesario para Spring
    public PlayerDTO() {}

    // Constructor desde Player (lo usaremos en el servicio)
    public PlayerDTO(com.magician.adventure.model.Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.element = player.getElement();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.expToNextLevel = player.getExpToNextLevel();

        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.mana = player.getMana();
        this.maxMana = player.getMaxMana();
        this.attackPower = player.getAttackPower();
        this.defense = player.getDefense();
        this.critChance = player.getCritChance();

        this.availablePoints = player.getAvailablePoints();
        this.stats = player.getStats();

        // Convertir cartas a DTOs
        this.hand = new ArrayList<>();
        for (var card : player.getHand()) {
            this.hand.add(new CardDTO(card));
        }

        this.deck = new ArrayList<>();
        for (var card : player.getDeck()) {
            this.deck.add(new CardDTO(card));
        }

        this.unlockedSkills = player.getUnlockedSkills();
        this.equippedSkills = player.getEquippedSkills();
        this.maxEquippedSkills = player.getMaxEquippedSkills();

        // Efectos como strings legibles
        this.activeEffects = new ArrayList<>();
        for (var effect : player.getActiveEffects()) {
            this.activeEffects.add(effect.getDisplayName());
        }

        // Porcentajes para la UI
        this.healthPercent = player.getHealthPercent() * 100;
        this.manaPercent = player.getManaPercent() * 100;
        this.expPercent = (double) exp / expToNextLevel * 100;
    }
}