package org.tact.features.hunger;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.common.ui.HudManager;
import org.tact.features.hunger.component.HungerComponent;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.hunger.manager.FoodConsumptionManager;
import org.tact.features.hunger.system.HungerSystem;
import org.tact.features.hunger.ui.HungerHud;

public class HungerFeature implements Feature {
    private final HungerConfig config;
    private final FoodConsumptionManager foodManager;

    public HungerFeature(HungerConfig config) {
        this.config = config;
        this.foodManager = new FoodConsumptionManager(config);
    }

    @Override
    public String getId() {
        return "hunger";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        HungerComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(HungerComponent.class, "hunger_component", HungerComponent.CODEC);
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

                if(store.getComponent(ref, HungerComponent.getComponentType()) == null) {
                    store.addComponent(ref, HungerComponent.getComponentType());
                }

                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                HudManager.open(player, playerRef, new HungerHud(playerRef), getId());

                Inventory playerInventory = player.getInventory();
                ItemContainer hotbar = playerInventory.getHotbar();

                if (hotbar != null) {
                    hotbar.registerChangeEvent(changeEvent -> {
                        if (changeEvent.transaction() instanceof ItemStackSlotTransaction) {
                            ItemStackSlotTransaction transaction = (ItemStackSlotTransaction) changeEvent.transaction();
                            foodManager.handleTransaction(player, transaction);
                        }
                    });
                }
            });
        });


    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new HungerSystem(config));
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
