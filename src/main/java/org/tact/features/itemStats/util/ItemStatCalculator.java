package org.tact.features.itemStats.util;

import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.tact.features.itemStats.component.UsageBufferComponent;
import org.tact.features.itemStats.config.ItemStats;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;

public class ItemStatCalculator {

    private enum ScanLocation { ARMOR, HAND, INVENTORY }

    public static ItemStatSnapshot calculate(
            Player player,
            InteractionManager interactionManager,
            ItemStatsConfig config,
            float deltaTime,
            UsageBufferComponent buffer,
            boolean applyDamage
    ) {
        ItemStatSnapshot totals = new ItemStatSnapshot();
        Inventory inventory = player.getInventory();

        ItemContainer armor = inventory.getArmor();
        for (short i = 0; i < armor.getCapacity(); i++) {
            processItem(totals, armor.getItemStack(i), ScanLocation.ARMOR, config, interactionManager, player, deltaTime, buffer, armor, i, false);
        }

        ItemContainer hotbar = inventory.getHotbar();
        short activeSlot = inventory.getActiveHotbarSlot();
        processItem(totals, inventory.getItemInHand(), ScanLocation.HAND, config, interactionManager, player, deltaTime, buffer, hotbar, activeSlot, applyDamage);

        ItemContainer backpack = inventory.getCombinedStorageFirst();
        for (short i = 0; i < backpack.getCapacity(); i++) {
            processItem(totals, backpack.getItemStack(i), ScanLocation.INVENTORY, config, interactionManager, player, deltaTime, buffer, backpack, i, false);
        }

        totals.insulationCooling = Math.min(totals.insulationCooling, 0.95f);
        totals.insulationHeating = Math.min(totals.insulationHeating, 0.95f);

        return totals;
    }

    private static void processItem(
            ItemStatSnapshot totals,
            ItemStack stack,
            ScanLocation loc,
            ItemStatsConfig config,
            InteractionManager interactionManager,
            Player player,
            float deltaTime,
            UsageBufferComponent buffer,
            ItemContainer container,
            short slot,
            boolean applyDamage
    ) {
        if (stack == null || stack.isEmpty()) return;

        ItemStats stats = config.getStats(stack.getItemId());
        if (stats == null) return;

        boolean active = false;
        switch (loc) {
            case ARMOR -> {
                if (stats.activeInArmor) active = true;
            }
            case HAND -> {
                if (stats.activeInHand) {
                    if (stats.requireUsage) {
                        if (isPlayerUsingItem(interactionManager)) {
                            active = true;
                            if (applyDamage && buffer != null) {
                                applyUsageDurability(stack, deltaTime, buffer, container, slot);
                            }
                        }
                    } else {
                        active = true;
                    }
                }
            }
            case INVENTORY -> {
                if (stats.activeInInventory) active = true;
            }
        }

        if (active) {
            applyStats(totals, stats);
        }
    }

    private static void applyStats(ItemStatSnapshot totals, ItemStats stats) {
        totals.thermalOffset += stats.thermalOffset;
        totals.insulationCooling += stats.insulationCooling;
        totals.insulationHeating += stats.insulationHeating;
        totals.speedModifier += stats.walkSpeedModifier;
        totals.healthChange += stats.healthChangePerSecond;
        totals.staminaDrain += stats.staminaDrainPerSecond;
        totals.comfortModifier += stats.comfortModifier;
    }

    private static void applyUsageDurability(
            ItemStack stack,
            float deltaTime,
            UsageBufferComponent buffer,
            ItemContainer container,
            short slot
    ) {
        float lossPerSecond = 1.0F;
        buffer.addDamage(lossPerSecond * deltaTime);
        buffer.addSyncTime(deltaTime);

        double estimatedDurability = stack.getDurability() - buffer.getAccumulatedDamage();

        if (buffer.getAccumulatedDamage() >= 1.0F || estimatedDurability <= 0) {
            if (buffer.shouldSync() || estimatedDurability <= 0) {
                int damageToApply = (int) Math.floor(buffer.getAccumulatedDamage());
                double newDurability = Math.max(0, stack.getDurability() - damageToApply);

                if (newDurability > 0) {
                    container.setItemStackForSlot(slot, stack.withDurability(newDurability));
                } else {
                    container.setItemStackForSlot(slot, null);
                }

                buffer.consumeDamage(damageToApply);
                buffer.resetSyncTimer();
            }
        }
    }

    private static boolean isPlayerUsingItem(InteractionManager interactionManager) {
        if (interactionManager == null) return false;

        for (InteractionChain chain : interactionManager.getChains().values()) {
            if (chain.getType() == InteractionType.Secondary && chain.getServerState() == InteractionState.NotFinished) {
                return true;
            }
        }
        return false;
    }
}