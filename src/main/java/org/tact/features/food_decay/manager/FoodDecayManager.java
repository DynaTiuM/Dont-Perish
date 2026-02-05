package org.tact.features.food_decay.manager;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.modifier.FoodDecayModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FoodDecayManager {

    private final FoodDecayConfig config;
    private final List<FoodDecayModifier> modifiers = new ArrayList<>();

    public FoodDecayManager(FoodDecayConfig config) {
        this.config = config;
    }

    public void addModifier(FoodDecayModifier modifier) {
        this.modifiers.add(modifier);
    }
    private float globalAccumulatedTime = 0.0f;

    public void updateClock(float deltaTime) {
        globalAccumulatedTime += deltaTime;
    }

    public float getGlobalAccumulatedTime() {
        return globalAccumulatedTime;
    }

    public void resetClock() {
        globalAccumulatedTime = 0.0f;
    }

    public double calculateMultiplier(World world, @Nullable Ref<EntityStore> playerRef, @Nullable String blockId) {
        double multiplier = 1.0;
        for (FoodDecayModifier modifier : modifiers) {
            multiplier *= modifier.getMultiplier(world, playerRef, blockId);
        }

        return Math.max(0.0, multiplier);
    }

    public void processContainer(ItemContainer container, float deltaTime, double multiplier) {
        if (container == null || multiplier <= 0.0001) return;

        for (short i = 0; i < container.getCapacity(); i++) {
            ItemStack item = container.getItemStack(i);
            ItemStack processedItem = calculateDecayedItem(item, deltaTime, multiplier);

            if (processedItem != item) {
                container.setItemStackForSlot(i, processedItem);
            }
        }
    }

    public void processDroppedItem(ItemComponent itemEntity, float deltaTime, double multiplier) {
        if (itemEntity == null) return;

        ItemStack currentStack = itemEntity.getItemStack();
        ItemStack processedStack = calculateDecayedItem(currentStack, deltaTime, multiplier);

        if (processedStack != currentStack) {
            itemEntity.setItemStack(processedStack);
        }
    }

    private ItemStack calculateDecayedItem(ItemStack item, float deltaTime, double multiplier) {
        if (item == null || item.isEmpty()) return item;

        String itemId = item.getItemId();
        double maxDecayTime = config.getDecayTime(itemId);

        if (maxDecayTime <= 0) return item;

        if (item.getMaxDurability() <= 0) {
            return item.withRestoredDurability(maxDecayTime);
        }

        else {
            double degradation = config.degradationSpeed * deltaTime * multiplier;
            double newDurability = item.getDurability() - degradation;
            double roundedDurability = Math.floor(newDurability);

            if (newDurability <= 0) {
                return new ItemStack("Ingredient_Poop", item.getQuantity());
            } else {
                if (roundedDurability != item.getDurability()) {
                    return item.withDurability(roundedDurability);
                }
            }
        }

        return item;
    }
}