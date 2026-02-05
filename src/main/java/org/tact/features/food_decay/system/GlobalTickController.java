package org.tact.features.food_decay.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.manager.FoodDecayManager;
import org.tact.features.food_decay.manager.FoodStackingManager;

public class GlobalTickController extends EntityTickingSystem<EntityStore> {
    private final FoodDecayConfig config;
    private final FoodDecayManager decayManager;
    private final FoodStackingManager stackingManager;
    private boolean shouldProcessThisTick = false;
    private float timeToProcess = 0.0f;


    public GlobalTickController(
            FoodDecayConfig config,
            FoodDecayManager decayManager,
            FoodStackingManager stackingManager
    ) {
        this.config = config;
        this.decayManager = decayManager;
        this.stackingManager = stackingManager;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        if (index == 0) {
            decayManager.updateClock(deltaTime);
            stackingManager.clearTransactionCache();

            if (decayManager.getGlobalAccumulatedTime() >= config.decayInterval) {
                shouldProcessThisTick = true;
                timeToProcess = decayManager.getGlobalAccumulatedTime();
                decayManager.resetClock();
            } else {
                shouldProcessThisTick = false;
            }
        }
    }

    public boolean shouldProcess() { return shouldProcessThisTick; }
    public float getTimeToProcess() { return timeToProcess; }

    @Override
    public Query<EntityStore> getQuery() { return Query.and(Player.getComponentType()); }
}