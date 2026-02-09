package org.tact.features.hunger.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.common.util.StatHelper;
import org.tact.features.hunger.component.HungerComponent;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.hunger.ui.HungerHud;

import javax.annotation.Nonnull;

public class HungerSystem extends EntityTickingSystem<EntityStore> {
    private final HungerConfig config;

    private DamageCause starvationDamageCause;
    private int hungerStatIndex = -1;

    public HungerSystem(
            HungerConfig config
    ) {
        this.config = config;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> chunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        HungerComponent hungerComponent = chunk.getComponent(index, HungerComponent.getComponentType());
        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);

        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
        EntityStatValue hungerStat = statMap.get(getHungerStatIndex());

        float currentHunger = hungerStat.get();

        float pendingHunger = currentHunger;
        pendingHunger = applyDigestion(hungerComponent, pendingHunger, deltaTime);
        pendingHunger = applyGamemodeLogic(player, hungerStat, pendingHunger, deltaTime);

        float clampedHunger = StatHelper.clamp(hungerStat, pendingHunger);
        updateStatIfChanged(statMap, currentHunger, clampedHunger);

        handleStarvationLogic(hungerComponent, clampedHunger, hungerStat.getMin(), entityRef, deltaTime, commandBuffer);
        handleVisualUpdates(player, hungerComponent, hungerStat, currentHunger, clampedHunger);
    }

    private float applyDigestion(HungerComponent hunger, float pendingHunger, float deltaTime) {
        if (hunger.getDigestionBuffer() <= 0) return pendingHunger;

        float percentageToTransfer = Math.min(1.0F, 5.0F * deltaTime);
        float amountToTransfer = hunger.getDigestionBuffer() * percentageToTransfer;

        if (hunger.getDigestionBuffer() < 0.05F) {
            amountToTransfer = hunger.getDigestionBuffer();
        }

        hunger.reduceDigestionBuffer(amountToTransfer);
        return pendingHunger + amountToTransfer;
    }

    private float applyGamemodeLogic(Player player, EntityStatValue stat, float pendingHunger, float deltaTime) {
        if (player.getGameMode() == GameMode.Creative) {
            if (pendingHunger < stat.getMax()) {
                float regenSpeed = config.creativeRegenSpeed > 0 ? config.creativeRegenSpeed : 50.0F;
                return pendingHunger + (regenSpeed * deltaTime);
            }
        } else {
            float lossPerSecond = config.saturationLossSpeed / config.saturationLossInterval;
            return pendingHunger - (lossPerSecond * deltaTime);
        }
        return pendingHunger;
    }

    private void updateStatIfChanged(EntityStatMap statMap, float oldVal, float newVal) {
        if (Math.abs(newVal - oldVal) > 1e-5f) {
            statMap.setStatValue(getHungerStatIndex(), newVal);
        }
    }

    private void handleStarvationLogic(HungerComponent hunger, float currentHunger, float minHunger, Ref<EntityStore> ref, float dt, CommandBuffer<EntityStore> cb) {
        if (currentHunger <= minHunger) {
            processStarvation(hunger, dt, ref, cb);
        } else {
            hunger.resetStarvingElapsedTime();
        }
    }

    private void handleVisualUpdates(Player player, HungerComponent hunger, EntityStatValue stat, float oldVal, float newVal) {
        if (Math.abs(newVal - oldVal) > 1e-5f || hunger.getDigestionBuffer() > 0) {
            updateHud(player, stat);
        }
    }

    private void processStarvation(
            HungerComponent hunger,
            float deltaTime,
            Ref<EntityStore> entityRef,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        hunger.addStarvingElapsedTime(deltaTime);

        if (hunger.getStarvingElapsedTime() >= config.starvingDamageInterval) {
            hunger.resetStarvingElapsedTime();

            Damage damage = new Damage(
                    Damage.NULL_SOURCE,
                    getStarvationDamageCause(),
                    config.starvingDamage
            );
            DamageSystems.executeDamage(entityRef, commandBuffer, damage);
        }
    }

    private void updateHud(Player player, EntityStatValue hungerStat) {
        float percentage = hungerStat.get() / hungerStat.getMax();
        HudManager.updateChild(player, "hunger", HungerHud.class, (hud, builder) -> {
            hud.render(builder, percentage);
        });
    }

    private DamageCause getStarvationDamageCause() {
        if (starvationDamageCause == null) {
            starvationDamageCause = DamageCause.getAssetMap().getAsset("Starvation");
        }
        return starvationDamageCause;
    }

    private int getHungerStatIndex() {
        if (hungerStatIndex == -1) {
            hungerStatIndex = EntityStatType.getAssetMap().getIndex("Hunger");
        }
        return hungerStatIndex;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), HungerComponent.getComponentType());
    }
}