package com.magician.adventure.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
public class Enemy {
    private String id;
    private String name;
    private Element element;
    private int level;

    private int health;
    private int maxHealth;
    private int attackPower;
    private int defense = 5;
    private int expReward;

    // IA
    private String state = "normal"; // normal, agresivo, defensivo

    // Efectos de estado
    private List<StatusEffect> activeEffects = new ArrayList<>();

    public Enemy(String name, Element element, int level, int maxHealth, int attackPower, int expReward) {
        this.name = name;
        this.element = element;
        this.level = level;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.expReward = expReward;
        this.id = UUID.randomUUID().toString();
    }

    public Map<String, Object> takeTurn(Player player) {
        Map<String, Object> action = new HashMap<>();

        // Determinar estado según situación
        if (this.health < this.maxHealth * 0.3) {
            this.state = "defensivo";
        } else if (player.getHealth() < player.getMaxHealth() * 0.5) {
            this.state = "agresivo";
        } else {
            this.state = "normal";
        }

        // Ejecutar acción según estado
        int damage = 0;
        boolean healed = false;

        switch (this.state) {
            case "agresivo":
                damage = (int)(this.attackPower * 1.5);
                break;
            case "defensivo":
                // Curarse
                int healAmount = this.defense * 2;
                this.health = Math.min(this.maxHealth, this.health + healAmount);
                healed = true;
                damage = (int)(this.attackPower * 0.5);
                break;
            default: // normal
                damage = this.attackPower;
                break;
        }

        // Aplicar daño al jugador
        player.takeDamage(damage);

        action.put("damage", damage);
        action.put("state", this.state);
        action.put("healed", healed);
        action.put("name", "Ataque");

        return action;
    }

    public int takeDamage(int damage) {
        int actualDamage = Math.max(0, damage - this.defense);
        this.health = Math.max(0, this.health - actualDamage);
        return actualDamage;
    }

    public void addStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
    }

    public void processStatusEffects() {
        Iterator<StatusEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            StatusEffect effect = it.next();
            effect.tickEnemy(this);
            if (effect.isExpired()) {
                it.remove();
            }
        }
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public int getExpValue() {
        return 50 + (this.level * 25);
    }
}