package org.tact.features.seasons;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.environment.EnvironmentRegistry;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.handler.SeasonsTemperatureHandler;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.seasons.system.SeasonsCycleSystem;
import org.tact.features.seasons.ui.SeasonsHud;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;
    private final EnvironmentRegistry environmentRegistry;

    public SeasonsFeature(
            SeasonsConfig config,
            EnvironmentRegistry environmentRegistry
    ) {
        this.config = config;
        this.environmentRegistry = environmentRegistry;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        SeasonsResource.TYPE = plugin.getEntityStoreRegistry()
                .registerResource(SeasonsResource.class,"season_resource", SeasonsResource.CODEC);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            player.sendMessage(Message.raw("[Seasons] New Season Manager created"));

            setupPlayer(player);
            player.sendMessage(Message.raw("[Seasons] Player set up"));
        });
    }

    private void setupPlayer(Player player) {
        Ref<EntityStore> playerRef = player.getReference();
        if(playerRef == null) {
            throw new NullPointerException("[Seasons] PlayerRef is null in player.getReference()");
        }
        Store<EntityStore> store = playerRef.getStore();


        PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
        if(pRef == null) {
            throw new NullPointerException("[Seasons] PlayerRef is null in store.getComponent()");
        }
        HudManager.open(player, pRef, new SeasonsHud(pRef), getId());
    }

    @Override
    public void enable(JavaPlugin plugin) {
        environmentRegistry.register("seasons_ambient", new SeasonsTemperatureHandler(config));

        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonsCycleSystem(config)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

}
