package com.magician.adventure.model;

public enum StatusEffectType {
    QUEMADURA("Quemadura", 3, 0.1, "🔥"),
    CONGELACION("Congelación", 2, 0.0, "❄️"),
    ELECTROCUCION("Electrocución", 2, 0.0, "⚡"),
    VENENO("Veneno", 4, 0.05, "☠️"),
    REGENERACION("Regeneración", 3, 0.08, "💚");

    private final String name;
    private final int duration;
    private final double damagePercent; // % de vida máxima por turno
    private final String emoji;

    StatusEffectType(String name, int duration, double damagePercent, String emoji) {
        this.name = name;
        this.duration = duration;
        this.damagePercent = damagePercent;
        this.emoji = emoji;
    }

    public String getName() { return name; }
    public int getDuration() { return duration; }
    public double getDamagePercent() { return damagePercent; }
    public String getEmoji() { return emoji; }
}