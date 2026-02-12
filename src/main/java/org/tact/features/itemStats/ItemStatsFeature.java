package org.tact.features.itemStats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.core.systems.aura.component.AuraComponent;
import org.tact.features.itemStats.component.UsageBufferComponent;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.system.ItemAuraSystem;
import org.tact.features.itemStats.system.PassiveItemSystem;

public class ItemStatsFeature implements Feature {
    private final ItemStatsConfig config;

    public ItemStatsFeature(ItemStatsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() { return "items"; }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        UsageBufferComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(UsageBufferComponent.class, UsageBufferComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {

    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            player.getWorld().execute(() -> {
                Ref<EntityStore> ref = player.getReference();
                Store<EntityStore> store = ref.getStore();

                if (store.getComponent(ref, UsageBufferComponent.getComponentType()) == null) {
                    store.putComponent(ref, UsageBufferComponent.getComponentType(), new UsageBufferComponent());
                }
                if (store.getComponent(ref, AuraComponent.getComponentType()) == null) {
                    store.putComponent(ref, AuraComponent.getComponentType(), new AuraComponent());
                }
            });
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new PassiveItemSystem(config));
        plugin.getEntityStoreRegistry().registerSystem(new ItemAuraSystem(config));
    }
    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}