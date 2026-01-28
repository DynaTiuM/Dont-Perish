package org.tact.features.seasons.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
<<<<<<< HEAD
import com.hypixel.hytale.server.core.Message;
=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
<<<<<<< HEAD
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
<<<<<<< HEAD
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonResource;
import org.tact.features.seasons.ui.SeasonHud;

import java.time.LocalDateTime;

=======
import org.tact.features.seasons.SeasonsFeature;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.ui.SeasonHud;

>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
public class TemperatureSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, TemperatureComponent> temperatureComponentType;
    private final SeasonsConfig config;
<<<<<<< HEAD
=======
    private final SeasonsFeature seasonsFeature;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)

    private DamageCause heatDamageCause;
    private DamageCause coldDamageCause;

    public TemperatureSystem(
<<<<<<< HEAD
            ComponentType<EntityStore, TemperatureComponent> temperatureComponentType,
            SeasonsConfig config
    ) {
        this.temperatureComponentType = temperatureComponentType;
        this.config = config;
    }

=======
        ComponentType<EntityStore, TemperatureComponent> temperatureComponentType,
        SeasonsConfig config,
        SeasonsFeature seasonsFeature
    ) {
        this.temperatureComponentType = temperatureComponentType;
        this.config = config;
        this.seasonsFeature = seasonsFeature;
    }


>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
<<<<<<< HEAD

        SeasonResource data = store.getResource(SeasonResource.TYPE);

=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        TemperatureComponent temperatureComponent = archetypeChunk.getComponent(index, temperatureComponentType);
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

<<<<<<< HEAD
        Season currentSeason = data.getCurrentSeason();
        float seasonProgress = data.getSeasonProgress();

        // Exterior temperature
=======
        Season currentSeason = seasonsFeature.getCurrentSeason(player, store);
        float seasonProgress = seasonsFeature.getSeasonProgress(player, store);

>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
        float targetTemperature = calculateTargetTemperature(currentSeason, player);
        temperatureComponent.setTargetTemperature(targetTemperature);

        float currentTemp = temperatureComponent.getCurrentTemperature();
        float tempDiff = targetTemperature - currentTemp;
<<<<<<< HEAD

        // Temperature of the player
=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
        float newTemperature = currentTemp + tempDiff * Math.min(deltaTime * config.temperatureTransitionSpeed, 1.0F);
        temperatureComponent.setCurrentTemperature(newTemperature);

        boolean hasProtection = checkProtection(player, store, currentSeason);
        temperatureComponent.setHasProtection(hasProtection);

        if(!hasProtection && isExtremeTemperature(newTemperature)) {
            temperatureComponent.addDamageTimer(deltaTime);

            if(temperatureComponent.getDamageTimer() >= config.damageInterval) {
                applyTemperatureDamage(entityRef, commandBuffer, newTemperature);
                temperatureComponent.resetDamageTimer();
            }
        }
        else {
            temperatureComponent.resetDamageTimer();
        }

        updateHud(player, currentSeason, seasonProgress, temperatureComponent);
    }

    private void updateHud(
            Player player,
            Season season,
            float progress,
            TemperatureComponent tempComp
    ) {
<<<<<<< HEAD

        HudManager.updateChild(player, "seasons", SeasonHud.class, (hud, builder) -> {
            hud.render(builder, season, tempComp.getCurrentTemperature());
=======
        HudManager.ifPresent(player, "seasons", SeasonHud.class, hud -> {
            hud.updateValues(
                    season,
                    progress,
                    tempComp.getCurrentTemperature(),
                    tempComp.hasProtection()
            );
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
        });
    }

    private float calculateTargetTemperature(Season season, Player player) {
        float baseTemperature = config.getSeasonBaseTemp(season.ordinal());

<<<<<<< HEAD
        float dayMultiplier = season.getDayLengthMultiplier();

        float hourCorrection = getTimeModifier(player) * (10.0f * dayMultiplier);
        float totalTemperature = baseTemperature + hourCorrection;

        return totalTemperature;
    }

    private float getTimeModifier(Player player) {

        Store<EntityStore> store = player.getWorld().getEntityStore().getStore();

        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());

        if (timeResource == null) {
            return 0;
        }

        LocalDateTime gameTime = timeResource.getGameDateTime();

        int hour = gameTime.getHour();
        int minute = gameTime.getMinute();

        float preciseHour = hour + (minute / 60.0f);

        return (float) Math.cos(((preciseHour - 14.0f) / 24.0f) * 2.0f * Math.PI);
=======
        // TODO: Possible modifiers: Biomes, Hour, Interior/Exterior, Altitude

        return baseTemperature;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    }

    private boolean checkProtection(Player player, Store<EntityStore> store, Season season) {
        // TODO: Verify the inventory of the player and holding item
        return false;
    }

    private boolean isExtremeTemperature(float temp) {
<<<<<<< HEAD
        return temp > (35.0F + config.extremeTemperatureThreshold) ||
            temp < (0.0F - config.extremeTemperatureThreshold);
=======
        return temp > (25.0f + config.extremeTemperatureThreshold) ||
            temp < (15.0f - config.extremeTemperatureThreshold);
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    }

    private void applyTemperatureDamage(
        Ref<EntityStore> entityRef,
        CommandBuffer<EntityStore> commandBuffer,
        float temperature
    ) {
        // TODO: manage the temperature isHot logic inside the config
<<<<<<< HEAD
        boolean isHot = temperature > 35.0F;
=======
        boolean isHot = temperature > 30.0F;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
        float damageAmount = isHot ? config.heatDamage : config.coldDamage;

        DamageCause cause = isHot ? getHeatDamageCause() : getColdDamageCause();
        Damage damage = new Damage(Damage.NULL_SOURCE, cause, damageAmount);

        DamageSystems.executeDamage(entityRef, commandBuffer, damage);
<<<<<<< HEAD
        if (config.staminaLoss) {
            Store<EntityStore> store = commandBuffer.getStore();
            EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
            int staminaIdx = DefaultEntityStatTypes.getStamina();
            statMap.subtractStatValue(staminaIdx, this.config.staminaDrainAmount);
        }
=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    }

    private DamageCause getHeatDamageCause() {
        if (heatDamageCause == null) {
            heatDamageCause = DamageCause.getAssetMap().getAsset("Heat");
        }
        return heatDamageCause;
    }

    private DamageCause getColdDamageCause() {
        if (coldDamageCause == null) {
            coldDamageCause = DamageCause.getAssetMap().getAsset("Cold");
        }
        return coldDamageCause;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), temperatureComponentType);
    }
}
