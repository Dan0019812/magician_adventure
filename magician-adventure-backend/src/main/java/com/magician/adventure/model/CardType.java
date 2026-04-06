package com.magician.adventure.model;

public enum CardType {
    ATAQUE("Ataque", "⚔️"),
    DEFENSA("Defensa", "🛡️"),
    HABILIDAD("Habilidad", "✨"),
    ESPECIAL("Especial", "⭐");

    private final String displayName;
    private final String emoji;

    CardType(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
}