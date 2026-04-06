package com.magician.adventure.model;

public enum Element {
    FUEGO("Fuego", "🔥"),
    AGUA("Agua", "💧"),
    TIERRA("Tierra", "🌍"),
    AIRE("Aire", "💨"),
    ELECTRICIDAD("Electricidad", "⚡"),
    LUZ("Luz", "✨"),
    OSCURIDAD("Oscuridad", "🌑");

    private final String displayName;
    private final String emoji;

    Element(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
}