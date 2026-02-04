package org.tact.features.hunger.ui;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class HungerHud extends CustomUIHud {

    public HungerHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    public void updateValues(GameMode gameMode, float hungerValue) {
        UICommandBuilder builder = (new UICommandBuilder()).set("#Icon.Background", "Hud/Textures/HungerIcon.png")
                .set("#ProgressBar.Value", hungerValue).set("#ProgressBar.Visible", gameMode == GameMode.Adventure);
        this.update(false, builder);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/HungerHud.ui");
    }
}
