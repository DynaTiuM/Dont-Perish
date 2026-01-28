package org.tact.features.seasons;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
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
import java.util.concurrent.ConcurrentHashMap;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;
    private ComponentType<EntityStore, TemperatureComponent> temperatureComponentType;

    private final Map<World, Ref<EntityStore>> seasonManagers = new ConcurrentHashMap<>();

    public SeasonsFeature(SeasonsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        SeasonWorldComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(SeasonWorldComponent.class,"season_world_component", SeasonWorldComponent.CODEC);

        temperatureComponentType = plugin.getEntityStoreRegistry()
                .registerComponent(TemperatureComponent.class, TemperatureComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            World world = player.getWorld();

            getOrCreateSeasonManager(world);
            player.sendMessage(Message.raw("[Seasons] New Season Manager created"));

            setupPlayer(player);
            player.sendMessage(Message.raw("[Seasons] Player set up"));
        });
    }

    private Ref<EntityStore> getOrCreateSeasonManager(World world) {
        if (seasonManagers.containsKey(world)) {
            Ref<EntityStore> existingRef = seasonManagers.get(world);
            if (existingRef != null && existingRef.isValid()) {
                return existingRef;
            }
        }
        EntityStore entityStore = world.getEntityStore();
        Store<EntityStore> store = entityStore.getStore();

        Ref<EntityStore> foundManager = findExistingSeasonManager(store);
        if (foundManager != null) {
            seasonManagers.put(world, foundManager);
            System.out.println("[Seasons] Found existing Season Manager for world: " + world.getName());
            return foundManager;
        }

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        Ref<EntityStore> newManager = store.addEntity(holder, AddReason.SPAWN);

        store.addComponent(newManager, SeasonWorldComponent.getComponentType());
        SeasonWorldComponent comp = store.getComponent(newManager, SeasonWorldComponent.getComponentType());
        if (comp != null) {
            comp.setCurrentSeason(Season.SPRING);
            comp.setSeasonProgress(0.0f);
        }
        seasonManagers.put(world, newManager);

        System.out.println("[Seasons] Created new Season Manager for world: " + world.getName());
        return newManager;
    }

    private Ref<EntityStore> findExistingSeasonManager(Store<EntityStore> store) {
        final Ref<EntityStore>[] found = new Ref[]{null};

        store.forEachChunk((chunk, commandBuffer) -> {
            if (found[0] != null) return;

            if (chunk.getComponent(0, SeasonWorldComponent.getComponentType()) != null) {
                for (int i = 0; i < chunk.size(); i++) {
                    SeasonWorldComponent comp = chunk.getComponent(i, SeasonWorldComponent.getComponentType());
                    if (comp != null) {
                        found[0] = chunk.getReferenceTo(i);
                        System.out.println("[Seasons] Found existing manager - Season: " + comp.getCurrentSeason() + ", Timer: " + comp.getSeasonTimer());
                        return;
                    }
                }
            }
        });

        return found[0];
    }

    private void setupPlayer(Player player) {
        Ref<EntityStore> playerRef = player.getReference();
        Store<EntityStore> store = playerRef.getStore();

        TemperatureComponent existingComp = store.getComponent(playerRef, temperatureComponentType);
        if (existingComp == null) {
            store.addComponent(playerRef, temperatureComponentType);
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
                new TemperatureSystem(temperatureComponentType, config, this)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    public Season getCurrentSeason(Player player, Store<EntityStore> store) {
        Ref<EntityStore> managerRef = getOrCreateSeasonManager(player.getWorld());

        SeasonWorldComponent comp = store.getComponent(managerRef, SeasonWorldComponent.getComponentType());

        return comp != null ? comp.getCurrentSeason() : Season.SPRING;
    }

    public float getSeasonProgress(Player player, Store<EntityStore> store) {
        Ref<EntityStore> managerRef = getOrCreateSeasonManager(player.getWorld());

        SeasonWorldComponent comp = store.getComponent(managerRef, SeasonWorldComponent.getComponentType());

        return comp != null ? comp.getSeasonProgress() : 0.0F;
    }
}
