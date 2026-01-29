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
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.resource.SeasonResource;
import org.tact.features.seasons.system.SeasonCycleSystem;
import org.tact.features.seasons.system.TemperatureSystem;
import org.tact.features.seasons.ui.SeasonHud;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;

    public SeasonsFeature(SeasonsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        SeasonResource.TYPE = plugin.getEntityStoreRegistry()
                .registerResource(SeasonResource.class,"season_resource", SeasonResource.CODEC);

        TemperatureComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(TemperatureComponent.class, "temperature_component", TemperatureComponent.CODEC);
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
        Store<EntityStore> store = playerRef.getStore();

        TemperatureComponent existingComp = store.getComponent(playerRef, TemperatureComponent.getComponentType());
        if (existingComp == null) {
            store.addComponent(playerRef, TemperatureComponent.getComponentType());
        }

        PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
        HudManager.open(player, pRef, new SeasonHud(pRef), getId());
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonCycleSystem(config)
        );
        plugin.getEntityStoreRegistry().registerSystem(
                new TemperatureSystem(TemperatureComponent.getComponentType(), config)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

}
