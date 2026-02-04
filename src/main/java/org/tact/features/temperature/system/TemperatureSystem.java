package org.tact.features.temperature.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.temperature.config.TemperatureConfig;
import org.tact.features.temperature.ui.TemperatureHud;

import java.time.LocalDateTime;

public class TemperatureSystem extends EntityTickingSystem<EntityStore> {
    private final TemperatureConfig config;

    private DamageCause heatDamageCause;
    private DamageCause coldDamageCause;


    public TemperatureSystem(
            TemperatureConfig config
    ) {
        this.config = config;
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
        TemperatureComponent temperatureComponent = archetypeChunk.getComponent(index, TemperatureComponent.getComponentType());
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
        WorldTimeResource timeData = store.getResource(WorldTimeResource.getResourceType());

        if(temperatureComponent == null) {
            return;
        }

        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
        if (statMap == null) return;

        EntityStatValue temperatureStat = statMap.get(getTemperatureStatIndex());
        if (temperatureStat == null) return;

        float currentTemp = temperatureStat.get();

        float timeModifier = calculateTimeModifier(timeData);

        // Exterior temperature
        float targetTemperature = calculateTargetTemperature(temperatureComponent, timeModifier);
        temperatureComponent.setTargetTemperature(targetTemperature);

        float tempDiff = targetTemperature - currentTemp;

        // Temperature of the player
        float newTemperature = currentTemp + tempDiff * Math.min(deltaTime * config.temperatureTransitionSpeed, 1.0F);

        if (newTemperature != currentTemp) {
            statMap.setStatValue(getTemperatureStatIndex(), newTemperature);
        }
        temperatureComponent.setLerpedTemperature(newTemperature);

        boolean hasProtection = checkProtection(player, store, newTemperature);
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

        updateHud(player, temperatureComponent);
    }

    private int temperatureStatIndex = -1;
    private int getTemperatureStatIndex() {
        if (temperatureStatIndex == -1) {
            temperatureStatIndex = EntityStatType.getAssetMap().getIndex("Temperature");
        }
        return temperatureStatIndex;
    }

    private void updateHud(
            Player player,
            TemperatureComponent temperatureComponent
    ) {

        HudManager.updateChild(player, "temperature", TemperatureHud.class, (hud, builder) -> {
            hud.render(builder, temperatureComponent.getLerpedTemperature());
        });
    }

    private float calculateTargetTemperature(TemperatureComponent temperatureComponent, float timeModifier) {

        // Modifier 0: Base Temperature (without any influence)
        float baseTemperature = config.defaultBaseTemperature;

        // Modifier 1: Season (if feature activated)
        float seasonal = temperatureComponent.getSeasonalModifier();
        // Modifier 2: Environment (blocks)
        float environment = temperatureComponent.getEnvironmentModifier();

        float totalTemperature = baseTemperature + environment + seasonal + timeModifier;

        return totalTemperature;
    }

    private float calculateTimeModifier(WorldTimeResource timeResource) {
        if (timeResource == null) {
            return 0;
        }

        LocalDateTime gameTime = timeResource.getGameDateTime();

        int hour = gameTime.getHour();
        int minute = gameTime.getMinute();

        float preciseHour = hour + (minute / 60.0f);

        return (float) Math.cos(((preciseHour - 14.0f) / 24.0f) * 2.0f * Math.PI) * config.dayNightTemperatureVariation;
    }


    private boolean checkProtection(Player player, Store<EntityStore> store, float temperature) {
        // TODO: Verify the inventory of the player and holding item
        return false;
    }

    private boolean isExtremeTemperature(float temp) {
        return temp > (35.0F + config.extremeTemperatureThreshold) ||
            temp < (0.0F - config.extremeTemperatureThreshold);
    }

    private void applyTemperatureDamage(
        Ref<EntityStore> entityRef,
        CommandBuffer<EntityStore> commandBuffer,
        float temperature
    ) {
        // TODO: manage the temperature isHot logic inside the config
        boolean isHot = temperature > 35.0F;
        float damageAmount = isHot ? config.heatDamage : config.coldDamage;

        DamageCause cause = isHot ? getHeatDamageCause() : getColdDamageCause();
        Damage damage = new Damage(Damage.NULL_SOURCE, cause, damageAmount);

        DamageSystems.executeDamage(entityRef, commandBuffer, damage);
        if (config.staminaLoss) {
            Store<EntityStore> store = commandBuffer.getStore();
            EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
            int staminaIdx = DefaultEntityStatTypes.getStamina();
            if(statMap == null) {
                throw new NullPointerException("[Temperature] statMap is null");
            }
            statMap.subtractStatValue(staminaIdx, this.config.staminaDrainAmount);
        }
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
        return Query.and(Player.getComponentType(), TemperatureComponent.getComponentType());
    }
}
