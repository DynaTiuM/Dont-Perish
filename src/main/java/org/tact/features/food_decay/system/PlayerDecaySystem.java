package org.tact.features.food_decay.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.features.food_decay.manager.FoodDecayManager;

public class PlayerDecaySystem extends EntityTickingSystem<EntityStore> {
    private final FoodDecayManager decayManager;
    private final GlobalTickController controller;

    public PlayerDecaySystem(FoodDecayManager decayManager, GlobalTickController controller) {
        this.decayManager = decayManager;
        this.controller = controller;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float deltaTime, int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> buffer) {
        if (!controller.shouldProcess()) return;

        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player == null) return;

        double multiplier = decayManager.calculateMultiplier(player.getWorld(), chunk.getReferenceTo(index), null);
        decayManager.processContainer(player.getInventory().getCombinedEverything(), controller.getTimeToProcess(), multiplier);
    }
}
