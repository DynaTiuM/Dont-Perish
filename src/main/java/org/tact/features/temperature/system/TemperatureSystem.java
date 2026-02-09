package org.tact.features.temperature.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
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
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;
import org.tact.features.itemStats.util.ItemStatCalculator;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.temperature.config.TemperatureConfig;
import org.tact.features.temperature.ui.TemperatureHud;

public class TemperatureSystem extends EntityTickingSystem<EntityStore> {
    private final TemperatureConfig config;
    private final ItemStatsConfig itemConfig;

    private int temperatureStatIndex = -1;

    private DamageCause heatDamageCause;
    private DamageCause coldDamageCause;

    public TemperatureSystem(
            TemperatureConfig config,
            ItemStatsConfig itemConfig
    ) {
        this.config = config;
        this.itemConfig = itemConfig;
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
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        TemperatureComponent temperatureComponent = archetypeChunk.getComponent(index, TemperatureComponent.getComponentType());
        if(temperatureComponent == null) return;

        EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
        if (statMap == null) return;

        EntityStatValue temperatureStat = statMap.get(getTemperatureStatIndex());
        if (temperatureStat == null) return;

        TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

        double playerY = config.optimalAltitude;
        if(transformComponent != null) {
            playerY = transformComponent.getPosition().getY();
        }

        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        float seasonStretch = getSeasonStretch(store);

        ComponentType<EntityStore, InteractionManager> managerType = InteractionModule.get().getInteractionManagerComponent();
        InteractionManager interactionManager = store.getComponent(playerRef, managerType);

        ItemStatSnapshot equipmentStats = ItemStatCalculator.calculate(player, interactionManager, itemConfig);

        // Temperature of the player
        float targetTemperature = calculateTargetTemperature(
                temperatureComponent,
                timeResource,
                seasonStretch,
                playerY,
                equipmentStats.thermalOffset
        );

        temperatureComponent.setTargetTemperature(targetTemperature);

        float currentTemperature = temperatureStat.get();
        float nextTemperature = interpolateTemperature(
                currentTemperature,
                targetTemperature,
                deltaTime,
                equipmentStats
        );
        player.sendMessage(Message.raw("[Temperature]" + nextTemperature));
        if (nextTemperature != currentTemperature) {
            statMap.setStatValue(getTemperatureStatIndex(), nextTemperature);
            temperatureComponent.setLerpedTemperature(nextTemperature);
        }

        boolean shouldApplyDamage = updateDamageTimer(temperatureComponent, nextTemperature, deltaTime);
        if (shouldApplyDamage) {
            applyTemperatureDamage(playerRef, commandBuffer, nextTemperature);
        }

        updateHud(player, temperatureComponent);
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

        return baseTemperature + environment + seasonal + timeModifier + altitude + equipmentOffset;
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
            float degreesPerSecond = config.activeItemResponseSpeed;
            float maxChange = degreesPerSecond * deltaTime;

            if (isHeatingUp) return Math.min(current + maxChange, target);
            else return Math.max(current - maxChange, target);
        }

        boolean isRecovering = false;
        float base = config.defaultBaseTemperature;
        float thresh = config.comfortZoneThreshold;

        if (isHeatingUp && current < (base - thresh)) isRecovering = true;
        else if (isCoolingDown && current > (base + thresh)) isRecovering = true;

        float selectedSpeed = isRecovering ? config.fastResponseSpeed : config.slowResponseSpeed;

        if (!isRecovering) {
            float appliedInsulation = isCoolingDown ? equipmentStats.insulationCooling : equipmentStats.insulationHeating;
            selectedSpeed = selectedSpeed * (1.0f - appliedInsulation);
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
