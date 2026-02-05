package org.tact.features.hunger.manager;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.hunger.component.HungerComponent;
import org.tact.features.hunger.config.HungerConfig;

public class FoodConsumptionManager {
    private final HungerConfig config;

    public FoodConsumptionManager(
            HungerConfig config
    ) {
        this.config = config;
    }

    public void handleTransaction(Player player, ItemStackSlotTransaction transaction) {
        ItemStack itemBefore = transaction.getSlotBefore();
        ItemStack itemAfter = transaction.getSlotAfter();

        if (itemBefore == null) return;

        String itemId = itemBefore.getItemId();
        if (itemAfter != null && !itemAfter.getItemId().equals(itemId)) return;

        int qtyBefore = itemBefore.getQuantity();
        int qtyAfter = (itemAfter != null) ? itemAfter.getQuantity() : 0;

        if (qtyAfter == qtyBefore - 1) {
            HungerConfig.NutritionValue properties = config.getNutrition(itemId);

            if (properties.hunger > 0) {
                addToHungerBuffer(player, properties.hunger);
            }

            if(properties.comfort > 0) {
                addToComfortBuffer(player, properties.comfort);
            }
        }
    }

    private void addToHungerBuffer(Player player, float amount) {
        Ref<EntityStore> playerRef = player.getReference();
        Store<EntityStore> store = playerRef.getStore();

        HungerComponent hungerComponent = store.getComponent(playerRef, HungerComponent.getComponentType());

        if(hungerComponent != null) hungerComponent.addDigestionBuffer(amount);
    }

    private void addToComfortBuffer(Player player, float amount) {
        Ref<EntityStore> playerRef = player.getReference();
        Store<EntityStore> store = playerRef.getStore();

        ComfortComponent comfortComponent = store.getComponent(playerRef, ComfortComponent.getComponentType());
        if (comfortComponent != null) comfortComponent.addComfortBuffer(amount);
    }
}
