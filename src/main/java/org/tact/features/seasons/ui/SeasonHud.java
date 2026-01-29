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
        temperaturePercent = Math.max(0.0f, Math.min(1.0f, temperaturePercent)) * 2;

        UICommandBuilder builder = new UICommandBuilder()
                .set("#Icon.Background", "Textures/Season_" + season.name() + ".png")
                .set("#TemperatureBar.Value", temperaturePercent)
                .set("#TemperatureBar.Visible", true);

        this.update(false, builder);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/SeasonHud.ui");
    }
}
