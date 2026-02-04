package org.tact.features.comfort.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.common.utils.StatHelper;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.comfort.config.ComfortConfig;
import org.tact.features.comfort.ui.ComfortHud;

public class ComfortSystem extends EntityTickingSystem<EntityStore> {
    private final ComfortConfig config;

    public ComfortSystem(ComfortConfig config) {
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
        ComfortComponent comfortComponent = archetypeChunk.getComponent(index, ComfortComponent.getComponentType());
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());

        float currentComfort = comfortStat.get();
        float pendingComfort = currentComfort;

        pendingComfort = applyComfortAnimation(comfortComponent, pendingComfort, deltaTime);
        pendingComfort = applyComfortLogic(player, comfortComponent, comfortStat, pendingComfort, deltaTime);

        float finalComfort = StatHelper.clamp(comfortStat, pendingComfort);
        updateStatIfChanged(statMap, currentComfort, finalComfort);

        float discomfortThreshold = comfortStat.getMax() * config.threshold;

        boolean isUncomfortable = finalComfort < discomfortThreshold;
        comfortComponent.setUncomfortable(isUncomfortable);

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

    private float applyComfortLogic(Player player, ComfortComponent comp, EntityStatValue stat, float pendingComfort, float deltaTime) {
        if (player.getGameMode() == GameMode.Creative) {
            if (pendingComfort < stat.getMax()) {
                float regenSpeed = config.creativeRegenSpeed > 0 ? config.creativeRegenSpeed : 50.0F;
                return pendingComfort + (regenSpeed * deltaTime);
            }
        } else {
            float loss = (config.comfortLossSpeed / config.comfortLossInterval) * deltaTime;
            float gain = comp.getEnvironmentalGain() * config.globalGainMultiplier * deltaTime;
            return pendingComfort - loss + gain;
        }
        return pendingComfort;
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

    private void handleMaxStaminaBonus(EntityStatMap statMap, float comfortRatio, ComfortComponent comfortComponent) {
        int staminaIdx = DefaultEntityStatTypes.getStamina();

        float finalBonus;

        float threshold =  config.threshold;
        if (comfortRatio < threshold) {
            float factor = (threshold - comfortRatio) / threshold;
            finalBonus = -config.maxStaminaPenaltyPercent * factor;
        }
        else {
            float range = 1.0f - threshold;
            float factor = (comfortRatio - threshold) / range;
            finalBonus = config.maxStaminaBonusPercent * factor;
        }

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
