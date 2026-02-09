package org.tact.features.itemStats.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.tact.features.itemStats.config.ItemStats;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;

public class ItemStatCalculator {

    private enum ScanLocation { ARMOR, HAND, INVENTORY }

    public static ItemStatSnapshot calculate(Player player, ItemStatsConfig config) {
        ItemStatSnapshot totals = new ItemStatSnapshot();
        Inventory inventory = player.getInventory();

        ItemContainer armor = inventory.getArmor();
        for (int i = 0; i < armor.getCapacity(); i++) {
            processItem(player, totals, armor.getItemStack( (short) i), ScanLocation.ARMOR, config);
        }

        processItem(player, totals, inventory.getItemInHand(), ScanLocation.HAND, config);

        ItemContainer backpack = inventory.getCombinedStorageFirst();
        for (int i = 0; i < backpack.getCapacity(); i++) {
            processItem(player, totals, backpack.getItemStack( (short) i), ScanLocation.INVENTORY, config);
        }

        totals.insulationCooling = Math.min(totals.insulationCooling, 0.95f);
        totals.insulationHeating = Math.min(totals.insulationHeating, 0.95f);

        return totals;
    }

    private static void processItem(
            Player player,
            ItemStatSnapshot totals,
            ItemStack stack,
            ScanLocation loc,
            ItemStatsConfig config
    ) {
        if (stack == null || stack.isEmpty()) return;

        ItemStats stats = config.getStats(stack.getItemId());
        if (stats == null) return;
        boolean active = false;
        switch (loc) {
            case ARMOR: if (stats.activeInArmor) active = true; break;
            case HAND: if (stats.activeInHand) active = true; break;
            case INVENTORY: if (stats.activeInInventory) active = true; break;
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
}