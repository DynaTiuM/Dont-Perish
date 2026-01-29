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

    public void render(UICommandBuilder builder, Season season, float temperature) {
        float temperaturePercent = (temperature + 20.0f) / 70.0f;
        temperaturePercent = Math.max(0.0f, Math.min(1.0f, temperaturePercent));

        builder.set("#SeasonIcon.Background", "Hud/Textures/Season_" + season.name() + ".png")
                .set("#TemperatureBar.Value", temperaturePercent)
                .set("#TemperatureBar.Visible", true);
    }
    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/SeasonHud.ui");
    }
}
