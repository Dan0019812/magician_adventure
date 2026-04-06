package com.magician.adventure.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
public class Player {
    private String id;
    private String name;
    private Element element;

    // Stats base
    private int level = 1;
    private int exp = 0;
    private int expToNextLevel = 100;

    private int health = 100;
    private int maxHealth = 100;
    private int mana = 100;
    private int maxMana = 100;
    private int attackPower = 10;
    private int defense = 5;
    private double critChance = 0.05;

    // Sistema de puntos (igual que tu Python)
    private int availablePoints = 0;
    private Map<String, Integer> stats = new HashMap<>();

    // Cartas y habilidades
    private List<Card> hand = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private Set<String> unlockedSkills = new HashSet<>();
    private Set<String> equippedSkills = new HashSet<>();
    private int maxEquippedSkills = 4;

    // Efectos de estado activos
    private List<StatusEffect> activeEffects = new ArrayList<>();

    // ID de sesión para multiplayer futuro
    private String sessionId;

    public Player(String name, Element element) {
        this.name = name;
        this.element = element;
        this.id = UUID.randomUUID().toString();

        // Inicializar stats base
        stats.put("attack", 1);
        stats.put("defense", 1);
        stats.put("crit_chance", 1);
        stats.put("health", 1);
        stats.put("mana", 1);

        // Crear mazo inicial según elemento
        initializeStarterDeck();
    }

    private void initializeStarterDeck() {
        // Cartas básicas según elemento (igual que tu Python)
        switch (element) {
            case FUEGO:
                deck.add(new Card("Bola de Fuego", Element.FUEGO, 15, 20, "burn"));
                deck.add(new Card("Chispa", Element.FUEGO, 10, 15, null));
                deck.add(new Card("Explosión", Element.FUEGO, 20, 25, "burn"));
                break;
            case AGUA:
                deck.add(new Card("Chorro de Agua", Element.AGUA, 12, 15, null));
                deck.add(new Card("Congelación", Element.AGUA, 10, 20, "freeze"));
                deck.add(new Card("Tsunami", Element.AGUA, 18, 25, null));
                break;
            case TIERRA:
                deck.add(new Card("Lanza de Piedra", Element.TIERRA, 25, 15, null));
                deck.add(new Card("Muro de Piedra", Element.TIERRA, 0, 20, "shield"));
                deck.add(new Card("Terremoto", Element.TIERRA, 18, 35, null));
                break;
            case AIRE:
                deck.add(new Card("Cuchilla de Viento", Element.AIRE, 18, 12, null));
                deck.add(new Card("Tornado", Element.AIRE, 15, 30, null));
                deck.add(new Card("Vendaval", Element.AIRE, 10, 20, "mana_regen"));
                break;
            case ELECTRICIDAD:
                deck.add(new Card("Descarga", Element.ELECTRICIDAD, 22, 15, "shock"));
                deck.add(new Card("Rayo", Element.ELECTRICIDAD, 40, 35, "shock"));
                deck.add(new Card("Tormenta", Element.ELECTRICIDAD, 25, 45, "shock"));
                break;
            case LUZ:
                deck.add(new Card("Rayo de Luz", Element.LUZ, 20, 15, null));
                deck.add(new Card("Sanación", Element.LUZ, 0, 20, "heal"));
                deck.add(new Card("Destello", Element.LUZ, 12, 15, "heal"));
                break;
            case OSCURIDAD:
                deck.add(new Card("Lanza Sombría", Element.OSCURIDAD, 23, 15, "drain"));
                deck.add(new Card("Maldición", Element.OSCURIDAD, 15, 30, "curse"));
                deck.add(new Card("Vacío", Element.OSCURIDAD, 25, 45, "drain"));
                break;
        }

        // Desbloquear habilidades iniciales del nivel 1
        unlockLevelSkills();
    }

    public void gainExp(int amount) {
        this.exp += amount;
        while (this.exp >= this.expToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.exp -= this.expToNextLevel;
        this.expToNextLevel = (int)(this.expToNextLevel * 1.5);

        // Dar 3 puntos para distribuir
        this.availablePoints += 3;

        // Mejorar stats base
        this.maxHealth += 20;
        this.health = this.maxHealth;
        this.maxMana += 10;
        this.mana = this.maxMana;
        this.attackPower += 2;

        // Desbloquear nuevas habilidades
        unlockLevelSkills();
    }

    private void unlockLevelSkills() {
        // Habilidades por nivel (simplificado de tu skill_tree.py)
        Map<Integer, List<String>> skillsByLevel = new HashMap<>();
        skillsByLevel.put(1, Arrays.asList("Ataque Básico", "Defensa"));
        skillsByLevel.put(2, Arrays.asList("Golpe Poderoso", "Escudo Mágico"));
        skillsByLevel.put(3, Arrays.asList("Ataque Elemental", "Curación Rápida"));
        // ... hasta nivel 10

        List<String> newSkills = skillsByLevel.getOrDefault(this.level, new ArrayList<>());
        for (String skill : newSkills) {
            if (unlockedSkills.add(skill)) {
                // Auto-equipar si hay espacio
                if (equippedSkills.size() < maxEquippedSkills) {
                    equippedSkills.add(skill);
                }
            }
        }
    }

    public boolean spendStatPoint(String stat) {
        if (availablePoints > 0 && stats.containsKey(stat)) {
            stats.put(stat, stats.get(stat) + 1);
            availablePoints--;
            updateStatsFromPoints();
            return true;
        }
        return false;
    }

    private void updateStatsFromPoints() {
        // Actualizar stats según puntos invertidos
        this.maxHealth = 100 + (stats.get("health") - 1) * 25;
        this.maxMana = 100 + (stats.get("mana") - 1) * 20;
        this.attackPower = 10 + (stats.get("attack") - 1) * 5;
        this.defense = 5 + (stats.get("defense") - 1) * 3;
        this.critChance = 0.05 + (stats.get("crit_chance") - 1) * 0.03;

        // Asegurar que no exceda máximos
        this.health = Math.min(this.health, this.maxHealth);
        this.mana = Math.min(this.mana, this.maxMana);
    }

    public void drawCard() {
        if (hand.size() < 5 && !deck.isEmpty()) {
            // Barajar si el mazo está vacío (usar descarte)
            if (deck.isEmpty() && !discardPile.isEmpty()) {
                deck.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(deck);
            }

            if (!deck.isEmpty()) {
                hand.add(deck.remove(0));
            }
        }
    }

    public void drawInitialHand() {
        for (int i = 0; i < 3; i++) {
            drawCard();
        }
    }

    public boolean playCard(int cardIndex, Enemy target) {
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            return false;
        }

        Card card = hand.get(cardIndex);
        if (mana < card.getManaCost()) {
            return false; // No hay suficiente maná
        }

        // Gastar maná
        mana -= card.getManaCost();

        // Aplicar daño (con cálculo de efectividad elemental)
        int finalDamage = calculateDamage(card, target);
        target.takeDamage(finalDamage);

        // Aplicar efecto especial
        applyCardEffect(card, target);

        // Mover carta a descarte
        discardPile.add(hand.remove(cardIndex));

        return true;
    }

    private int calculateDamage(Card card, Enemy target) {
        double multiplier = ElementalEffectiveness.getEffectiveness(
                card.getElement(),
                target.getElement()
        );

        int baseDamage = card.getDamage();
        boolean isCrit = Math.random() < critChance;

        if (isCrit) {
            baseDamage *= 1.5;
        }

        return (int)(baseDamage * multiplier);
    }

    private void applyCardEffect(Card card, Enemy target) {
        if (card.getEffectType() == null) return;

        switch (card.getEffectType()) {
            case "burn":
                target.addStatusEffect(new StatusEffect(StatusEffectType.QUEMADURA, 3));
                break;
            case "freeze":
                target.addStatusEffect(new StatusEffect(StatusEffectType.CONGELACION, 2));
                break;
            case "shock":
                target.addStatusEffect(new StatusEffect(StatusEffectType.ELECTROCUCION, 2));
                break;
            case "heal":
                this.health = Math.min(this.maxHealth, this.health + 15);
                break;
            case "shield":
                this.defense += 5;
                break;
            case "mana_regen":
                this.mana = Math.min(this.maxMana, this.mana + 20);
                break;
            case "drain":
                int drained = target.takeDamage(5);
                this.health = Math.min(this.maxHealth, this.health + drained);
                break;
        }
    }

    public void physicalAttack(Enemy target) {
        int damage = Math.max(0, attackPower - target.getDefense());
        target.takeDamage(damage);
    }

    public void rechargeMana() {
        this.mana = Math.min(this.maxMana, this.mana + 20);
    }

    public void startTurn() {
        // Recuperar algo de maná
        this.mana = Math.min(this.maxMana, this.mana + 10);

        // Robar carta
        drawCard();

        // Procesar efectos de estado
        processStatusEffects();
    }

    public void processStatusEffects() {
        Iterator<StatusEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            StatusEffect effect = it.next();
            effect.tick(this);
            if (effect.isExpired()) {
                it.remove();
            }
        }
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(0, damage - this.defense);
        this.health = Math.max(0, this.health - actualDamage);
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public double getHealthPercent() {
        return (double) this.health / this.maxHealth;
    }

    public double getManaPercent() {
        return (double) this.mana / this.maxMana;
    }
}