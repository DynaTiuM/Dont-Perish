package org.tact.features.seasons;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.system.SeasonCycleSystem;
import org.tact.features.seasons.system.TemperatureSystem;
import org.tact.features.seasons.ui.SeasonHud;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;
    private ComponentType<EntityStore, SeasonWorldComponent> seasonComponent;
    private ComponentType<EntityStore, TemperatureComponent> temperatureComponent;
    private final Map<World, Ref<EntityStore>> worldReferences = new ConcurrentHashMap<>();

    public SeasonsFeature(SeasonsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        seasonComponent = plugin.getEntityStoreRegistry()
                .registerComponent(SeasonWorldComponent.class, SeasonWorldComponent::new);
        temperatureComponent = plugin.getEntityStoreRegistry()
                .registerComponent(TemperatureComponent.class, TemperatureComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            Ref<EntityStore> playerRef = player.getReference();
            Store<EntityStore> store = playerRef.getStore();
            World world = player.getWorld();
            if (!worldReferences.containsKey(world)) {
                UUID worldUUID = world.getWorldConfig().getUuid();
                Ref<EntityStore> worldRef = world.getEntityRef(worldUUID);

                if (worldRef != null) {
                    store.addComponent(worldRef, seasonComponent);
                    worldReferences.put(world, worldRef);
                    player.sendMessage(Message.raw("[Seasons] Initialized season system for world: " + world.getName()));
                }
                else {
                    player.sendMessage(Message.raw("[Seasons] World Reference not found: " + world.getName()));
                }
            }

            store.addComponent(playerRef, temperatureComponent);

            PlayerRef pRef = store.getComponent(playerRef, PlayerRef.getComponentType());
            HudManager.open(player, pRef, new SeasonHud(pRef), getId());

            player.sendMessage(Message.raw("[Seasons] Added temperature component to player: " + player.getDisplayName()));
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonCycleSystem(seasonComponent, config)
        );
        plugin.getEntityStoreRegistry().registerSystem(
                new TemperatureSystem(temperatureComponent, config, this)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    public Ref<EntityStore> getWorldReference(Player player) {
        World world = player.getWorld();
        return worldReferences.get(world);
    }

    public Season getCurrentSeason(Player player, Store<EntityStore> store) {
        Ref<EntityStore> worldRef = getWorldReference(player);
        if (worldRef == null) {
            return Season.SPRING;
        }

        SeasonWorldComponent seasonComp = store.getComponent(worldRef, seasonComponent);
        return seasonComp.getCurrentSeason();
    }

    public float getSeasonProgress(Player player, Store<EntityStore> store) {
        Ref<EntityStore> worldRef = getWorldReference(player);
        if (worldRef == null) {
            return 0.0f;
        }

        SeasonWorldComponent seasonComp = store.getComponent(worldRef, seasonComponent);
        return seasonComp.getSeasonProgress();
    }

    public ComponentType<EntityStore, SeasonWorldComponent> getSeasonComponent() {
        return seasonComponent;
    }

    public ComponentType<EntityStore, TemperatureComponent> getTemperatureComponent() {
        return temperatureComponent;
    }
}
