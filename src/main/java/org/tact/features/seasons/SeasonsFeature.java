package org.tact.features.seasons;

<<<<<<< HEAD
<<<<<<< HEAD
import com.hypixel.hytale.component.*;
=======
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
=======
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
<<<<<<< HEAD
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.resource.SeasonResource;
=======
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
import org.tact.features.seasons.system.SeasonCycleSystem;
import org.tact.features.seasons.system.TemperatureSystem;
import org.tact.features.seasons.ui.SeasonHud;

<<<<<<< HEAD
public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;
=======
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonsFeature implements Feature {
    private final SeasonsConfig config;
<<<<<<< HEAD
    private ComponentType<EntityStore, SeasonWorldComponent> seasonComponent;
    private ComponentType<EntityStore, TemperatureComponent> temperatureComponent;
    private final Map<World, Ref<EntityStore>> worldReferences = new ConcurrentHashMap<>();
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
=======
    private ComponentType<EntityStore, TemperatureComponent> temperatureComponentType;

    private final Map<World, Ref<EntityStore>> seasonManagers = new ConcurrentHashMap<>();
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)

    public SeasonsFeature(SeasonsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "seasons";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
<<<<<<< HEAD
<<<<<<< HEAD
        SeasonResource.TYPE = plugin.getEntityStoreRegistry()
                .registerResource(SeasonResource.class,"season_resource", SeasonResource.CODEC);

        TemperatureComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(TemperatureComponent.class, "temperature_component", TemperatureComponent.CODEC);
=======
        seasonComponent = plugin.getEntityStoreRegistry()
                .registerComponent(SeasonWorldComponent.class, SeasonWorldComponent::new);
        temperatureComponent = plugin.getEntityStoreRegistry()
=======
        SeasonWorldComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(SeasonWorldComponent.class,"season_world_component", SeasonWorldComponent.CODEC);

        temperatureComponentType = plugin.getEntityStoreRegistry()
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)
                .registerComponent(TemperatureComponent.class, TemperatureComponent::new);
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
<<<<<<< HEAD
<<<<<<< HEAD

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

        PlayerRef playerRef_ = store.getComponent(playerRef, PlayerRef.getComponentType());
        HudManager.open(player, playerRef_, new SeasonHud(playerRef_), getId());
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new SeasonCycleSystem(config)
        );
        plugin.getEntityStoreRegistry().registerSystem(
                new TemperatureSystem(TemperatureComponent.getComponentType(), config)
=======
            Ref<EntityStore> playerRef = player.getReference();
            Store<EntityStore> store = playerRef.getStore();
=======
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)
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
<<<<<<< HEAD
                new TemperatureSystem(temperatureComponent, config, this)
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
=======
                new TemperatureSystem(temperatureComponentType, config, this)
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======
    public Ref<EntityStore> getWorldReference(Player player) {
        World world = player.getWorld();
        return worldReferences.get(world);
    }

=======
>>>>>>> 8f8d73b (feat: Seasons Night & Day variations & Seasons CODEC but Persistence still not working)
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
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
}
