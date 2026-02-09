package org.tact.common.util;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;

/**
 * Utilitaires pour manipuler les statistiques d'entités
 */
public class StatHelper {

    /**
     * Limite une valeur entre les bornes min/max d'une statistique
     */
    public static float clamp(EntityStatValue stat, float value) {
        return Math.min(stat.getMax(), Math.max(stat.getMin(), value));
    }

    /**
     * Vérifie si une statistique est à son maximum
     */
    public static boolean isAtMax(EntityStatValue stat) {
        return stat.get() >= stat.getMax();
    }

    /**
     * Vérifie si une statistique est à son minimum
     */
    public static boolean isAtMin(EntityStatValue stat) {
        return stat.get() <= stat.getMin();
    }

    /**
     * Calcule le pourcentage d'une statistique
     */
    public static float getPercentage(EntityStatValue stat) {
        float range = stat.getMax() - stat.getMin();
        if (range == 0) return 0;
        return (stat.get() - stat.getMin()) / range;
    }
}
