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
        ComfortComponent comfort = archetypeChunk.getComponent(index, ComfortComponent.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        EnvironmentComponent env = archetypeChunk.getComponent(index, EnvironmentComponent.getComponentType());

        if (player == null || comfort == null || transform == null) return;

        updateWeatherStatus(player, comfort, transform, store);

        processComfortEvolution(player, comfort, env, archetypeChunk, index, deltaTime);
    }

    private void updateWeatherStatus(
            Player player,
            ComfortComponent comfort,
            TransformComponent transform,
            Store<EntityStore> store
    ) {
        if (player.getWorld() == null || player.getWorld().getTick() % 10 != 0) return;

        WeatherResource weatherRes = store.getResource(WeatherResource.getResourceType());
        String weatherId = WeatherHelper.getWeatherId(player, weatherRes, transform);
        comfort.setLastWeatherMalus(calculateWeatherMalus(weatherId));
    }

    private void processComfortEvolution(
            Player player,
            ComfortComponent comfort,
            EnvironmentComponent env,
            ArchetypeChunk<EntityStore> chunk,
            int index,
            float deltaTime
    ) {
        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        EntityStatMap statMap = ref.getStore().getComponent(ref, EntityStatMap.getComponentType());
        EntityStatValue stat = statMap.get(getComfortStatIndex());

        float pending = applyComfortAnimation(comfort, stat.get(), deltaTime);

        if (player.getGameMode() != GameMode.Creative) {
            UsageBufferComponent buffer = chunk.getComponent(index, UsageBufferComponent.getComponentType());
            float equipmentBonus = (buffer != null) ? buffer.getLastSnapshot().comfortModifier : 0;

            pending += calculateComfortDelta(comfort, env, equipmentBonus) * deltaTime;
        } else {
            pending += (config.creativeRegenSpeed > 0 ? config.creativeRegenSpeed : 50.0F) * deltaTime;
        }

        float finalValue = StatHelper.clamp(stat, pending);
        updateStatIfChanged(statMap, stat.get(), finalValue);

        float ratio = finalValue / stat.getMax();
        updateComfortHud(player, ratio);
        handleMaxStaminaBonus(statMap, ratio, comfort);
    }

    private float calculateComfortDelta(ComfortComponent comfort, EnvironmentComponent env, float equipmentBonus) {
        boolean isExposed = env == null || env.lastResult == null || !env.lastResult.isUnderRoof();

        float loss = (config.comfortLossSpeed / config.comfortLossInterval);
        if (isExposed && comfort.getLastWeatherMalus() > 0) {
            loss += comfort.getLastWeatherMalus();
        }

        float gain = comfort.getAuraGain() + (comfort.getEnvironmentalGain() * config.environmentGlobalGainMultiplier);
        return gain - loss + equipmentBonus;
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
