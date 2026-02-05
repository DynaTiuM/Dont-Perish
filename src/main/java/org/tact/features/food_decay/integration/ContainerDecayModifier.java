package org.tact.features.food_decay.integration;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.features.food_decay.modifier.FoodDecayModifier;

public class ContainerDecayModifier implements FoodDecayModifier {
    @Override
    public double getMultiplier(World world, Ref<EntityStore> playerRef, String blockId) {
        if (blockId == null) return 1.0;

        if (blockId.contains("DP_Fridge")) {
            return 0.6;
        }

        return 1.0;
    }
}