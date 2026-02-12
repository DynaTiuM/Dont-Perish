package org.tact.features.temperature.system;

import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.common.util.TimeUtil;
import org.tact.common.util.WeatherHelper;
import org.tact.features.itemStats.component.UsageBufferComponent;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;
import org.tact.features.itemStats.util.ItemStatCalculator;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.temperature.config.TemperatureConfig;
import org.tact.features.temperature.ui.TemperatureHud;

public class TemperatureSystem extends EntityTickingSystem<EntityStore> {
    private final TemperatureConfig config;

    private int temperatureStatIndex = -1;

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
        TemperatureComponent tempComp = archetypeChunk.getComponent(index, TemperatureComponent.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        if(player == null || tempComp == null || player.getWorld() == null) return;

        updateWeatherCache(player, tempComp, transform, store);

        processTemperatureEvolution(player, tempComp, transform, archetypeChunk, index, deltaTime, commandBuffer);
    }

    private void updateWeatherCache(
            Player player,
            TemperatureComponent tempComp,
            TransformComponent transform,
            Store<EntityStore> store
    ) {
        if (player.getWorld().getTick() % 10 == 0) {
            WeatherResource weatherRes = store.getResource(WeatherResource.getResourceType());
            String weatherId = WeatherHelper.getWeatherId(player, weatherRes, transform);
            tempComp.setLastWeatherId(weatherId);
        }
    }

    private void processTemperatureEvolution(
            Player player,
            TemperatureComponent tempComp,
            TransformComponent transform,
            ArchetypeChunk<EntityStore> chunk,
            int index,
            float deltaTime,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        Ref<EntityStore> playerRef = chunk.getReferenceTo(index);
        Store<EntityStore> store = playerRef.getStore();

        EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
        EntityStatValue tempStat = statMap.get(getTemperatureStatIndex());

        WorldTimeResource timeRes = store.getResource(WorldTimeResource.getResourceType());
        UsageBufferComponent buffer = chunk.getComponent(index, UsageBufferComponent.getComponentType());
        ItemStatSnapshot items = (buffer != null) ? buffer.getLastSnapshot() : new ItemStatSnapshot();

        double altitude = (transform != null) ? transform.getPosition().getY() : config.optimalAltitude;

        float target = calculateTargetTemperature(
                tempComp,
                timeRes,
                tempComp.getLastWeatherId(),
                getSeasonStretch(store),
                altitude,
                items.thermalOffset
        );

        tempComp.setTargetTemperature(target);

        float nextTemp = interpolateTemperature(tempStat.get(), target, deltaTime, items);

        if (nextTemp != tempStat.get()) {
            statMap.setStatValue(getTemperatureStatIndex(), nextTemp);
            tempComp.setLerpedTemperature(nextTemp);
        }

        if (updateDamageTimer(tempComp, nextTemp, deltaTime)) {
            applyTemperatureDamage(playerRef, commandBuffer, nextTemp);
        }

        updateHud(player, tempComp);
    }

    private void updateHud(
            Player player,
            TemperatureComponent temperatureComponent
    ) {

        HudManager.updateChild(player, "temperature", TemperatureHud.class, (hud, builder) -> {
            hud.render(builder, temperatureComponent.getLerpedTemperature());
        });
    }

    private boolean updateDamageTimer(
            TemperatureComponent temperatureComponent,
            float currentTemp,
            float deltaTime
    ) {
        if (!isExtremeTemperature(currentTemp)) {
            temperatureComponent.resetDamageTimer();
            return false;
        }

        temperatureComponent.addDamageTimer(deltaTime);

        if (temperatureComponent.getDamageTimer() >= config.damageInterval) {
            temperatureComponent.resetDamageTimer();
            return true;
        }

        return false;
    }

    private float calculateTargetTemperature(
            TemperatureComponent temperatureComponent,
            WorldTimeResource timeResource,
            String weatherId,
            float dayLengthMultiplier,
            double playerY,
            float equipmentOffset
    ) {
        float timeModifier = calculateTimeModifier(timeResource, dayLengthMultiplier);

        // Modifier 0: Base Temperature (without any influence)
        float baseTemperature = config.defaultBaseTemperature;

        // Modifier 1: Season (if feature activated)
        float seasonal = temperatureComponent.getSeasonalModifier();
        // Modifier 2: Environment (blocks)
        float environment = temperatureComponent.getEnvironmentModifier();
        // Modifier 3: Altitude
        float altitude = calculateAltitudeModifier(playerY);
        // Modifier 4: Weather
        float weather = calculateWeatherModifier(weatherId);

        return baseTemperature + environment + seasonal + timeModifier + altitude + equipmentOffset + weather;
    }

    private float calculateTimeModifier(WorldTimeResource timeResource, float dayLengthMultiplier) {
        if (timeResource == null) return 0.0F;

        float preciseHour = TimeUtil.getPreciseHour(timeResource);
        float cycleFactor = TimeUtil.getSeasonalDayCycleFactor(preciseHour, dayLengthMultiplier);
        if (cycleFactor > 0) {
            return cycleFactor * config.dayNightTemperatureVariation * Math.min(dayLengthMultiplier, 1.0F);
        }

        return cycleFactor * config.dayNightTemperatureVariation;
    }

    private float calculateAltitudeModifier(double entityY) {
        float optimalY = config.optimalAltitude;
        float spread = config.altitudeSpread;
        float maxDrop = config.altitudeMaxDrop;
        double gaussian = Math.exp(-Math.pow(entityY - optimalY, 2) / (2 * Math.pow(spread, 2)));

        return (float) ((gaussian - 1.0) * maxDrop);
    }

    private float calculateWeatherModifier(String weatherId) {
        if(weatherId == null) return 0.0F;

        if(weatherId.contains("Rain")) {
            return -3.0F;
        }
        else if(weatherId.contains("Storm")) {
            return -5.0F;
        }
        else if(weatherId.contains("Snow")) {
            return -4.0F;
        }

        return 0.0F;
    }

    private float interpolateTemperature(
            float current,
            float target,
            float deltaTime,
            ItemStatSnapshot equipmentStats
    ) {
        float diff = target - current;
        boolean isHeatingUp = diff > 0;
        boolean isCoolingDown = diff < 0;

        float itemOffset = equipmentStats.thermalOffset;
        boolean itemWantsToCool = itemOffset < -1.0f;
        boolean itemWantsToHeat = itemOffset > 1.0f;

        boolean activeMode = false;
        if (itemWantsToCool && isCoolingDown) activeMode = true;
        else if (itemWantsToHeat && isHeatingUp) activeMode = true;

        if (activeMode) {
            float maxChange = config.activeItemResponseSpeed * deltaTime;
            if (isHeatingUp) return Math.min(current + maxChange, target);
            else return Math.max(current - maxChange, target);
        }

        float base = config.defaultBaseTemperature;

        float safeThreshold = config.comfortZoneThreshold;
        boolean targetIsSafe = (target > base - safeThreshold) && (target < base + safeThreshold);

        boolean directionTowardsBase = (isHeatingUp && current < base) || (isCoolingDown && current > base);
        boolean fightingAgainstItem = (itemWantsToCool && isHeatingUp) || (itemWantsToHeat && isCoolingDown);
        boolean isRecovering = directionTowardsBase && targetIsSafe && !fightingAgainstItem;

        float selectedSpeed = isRecovering ? config.fastResponseSpeed : config.slowResponseSpeed;

        if (isRecovering) {
            float currentDist = Math.abs(current - base);
            float distanceRatio = currentDist / config.inertiaReferenceDistance;

            float inertiaMultiplier = Math.max(
                    config.minInertiaMultiplier,
                    Math.min(config.maxInertiaMultiplier, distanceRatio)
            );
            selectedSpeed *= inertiaMultiplier;

        } else {
            float appliedInsulation = isCoolingDown ? equipmentStats.insulationCooling : equipmentStats.insulationHeating;

            appliedInsulation = Math.min(appliedInsulation, 0.95f);

            selectedSpeed *= (1.0f - appliedInsulation);
        }

        float step = Math.min(deltaTime * selectedSpeed, 1.0F);
        return current + (diff * step);
    }

    private float getSeasonStretch(Store<EntityStore> store) {
        SeasonsResource seasonData = store.getResource(SeasonsResource.TYPE);
        if (seasonData != null) {
            return seasonData.getCurrentSeason().getDayLengthMultiplier();
        }
        return 1.0F;
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

    private int getTemperatureStatIndex() {
        if (temperatureStatIndex == -1) {
            temperatureStatIndex = EntityStatType.getAssetMap().getIndex("Temperature");
        }
        return temperatureStatIndex;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), TemperatureComponent.getComponentType());
    }
}
