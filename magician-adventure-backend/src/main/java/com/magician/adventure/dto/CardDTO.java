package com.magician.adventure.dto;

import com.magician.adventure.model.Card;
import com.magician.adventure.model.CardType;
import com.magician.adventure.model.Element;
import lombok.Data;

@Data
public class CardDTO {
    private String id;
    private String name;
    private Element element;
    private CardType type;
    private int manaCost;
    private int damage;
    private String effectType;
    private String description;
    private String fullDescription;
    private boolean playable; // Si el jugador tiene suficiente maná

    public CardDTO() {}

    public CardDTO(Card card) {
        this.id = card.getId();
        this.name = card.getName();
        this.element = card.getElement();
        this.type = card.getType();
        this.manaCost = card.getManaCost();
        this.damage = card.getDamage();
        this.effectType = card.getEffectType();
        this.description = card.getDescription();
        this.fullDescription = card.getFullDescription();
        this.playable = true; // Se actualizará según el maná del jugador
    }

    public CardDTO(Card card, int currentMana) {
        this(card);
        this.playable = currentMana >= card.getManaCost();
    }
}