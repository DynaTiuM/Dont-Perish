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

    public void updateValues(Season season, float progress, float temperature, boolean hasProtection) {
        // -20°C to 50°C
        float temperaturePercent = (temperature + 20.0f) / 70.0f;
        temperaturePercent = Math.max(0.0f, Math.min(1.0f, temperaturePercent));

        UICommandBuilder builder = new UICommandBuilder()
                .set("#Icon.Background", "Textures/Season_" + season.name() + ".png")
                .set("#TemperatureBar.Value", temperaturePercent)
                .set("#TemperatureBar.Visible", true);

        this.update(false, builder);
    }

    private String getTemperatureColor(float temp) {
        if (temp > 30.0f) {
            return "#FF4444";
        } else if (temp > 25.0f) {
            return "#FF8844";
        } else if (temp > 15.0f) {
            return "#44FF44";
        } else if (temp > 5.0f) {
            return "#4488FF";
        } else {
            return "#4444FF";
        }
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/SeasonHud.ui");
    }
}
