package com.magician.adventure.model;

import lombok.Data;

@Data
public class StatusEffect {
    private StatusEffectType type;
    private int remainingTurns;
    private double power;

    public StatusEffect(StatusEffectType type, int duration) {
        this.type = type;
        this.remainingTurns = duration;
        this.power = 1.0;
    }

    public void tick(Player player) {
        if (remainingTurns <= 0) return;

        // Aplicar daño por quemadura/veneno
        if (type.getDamagePercent() > 0) {
            int damage = (int)(player.getMaxHealth() * type.getDamagePercent());
            player.setHealth(Math.max(0, player.getHealth() - damage));
        }

        // Reducir duración
        remainingTurns--;
    }

    public void tickEnemy(Enemy enemy) {
        if (remainingTurns <= 0) return;

        if (type.getDamagePercent() > 0) {
            int damage = (int)(enemy.getMaxHealth() * type.getDamagePercent());
            enemy.setHealth(Math.max(0, enemy.getHealth() - damage));
        }

        remainingTurns--;
    }

    public boolean isExpired() {
        return remainingTurns <= 0;
    }

    public String getDisplayName() {
        return type.getEmoji() + " " + type.getName() + " (" + remainingTurns + ")";
    }
}