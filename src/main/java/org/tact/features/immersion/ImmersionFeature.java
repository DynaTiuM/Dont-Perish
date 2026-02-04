package org.tact.features.immersion;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.immersion.ui.ImmersionHud;
import org.tact.features.immersion.system.ImmersionSystem;

public class ImmersionFeature implements Feature {

    @Override
    public String getId() {
        return "immersion";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {

    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new ImmersionSystem());
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            Ref<EntityStore> ref = player.getReference();
            Store<EntityStore> store = ref.getStore();

            PlayerRef pRef = store.getComponent(ref, PlayerRef.getComponentType());
            HudManager.open(player, pRef, new ImmersionHud(pRef), getId());
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}