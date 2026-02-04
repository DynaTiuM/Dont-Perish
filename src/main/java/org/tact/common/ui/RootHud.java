package org.tact.common.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.tact.common.utils.ReflectionHelper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RootHud extends CustomUIHud {

    private final Map<String, CustomUIHud> huds = new LinkedHashMap<>();

    public RootHud(PlayerRef playerRef) {
        super(playerRef);
    }

    public void register(String id, CustomUIHud hud) {
        huds.put(id, hud);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder builder) {
        for (CustomUIHud hud : huds.values()) {
            if (hud != null) {
                ReflectionHelper.invokeMethod(hud, "build", builder);
            }
        }
    }
    public Map<String, CustomUIHud> getHuds() {
        return Collections.unmodifiableMap(huds);
    }

}
