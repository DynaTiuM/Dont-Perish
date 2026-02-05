package org.tact.features.food_decay.modifier;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;

import javax.annotation.Nullable;

public interface FoodDecayModifier {

    double getMultiplier(World world, @Nullable Ref<EntityStore> playerRef, @Nullable String blockId);
}