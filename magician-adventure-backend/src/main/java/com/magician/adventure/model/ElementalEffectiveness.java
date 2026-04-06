package com.magician.adventure.model;

import java.util.HashMap;
import java.util.Map;

public class ElementalEffectiveness {

    private static final Map<Element, Map<Element, Double>> effectivenessChart = new HashMap<>();

    static {
        // Fuego
        Map<Element, Double> fireChart = new HashMap<>();
        fireChart.put(Element.AIRE, 1.0);
        fireChart.put(Element.AGUA, 0.5);      // Débil vs Agua
        fireChart.put(Element.ELECTRICIDAD, 1.0);
        fireChart.put(Element.LUZ, 1.0);
        fireChart.put(Element.OSCURIDAD, 2.0);  // Fuerte vs Oscuridad
        fireChart.put(Element.TIERRA, 1.0);
        fireChart.put(Element.FUEGO, 1.0);
        effectivenessChart.put(Element.FUEGO, fireChart);

        // Agua
        Map<Element, Double> waterChart = new HashMap<>();
        waterChart.put(Element.FUEGO, 2.0);     // Fuerte vs Fuego
        waterChart.put(Element.AIRE, 1.0);
        waterChart.put(Element.ELECTRICIDAD, 0.5); // Débil vs Electricidad
        waterChart.put(Element.LUZ, 1.0);
        waterChart.put(Element.OSCURIDAD, 1.0);
        waterChart.put(Element.TIERRA, 1.0);
        waterChart.put(Element.AGUA, 1.0);
        effectivenessChart.put(Element.AGUA, waterChart);

        // Aire
        Map<Element, Double> airChart = new HashMap<>();
        airChart.put(Element.FUEGO, 0.5);       // Débil vs Fuego
        airChart.put(Element.AGUA, 1.0);
        airChart.put(Element.ELECTRICIDAD, 2.0); // Fuerte vs Electricidad
        airChart.put(Element.LUZ, 1.0);
        airChart.put(Element.OSCURIDAD, 1.0);
        airChart.put(Element.TIERRA, 1.0);
        airChart.put(Element.AIRE, 1.0);
        effectivenessChart.put(Element.AIRE, airChart);

        // Electricidad
        Map<Element, Double> electricChart = new HashMap<>();
        electricChart.put(Element.FUEGO, 1.0);
        electricChart.put(Element.AGUA, 2.0);    // Fuerte vs Agua
        electricChart.put(Element.AIRE, 0.5);   // Débil vs Aire
        electricChart.put(Element.LUZ, 1.0);
        electricChart.put(Element.OSCURIDAD, 1.0);
        electricChart.put(Element.TIERRA, 1.0);
        electricChart.put(Element.ELECTRICIDAD, 1.0);
        effectivenessChart.put(Element.ELECTRICIDAD, electricChart);

        // Luz
        Map<Element, Double> lightChart = new HashMap<>();
        lightChart.put(Element.FUEGO, 1.0);
        lightChart.put(Element.AGUA, 1.0);
        lightChart.put(Element.AIRE, 1.0);
        lightChart.put(Element.ELECTRICIDAD, 1.0);
        lightChart.put(Element.OSCURIDAD, 2.0); // Fuerte vs Oscuridad
        lightChart.put(Element.TIERRA, 1.0);
        lightChart.put(Element.LUZ, 1.0);
        effectivenessChart.put(Element.LUZ, lightChart);

        // Oscuridad
        Map<Element, Double> darkChart = new HashMap<>();
        darkChart.put(Element.FUEGO, 0.5);      // Débil vs Fuego
        darkChart.put(Element.AGUA, 1.0);
        darkChart.put(Element.AIRE, 1.0);
        darkChart.put(Element.ELECTRICIDAD, 1.0);
        darkChart.put(Element.LUZ, 0.5);         // Débil vs Luz
        darkChart.put(Element.TIERRA, 1.0);
        darkChart.put(Element.OSCURIDAD, 1.0);
        effectivenessChart.put(Element.OSCURIDAD, darkChart);

        // Tierra
        Map<Element, Double> earthChart = new HashMap<>();
        earthChart.put(Element.FUEGO, 1.0);
        earthChart.put(Element.AGUA, 1.0);
        earthChart.put(Element.AIRE, 1.0);
        earthChart.put(Element.ELECTRICIDAD, 1.0);
        earthChart.put(Element.LUZ, 1.0);
        earthChart.put(Element.OSCURIDAD, 1.0);
        earthChart.put(Element.TIERRA, 1.0);
        effectivenessChart.put(Element.TIERRA, earthChart);
    }

    public static double getEffectiveness(Element attacker, Element defender) {
        return effectivenessChart
                .getOrDefault(attacker, new HashMap<>())
                .getOrDefault(defender, 1.0);
    }

    public static String getEffectivenessText(double multiplier) {
        if (multiplier >= 2.0) return "¡Es súper efectivo!";
        if (multiplier <= 0.5) return "No es muy efectivo...";
        return "";
    }
}