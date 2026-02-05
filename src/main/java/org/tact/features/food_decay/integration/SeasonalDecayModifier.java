package org.tact.features.food_decay.integration;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.features.food_decay.modifier.FoodDecayModifier;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;

public class SeasonalDecayModifier implements FoodDecayModifier {
    @Override
    public double getMultiplier(World world, Ref<EntityStore> playerRef, String blockId) {
        if (playerRef == null) return 1.0;

        SeasonsResource data = playerRef.getStore().getResource(SeasonsResource.TYPE);
        Season currentSeason = data.getCurrentSeason();

        if (currentSeason == Season.SUMMER) return 1.2;
        if (currentSeason == Season.WINTER) return 0.8;

        return 1.0;
    }
}
