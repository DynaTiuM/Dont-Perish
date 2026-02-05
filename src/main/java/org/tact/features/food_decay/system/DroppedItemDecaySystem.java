package org.tact.features.food_decay.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.features.food_decay.manager.FoodDecayManager;

public class DroppedItemDecaySystem extends EntityTickingSystem<EntityStore> {
    private final FoodDecayManager decayManager;
    private final GlobalTickController controller;

    public DroppedItemDecaySystem(
            FoodDecayManager decayManager,
            GlobalTickController controller
    ) {
        this.decayManager = decayManager;
        this.controller = controller;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(ItemComponent.getComponentType());
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        if (!controller.shouldProcess()) return;

        ItemComponent itemComp = archetypeChunk.getComponent(index, ItemComponent.getComponentType());
        if (itemComp == null || itemComp.getItemStack() == null) return;

        var world = store.getExternalData().getWorld();

        double multiplier = decayManager.calculateMultiplier(world, null, null);

        decayManager.processDroppedItem(itemComp, controller.getTimeToProcess(), multiplier);

    }
}