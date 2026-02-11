package org.tact.features.itemStats.util;

import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
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

    private record CalculationContext(
            Player player, InteractionManager interactionManager,
            ItemStatsConfig config, float deltaTime,
            UsageBufferComponent buffer, boolean applyDamage
    ) {}

    public static ItemStatSnapshot calculate(
            Player player, InteractionManager interactionManager,
            ItemStatsConfig config, float deltaTime,
            UsageBufferComponent buffer, boolean applyDamage
    ) {
        CalculationContext ctx = new CalculationContext(player, interactionManager, config, deltaTime, buffer, applyDamage);
        ItemStatSnapshot totals = new ItemStatSnapshot();
        Inventory inv = player.getInventory();

        scanContainer(totals, inv.getArmor(), ScanLocation.ARMOR, ctx);

        ItemContainer hotbar = inv.getHotbar();
        scanSlot(totals, inv.getItemInHand(), hotbar, inv.getActiveHotbarSlot(), ScanLocation.HAND, ctx);

        scanContainer(totals, inv.getCombinedStorageFirst(), ScanLocation.INVENTORY, ctx);

        return finalizeStats(totals);
    }

    private static void scanContainer(ItemStatSnapshot totals, ItemContainer container, ScanLocation loc, CalculationContext ctx) {
        for (short i = 0; i < container.getCapacity(); i++) {
            scanSlot(totals, container.getItemStack(i), container, i, loc, ctx);
        }
    }

    private static void scanSlot(ItemStatSnapshot totals, ItemStack stack, ItemContainer container, short slot, ScanLocation loc, CalculationContext ctx) {
        if (stack == null || stack.isEmpty()) return;

        ItemStats stats = ctx.config().getStats(stack.getItemId());
        if (stats == null) return;

        if (isItemActive(stats, loc, ctx)) {
            applyStats(totals, stats);

            if (ctx.applyDamage() && ctx.buffer() != null) {
                String slotKey = loc.name() + "_" + slot;

                if (loc == ScanLocation.HAND && stats.requireUsage) {
                    updateDurability(stack, container, slot, slotKey, ctx, 1.0F);
                }
                else if (loc == ScanLocation.ARMOR) {
                    // updateDurability(stack, container, slot, slotKey, ctx, 0.1F);
                }
            }
        }
    }

    private static boolean isItemActive(ItemStats stats, ScanLocation loc, CalculationContext ctx) {
        return switch (loc) {
            case ARMOR -> stats.activeInArmor;

            case INVENTORY -> stats.activeInInventory;

            case HAND -> {
                if (!stats.activeInHand) yield false;

                if (stats.requireUsage) {
                    yield isUsingItem(ctx.interactionManager());
                }

                yield true;
            }
        };
    }

    private static void updateDurability(ItemStack stack, ItemContainer container, short slot, String slotKey, CalculationContext ctx, float lossPerSec) {
        UsageBufferComponent buffer = ctx.buffer();
        buffer.addDamage(slotKey, lossPerSec * ctx.deltaTime());
        buffer.addSyncTime(ctx.deltaTime());

        double currentDura = stack.getDurability();
        float accumulated = buffer.getDamage(slotKey);

        if (accumulated >= 1.0F || currentDura - accumulated <= 0) {
            if (buffer.shouldSync() || currentDura - accumulated <= 0) {
                int toApply = (int) Math.floor(accumulated);
                double nextDura = Math.max(0, currentDura - toApply);

                container.setItemStackForSlot(slot, nextDura > 0 ? stack.withDurability(nextDura) : null);

                buffer.consumeDamage(slotKey, toApply);
                buffer.resetSyncTimer();
            }
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

    private static ItemStatSnapshot finalizeStats(ItemStatSnapshot totals) {
        totals.insulationCooling = Math.min(totals.insulationCooling, 0.95f);
        totals.insulationHeating = Math.min(totals.insulationHeating, 0.95f);
        return totals;
    }

    public static boolean isUsingItem(InteractionManager im) {
        if (im == null) return false;
        return im.getChains().values().stream()
                .anyMatch(c -> c.getType() == InteractionType.Secondary && c.getServerState() == InteractionState.NotFinished);
    }
}