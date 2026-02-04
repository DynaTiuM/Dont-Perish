package org.tact.features.seasons.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.tact.features.seasons.model.Season;

import javax.annotation.Nonnull;

public class SeasonsHud extends CustomUIHud {

    public SeasonsHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    public void render(UICommandBuilder builder, Season season, float progress, int globalDay) {

        builder.set("#SeasonIcon.Background", "Hud/Textures/Season_" + season.name() + ".png")
                .set("#SeasonProgressBar.Value", progress)
                .set("#DayLabel.Text", "Day: " + globalDay);
    }
    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        builder.append("Hud/SeasonHud.ui");
    }
}
