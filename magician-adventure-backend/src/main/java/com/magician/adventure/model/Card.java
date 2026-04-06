package com.magician.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private String id;
    private String name;
    private Element element;
    private CardType type;
    private int manaCost;
    private int damage;
    private String effectType; // "burn", "freeze", "heal", "shield", etc.
    private String description;
    private int levelRequired; // Nivel mínimo para usar

    // Efecto especial como función (se implementa en el servicio)
    private String specialEffect;

    public Card(String name, Element element, int damage, int manaCost, String effectType) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.manaCost = manaCost;
        this.effectType = effectType;
        this.type = CardType.ATAQUE;
        this.levelRequired = 1;
        generateId();
    }

    private void generateId() {
        this.id = element.name().toLowerCase() + "_" +
                name.toLowerCase().replace(" ", "_") + "_" +
                System.currentTimeMillis();
    }

    // Método para verificar si la carta tiene efecto elemental
    public boolean hasElementalEffect() {
        return effectType != null && !effectType.isEmpty();
    }

    // Obtener descripción automática si no está definida
    public String getFullDescription() {
        if (description != null && !description.isEmpty()) {
            return description;
        }
        StringBuilder desc = new StringBuilder();
        desc.append(type.getEmoji()).append(" ").append(name);
        desc.append(" | ").append(element.getEmoji()).append(" ").append(element.getDisplayName());
        desc.append(" | ⚡").append(manaCost).append(" | ⚔️").append(damage);
        if (hasElementalEffect()) {
            desc.append(" | Efecto: ").append(effectType);
        }
        return desc.toString();
    }
}