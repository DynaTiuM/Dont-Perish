package org.tact.features.seasons.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.SeasonsFeature;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.ui.SeasonHud;

public class TemperatureSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, TemperatureComponent> temperatureComponentType;
    private final SeasonsConfig config;
    private final SeasonsFeature seasonsFeature;

    private DamageCause heatDamageCause;
    private DamageCause coldDamageCause;

    public TemperatureSystem(
        ComponentType<EntityStore, TemperatureComponent> temperatureComponentType,
        SeasonsConfig config,
        SeasonsFeature seasonsFeature
    ) {
        this.temperatureComponentType = temperatureComponentType;
        this.config = config;
        this.seasonsFeature = seasonsFeature;
    }


    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        TemperatureComponent temperatureComponent = archetypeChunk.getComponent(index, temperatureComponentType);
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

        Season currentSeason = seasonsFeature.getCurrentSeason(player, store);
        float seasonProgress = seasonsFeature.getSeasonProgress(player, store);

        float targetTemperature = calculateTargetTemperature(currentSeason, player);
        temperatureComponent.setTargetTemperature(targetTemperature);

        float currentTemp = temperatureComponent.getCurrentTemperature();
        float tempDiff = targetTemperature - currentTemp;
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
        HudManager.ifPresent(player, "seasons", SeasonHud.class, hud -> {
            hud.updateValues(
                    season,
                    progress,
                    tempComp.getCurrentTemperature(),
                    tempComp.hasProtection()
            );
        });
    }

    private float calculateTargetTemperature(Season season, Player player) {
        float baseTemperature = config.getSeasonBaseTemp(season.ordinal());

        // TODO: Possible modifiers: Biomes, Hour, Interior/Exterior, Altitude

        return baseTemperature;
    }

    private boolean checkProtection(Player player, Store<EntityStore> store, Season season) {
        // TODO: Verify the inventory of the player and holding item
        return false;
    }

    private boolean isExtremeTemperature(float temp) {
        return temp > (25.0f + config.extremeTemperatureThreshold) ||
            temp < (15.0f - config.extremeTemperatureThreshold);
    }

    private void applyTemperatureDamage(
        Ref<EntityStore> entityRef,
        CommandBuffer<EntityStore> commandBuffer,
        float temperature
    ) {
        // TODO: manage the temperature isHot logic inside the config
        boolean isHot = temperature > 30.0F;
        float damageAmount = isHot ? config.heatDamage : config.coldDamage;

        DamageCause cause = isHot ? getHeatDamageCause() : getColdDamageCause();
        Damage damage = new Damage(Damage.NULL_SOURCE, cause, damageAmount);

        DamageSystems.executeDamage(entityRef, commandBuffer, damage);
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
