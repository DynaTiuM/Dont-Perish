package org.tact.features.immersion.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class ImmersionHud extends CustomUIHud {
    public ImmersionHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    public void render(UICommandBuilder builder, boolean showFrost, boolean showHeat, boolean showDiscomfort) {
        if(showFrost) {
            builder.set("#Immersion.Visible", true)
                    .set("#Immersion.Background", "Hud/Textures/Immersion/FrostHud.png");
        }
        else if(showHeat) {
            builder.set("#Immersion.Visible", true)
                    .set("#Immersion.Background", "Hud/Textures/Immersion/HeatHud.png");
        } else if (showDiscomfort) {
            builder.set("#Immersion.Visible", true)
                    .set("#Immersion.Background", "Hud/Textures/Immersion/DiscomfortHud.png");
        }
        else {
            builder.set("#Immersion.Visible", false);
        }
    }

    @Override
    protected void build(UICommandBuilder builder) {
        builder.append("Hud/ImmersionHud.ui");
    }
}
