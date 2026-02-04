package org.tact.features.hunger;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.hunger.component.HungerComponent;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.hunger.system.HungerSystem;
import org.tact.features.hunger.ui.HungerHud;

public class HungerFeature implements Feature {
    private final HungerConfig config;

    public HungerFeature(HungerConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "hunger";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        HungerComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(HungerComponent.class, HungerComponent::new);
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

            store.addComponent(ref, HungerComponent.getComponentType());

            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            HudManager.open(player, playerRef, new HungerHud(playerRef), getId());
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new HungerSystem(HungerComponent.getComponentType(), config));
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
