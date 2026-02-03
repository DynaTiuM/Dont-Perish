package org.tact.features.comfort.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class ComfortHud extends CustomUIHud {

    public ComfortHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    public void render(UICommandBuilder builder, float comfortValue) {
        builder.set("#ComfortBar.Value", comfortValue)
                .set("#ComfortBar.Visible", true);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/ComfortHud.ui");
    }
}
