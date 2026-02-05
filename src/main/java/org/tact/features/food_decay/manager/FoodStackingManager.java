package org.tact.features.food_decay.manager;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.*;
import org.tact.features.food_decay.config.FoodDecayConfig;

import java.util.*;

public class FoodStackingManager {

    private final FoodDecayConfig config;
    private final Set<ItemContainer> hookedContainers = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<String> processedSignatures = new HashSet<>();

    public FoodStackingManager(FoodDecayConfig config) {
        this.config = config;
    }

    public void registerPlayer(Player player) {
        if (player.getInventory() != null) {
            ItemContainer storage = player.getInventory().getStorage();
            ItemContainer hotbar = player.getInventory().getHotbar();

            storage.registerChangeEvent(ev -> this.handleContainerChange(player, ev));
            hookedContainers.add(storage);

            hotbar.registerChangeEvent(ev -> this.handleContainerChange(player, ev));
            hookedContainers.add(hotbar);
        }
    }

    public void clearTransactionCache() {
        processedSignatures.clear();
    }

    private void handleContainerChange(Player player, ItemContainer.ItemContainerChangeEvent event) {
        Transaction transaction = event.transaction();

        if (processedSignatures.contains(String.valueOf(transaction.hashCode()))) {
            return;
        }
        if (transaction instanceof MoveTransaction<?> moveTx && moveTx.succeeded()) {

            ItemContainer other = moveTx.getOtherContainer();
            if (!hookedContainers.contains(other) && other != event.container())
            {
                other.registerChangeEvent(ev -> this.handleContainerChange(player, ev));
                hookedContainers.add(other);
            }

            SlotTransaction removeTx = moveTx.getRemoveTransaction();
            if (moveTx.getAddTransaction() instanceof SlotTransaction addSlotTx) {

                ItemStack held = removeTx.getSlotBefore();
                ItemStack inSlot = addSlotTx.getSlotBefore();

                if (held != null && inSlot != null && held.getItemId().equals(inSlot.getItemId())) {
                    if (addSlotTx.getAction() == ActionType.REPLACE) {

                        ItemContainer destCont;
                        ItemContainer sourceCont;

                        if (moveTx.getMoveType() == MoveType.MOVE_TO_SELF) {
                            destCont = event.container();
                            sourceCont = moveTx.getOtherContainer();
                        } else {
                            return;
                        }
                        String signature = String.valueOf(transaction.hashCode());
                        if (processedSignatures.contains(signature)) {
                            return;
                        }
                        processedSignatures.add(signature);

                        mergeStacks(player, destCont, addSlotTx.getSlot(), sourceCont, removeTx.getSlot(), held, inSlot);
                    }
                }
            }
        }
    }

    private void mergeStacks(Player player, ItemContainer destContainer, short destSlot, ItemContainer sourceContainer, short sourceSlot, ItemStack held, ItemStack inSlot) {
        int totalQty = held.getQuantity() + inSlot.getQuantity();

        if (totalQty <= inSlot.getItem().getMaxStack()) {
            double durHeld = getRealDurability(held);
            double durSlot = getRealDurability(inSlot);

            double avg = ((durHeld * held.getQuantity()) + (durSlot * inSlot.getQuantity())) / totalQty;
            double finalDurability = Math.floor(avg);

            destContainer.setItemStackForSlot(destSlot, inSlot.withQuantity(totalQty).withDurability(finalDurability));

            if (sourceSlot != -1) {
                sourceContainer.setItemStackForSlot(sourceSlot, ItemStack.EMPTY);
            } else {
                player.getInventory().getCombinedEverything().setItemStackForSlot((short)-1, ItemStack.EMPTY);
            }

            player.sendMessage(Message.raw("Stacking"));
        }
    }

    private double getRealDurability(ItemStack item) {
        double current = item.getDurability();
        if (current <= 0.1) {
            double maxDecay = config.getDecayTime(item.getItemId());
            return maxDecay > 0 ? maxDecay : current;
        }
        return current;
    }
}