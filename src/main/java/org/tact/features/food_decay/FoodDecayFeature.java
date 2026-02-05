package org.tact.features.food_decay;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.integration.ContainerDecayModifier;
import org.tact.features.food_decay.integration.SeasonalDecayModifier;
import org.tact.features.food_decay.manager.FoodDecayManager;
import org.tact.features.food_decay.system.FoodDecaySystem;

public class FoodDecayFeature implements Feature {
    private final FoodDecayConfig config;

    public FoodDecayFeature(FoodDecayConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "food";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        FoodDecayManager decayManager = new FoodDecayManager(config);
        decayManager.addModifier(new SeasonalDecayModifier());
        decayManager.addModifier(new ContainerDecayModifier());

        plugin.getEntityStoreRegistry().registerSystem(new FoodDecaySystem(config, decayManager));
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getInventory() != null) {
                player.getInventory().getStorage().registerChangeEvent(ev -> this.handleContainerChange(player, ev));
                player.getInventory().getHotbar().registerChangeEvent(ev -> this.handleContainerChange(player, ev));
           }
        });
    }
    private void handleContainerChange(Player player, ItemContainer.ItemContainerChangeEvent event) {
        Transaction transaction = event.transaction();

        if (transaction instanceof MoveTransaction<?> moveTx && moveTx.succeeded()) {
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
                        }

                        else {
                            return;
                        }

                        mergeStacksManually(player, destCont, addSlotTx.getSlot(), sourceCont, removeTx.getSlot(), held, inSlot);
                    }
                }
            }
        }
    }

    private void mergeStacksManually(Player player, ItemContainer destContainer, short destSlot, ItemContainer sourceContainer, short sourceSlot, ItemStack held, ItemStack inSlot) {
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

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
