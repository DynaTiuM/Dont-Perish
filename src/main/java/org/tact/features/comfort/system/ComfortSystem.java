package org.tact.features.comfort.system;

import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentRange;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.worldgen.container.EnvironmentContainer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.common.util.StatHelper;
import org.tact.common.util.WeatherHelper;
import org.tact.core.systems.environment.component.EnvironmentComponent;
import org.tact.core.systems.environment.system.EnvironmentSystem;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.comfort.config.ComfortConfig;
import org.tact.features.comfort.ui.ComfortHud;
import org.tact.features.itemStats.component.UsageBufferComponent;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;
import org.tact.features.itemStats.util.ItemStatCalculator;

import static com.hypixel.hytale.math.util.ChunkUtil.indexChunkFromBlock;

public class ComfortSystem extends EntityTickingSystem<EntityStore> {
    private final ComfortConfig config;

    public ComfortSystem(
            ComfortConfig config
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
        if(player == null) return;
        ComfortComponent comfortComponent = archetypeChunk.getComponent(index, ComfortComponent.getComponentType());
        TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        if(comfortComponent == null || transformComponent == null) return;

        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());

        UsageBufferComponent buffer = archetypeChunk.getComponent(index, UsageBufferComponent.getComponentType());

        ItemStatSnapshot itemStats = (buffer != null) ? buffer.getLastSnapshot() : new ItemStatSnapshot();

        float equipmentBonus = itemStats.comfortModifier;

        float currentComfort = comfortStat.get();
        float pendingComfort = currentComfort;

        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());
        pendingComfort = applyComfortAnimation(comfortComponent, pendingComfort, deltaTime);
        EnvironmentComponent environmentComponent = archetypeChunk.getComponent(index, EnvironmentComponent.getComponentType());

        if(player.getWorld() != null) {
            if (player.getWorld().getTick() % 10 == 0) {
                String weatherId = WeatherHelper.getWeatherId(player, weatherResource, transformComponent);

                float malus = calculateWeatherMalus(weatherId);
                comfortComponent.setLastWeatherMalus(malus);
            }
        }

        pendingComfort = applyComfortLogic(
                player,
                comfortComponent,
                environmentComponent,
                comfortStat,
                pendingComfort,
                deltaTime,
                equipmentBonus
        );

        float finalComfort = StatHelper.clamp(comfortStat, pendingComfort);
        updateStatIfChanged(statMap, currentComfort, finalComfort);

        float comfortRatio = finalComfort / comfortStat.getMax();
        updateComfortHud(player, comfortRatio);
        handleMaxStaminaBonus(statMap, comfortRatio, comfortComponent);
    }

    private float applyComfortAnimation(ComfortComponent comp, float pendingComfort, float deltaTime) {
        if (comp.getComfortBuffer() <= 0) return pendingComfort;

        float percentageToTransfer = Math.min(1.0F, 5.0F * deltaTime);
        float amountToTransfer = comp.getComfortBuffer() * percentageToTransfer;

        if (comp.getComfortBuffer() < 0.05F) {
            amountToTransfer = comp.getComfortBuffer();
        }

        comp.reduceComfortBuffer(amountToTransfer);
        return pendingComfort + amountToTransfer;
    }

    private float applyComfortLogic(
            Player player,
            ComfortComponent comfortComponent,
            EnvironmentComponent environmentComponent,
            EntityStatValue stat,
            float pendingComfort,
            float deltaTime,
            float equipmentBonus
    ) {
        if (player.getGameMode() == GameMode.Creative) {
            if (pendingComfort < stat.getMax()) {
                float regenSpeed = config.creativeRegenSpeed > 0 ? config.creativeRegenSpeed : 50.0F;
                return pendingComfort + (regenSpeed * deltaTime);
            }
        } else {
            float totalChangePerSecond = getTotalChangePerSecond(comfortComponent, environmentComponent, equipmentBonus);

            return pendingComfort + (totalChangePerSecond * deltaTime);
        }
        return pendingComfort;
    }

    private float getTotalChangePerSecond(ComfortComponent comfortComponent, EnvironmentComponent environmentComponent, float equipmentBonus) {
        boolean exposed = true;
        if (environmentComponent != null && environmentComponent.lastResult != null) {
            if (environmentComponent.lastResult.isUnderRoof()) {
                exposed = false;
            }
        }

        float loss = (config.comfortLossSpeed / config.comfortLossInterval);
        if (comfortComponent.getLastWeatherMalus() > 0 && exposed) {
            loss += comfortComponent.getLastWeatherMalus();
        }
        float gain = comfortComponent.getAuraGain() + comfortComponent.getEnvironmentalGain() * config.environmentGlobalGainMultiplier;

        float totalChangePerSecond = gain - loss + equipmentBonus;
        return totalChangePerSecond;
    }

    private float calculateWeatherMalus(String weatherId) {
        if(weatherId == null) {
            return 0.0F;
        }

        if (weatherId.contains("Rain") || weatherId.contains("Storm")) {
            return 1.0F;
        }

        return 0.0F;
    }

    private void updateStatIfChanged(EntityStatMap statMap, float oldVal, float newVal) {
        if (Math.abs(newVal - oldVal) > 1e-5f) {
            statMap.setStatValue(getComfortStatIndex(), newVal);
        }
    }

    private void updateComfortHud(Player player, float ratio) {
        HudManager.updateChild(player, "comfort", ComfortHud.class, (hud, builder) -> {
            hud.render(builder, ratio);
        });
    }

    private void handleMaxStaminaBonus(
            EntityStatMap statMap,
            float comfortRatio,
            ComfortComponent comfortComponent
    ) {
        int staminaIdx = DefaultEntityStatTypes.getStamina();

        float finalBonus = comfortRatio *
                (config.maxStaminaBonusPercent + config.maxStaminaPenaltyPercent) - config.maxStaminaPenaltyPercent;

        if (Math.abs(finalBonus - comfortComponent.getLastAppliedBonus()) < 0.005F) {
            return;
        }

        comfortComponent.setLastAppliedBonus(finalBonus);

        if (Math.abs(finalBonus) < 0.01F) {
            statMap.removeModifier(staminaIdx, "comfort_max_stamina");
            return;
        }

        Modifier comfortModifier = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.MULTIPLICATIVE,
                1.0F + finalBonus
        );

        statMap.putModifier(EntityStatMap.Predictable.SELF, staminaIdx, "comfort_max_stamina", comfortModifier);
    }

    private int comfortStatIndex = -1;
    private int getComfortStatIndex() {
        if (comfortStatIndex == -1) {
            comfortStatIndex = EntityStatType.getAssetMap().getIndex("Comfort");
        }
        return comfortStatIndex;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), ComfortComponent.getComponentType());
    }
}
