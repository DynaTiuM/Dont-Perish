package org.tact.features.seasons;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.seasons.system.SeasonsCycleSystem;
import org.tact.features.seasons.system.SeasonsTemperatureBridgeSystem;
import org.tact.features.seasons.system.SeasonsWeatherSystem;
import org.tact.features.seasons.ui.SeasonsHud;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;

    public SeasonsFeature(
            SeasonsConfig config
    ) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        SeasonsResource.TYPE = plugin.getEntityStoreRegistry()
                .registerResource(SeasonsResource.class, "season_resource", SeasonsResource.CODEC);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {

    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            player.getWorld().execute(() -> {
                setupPlayer(player);
                player.sendMessage(Message.raw("[Seasons] Player set up"));
            });
        });
    }

    private void setupPlayer(Player player) {
        Ref<EntityStore> playerRef = player.getReference();
        if (playerRef == null) return;

        Store<EntityStore> store = playerRef.getStore();

        PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
        if (pRef == null) return;
        HudManager.open(player, pRef, new SeasonsHud(pRef), getId());
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonsCycleSystem(config)
        );
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonsWeatherSystem()
        );
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonsTemperatureBridgeSystem(config)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

}
