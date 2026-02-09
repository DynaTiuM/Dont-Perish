package org.tact.features.comfort;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.environment.EnvironmentRegistry;
import org.tact.common.ui.HudManager;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.comfort.config.ComfortConfig;
import org.tact.features.comfort.handler.ComfortEnvironmentHandler;
import org.tact.features.comfort.system.ComfortBlockSystem;
import org.tact.features.comfort.system.ComfortDamageSystem;
import org.tact.features.comfort.system.ComfortSystem;
import org.tact.features.comfort.ui.ComfortHud;
import org.tact.features.itemStats.config.ItemStatsConfig;

public class ComfortFeature implements Feature {
    private final ComfortConfig config;
    private final ItemStatsConfig itemConfig;
    private final EnvironmentRegistry environmentRegistry;

    public ComfortFeature(
            ComfortConfig config,
            ItemStatsConfig itemConfig,
            EnvironmentRegistry environmentRegistry
    ) {
        this.config = config;
        this.itemConfig = itemConfig;
        this.environmentRegistry = environmentRegistry;
    }

    @Override
    public String getId() {
        return "comfort";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        ComfortComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(ComfortComponent.class, "comfort_component", ComfortComponent.CODEC);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {

    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            Ref<EntityStore> ref = player.getReference();
            Store<EntityStore> store = ref.getStore();

            if (store.getComponent(ref, ComfortComponent.getComponentType()) == null) {
                store.addComponent(ref, ComfortComponent.getComponentType());
            }

            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            HudManager.open(player, playerRef, new ComfortHud(playerRef), getId());
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
        environmentRegistry.register("comfort", new ComfortEnvironmentHandler(config));
        plugin.getEntityStoreRegistry().registerSystem(new ComfortSystem(config, itemConfig));
        plugin.getEntityStoreRegistry().registerSystem(new ComfortDamageSystem(config));
        plugin.getEntityStoreRegistry().registerSystem(new ComfortBlockSystem(config));

    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

}
