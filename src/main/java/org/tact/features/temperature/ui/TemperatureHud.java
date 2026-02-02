package org.tact.features.temperature.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class TemperatureHud extends CustomUIHud {

    public TemperatureHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    public void render(UICommandBuilder builder, float temperature) {
        float temperaturePercent = (temperature + 20.0f) / 70.0f;
        temperaturePercent = Math.max(0.0f, Math.min(1.0f, temperaturePercent));

        builder.set("#TemperatureBar.Value", temperaturePercent)
                .set("#TemperatureBar.Visible", true);
    }
    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/TemperatureHud.ui");
    }
}
