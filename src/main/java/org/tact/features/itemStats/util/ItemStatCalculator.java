package org.tact.features.itemStats.util;

import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.tact.features.itemStats.config.ItemStats;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;

public class ItemStatCalculator {

    private enum ScanLocation { ARMOR, HAND, INVENTORY }

    public static ItemStatSnapshot calculate(
            Player player,
            InteractionManager interactionManager,
            ItemStatsConfig config
    ) {
        ItemStatSnapshot totals = new ItemStatSnapshot();
        Inventory inventory = player.getInventory();

        ItemContainer armor = inventory.getArmor();
        for (int i = 0; i < armor.getCapacity(); i++) {
            processItem(totals, armor.getItemStack( (short) i), ScanLocation.ARMOR, config, interactionManager);
        }

        processItem(totals, inventory.getItemInHand(), ScanLocation.HAND, config, interactionManager);

        ItemContainer backpack = inventory.getCombinedStorageFirst();
        for (int i = 0; i < backpack.getCapacity(); i++) {
            processItem(totals, backpack.getItemStack( (short) i), ScanLocation.INVENTORY, config, interactionManager);
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
            InteractionManager interactionManager
    ) {
        if (stack == null || stack.isEmpty()) return;

        ItemStats stats = config.getStats(stack.getItemId());
        if (stats == null) return;
        boolean active = false;
        switch (loc) {
            case ARMOR:
                if (stats.activeInArmor) {
                    active = true;
                }
                break;
            case HAND:
                if (stats.activeInHand) {
                    if (stats.requireUsage) {
                        if (isPlayerUsingItem(interactionManager)) {
                            active = true;
                        }
                    } else {
                        active = true;
                    }
                }
                break;
            case INVENTORY:
                if (stats.activeInInventory) {
                    active = true;
                }
                break;
        }

        if (active) {
            totals.thermalOffset += stats.thermalOffset;
            totals.insulationCooling += stats.insulationCooling;
            totals.insulationHeating += stats.insulationHeating;

            totals.speedModifier += stats.walkSpeedModifier;
            totals.healthChange += stats.healthChangePerSecond;
            totals.staminaDrain += stats.staminaDrainPerSecond;

            totals.comfortModifier += stats.comfortModifier;
        }
    }

    private static boolean isPlayerUsingItem(InteractionManager interactionManager) {
        if (interactionManager == null) return false;

        for (InteractionChain chain : interactionManager.getChains().values()) {
            if (chain.getType() == InteractionType.Secondary) {
                if (chain.getServerState() == InteractionState.NotFinished) {
                    return true;
                }
            }
        }

        return false;
    }
}