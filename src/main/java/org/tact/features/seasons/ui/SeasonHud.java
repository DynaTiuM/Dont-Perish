package org.tact.features.seasons.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.tact.features.seasons.model.Season;

import javax.annotation.Nonnull;

public class SeasonHud extends CustomUIHud {

    public SeasonHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Met à jour l'affichage du HUD avec les données actuelles
     *
     * @param season Saison actuelle
     * @param progress Progression de la saison (0.0 à 1.0)
     * @param temperature Température actuelle en °C
     * @param hasProtection Le joueur a-t-il une protection?
     */
    public void updateValues(Season season, float progress, float temperature, boolean hasProtection) {
        // Convertir température (-20°C à 40°C) en pourcentage (0.0 à 1.0)
        float tempPercent = (temperature + 20.0f) / 60.0f;
        tempPercent = Math.max(0.0f, Math.min(1.0f, tempPercent));

        UICommandBuilder builder = new UICommandBuilder()
                .set("#Icon.Background", "Textures/Season_" + season.name() + ".png")
                .set("#TemperatureBar.Value", tempPercent)
                .set("#TemperatureBar.Visible", true);

        this.update(false, builder);
    }

    /**
     * Formate la température pour l'affichage
     */
    private String formatTemperature(float temp) {
        int rounded = Math.round(temp);
        return rounded + "°C";
    }

    /**
     * Retourne une couleur selon la température
     */
    private String getTemperatureColor(float temp) {
        if (temp > 30.0f) {
            return "#FF4444"; // Rouge (chaud)
        } else if (temp > 25.0f) {
            return "#FF8844"; // Orange
        } else if (temp > 15.0f) {
            return "#44FF44"; // Vert (confortable)
        } else if (temp > 5.0f) {
            return "#4488FF"; // Bleu clair
        } else {
            return "#4444FF"; // Bleu foncé (froid)
        }
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/SeasonHud.ui");
    }
}
