package com.magician.adventure.dto;

import com.magician.adventure.model.Element;
import lombok.Data;

@Data
public class EnemyDTO {
    private String id;
    private String name;
    private Element element;
    private int level;
    private int health;
    private int maxHealth;
    private int attackPower;
    private int defense;
    private double healthPercent;
    private String state; // normal, agresivo, defensivo

    public EnemyDTO() {}

    public EnemyDTO(com.magician.adventure.model.Enemy enemy) {
        this.id = enemy.getId();
        this.name = enemy.getName();
        this.element = enemy.getElement();
        this.level = enemy.getLevel();
        this.health = enemy.getHealth();
        this.maxHealth = enemy.getMaxHealth();
        this.attackPower = enemy.getAttackPower();
        this.defense = enemy.getDefense();
        this.healthPercent = (double) enemy.getHealth() / enemy.getMaxHealth() * 100;
        this.state = enemy.getState();
    }
}