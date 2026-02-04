package org.tact.features.comfort.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.comfort.config.ComfortConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComfortDamageSystem extends DamageEventSystem {

    private final ComfortConfig config;
    private int comfortStatIndex = -1;

    public ComfortDamageSystem(ComfortConfig config) {
        this.config = config;
    }

    @Nullable
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public void handle(
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer,
            @Nonnull Damage damage
    ) {
        if (!(damage.getSource() instanceof Damage.EntitySource)) return;

        Ref<EntityStore> attackerRef = ((Damage.EntitySource) damage.getSource()).getRef();
        if (!attackerRef.isValid()) return;

        Player attacker = store.getComponent(attackerRef, Player.getComponentType());
        if (attacker == null) return;

        EntityStatMap statMap = store.getComponent(attackerRef, EntityStatMap.getComponentType());
        if (statMap == null) return;

        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());
        if (comfortStat == null) return;

        float comfortRatio = comfortStat.get() / comfortStat.getMax();

        float newDamage = getNewDamage(damage, comfortRatio);

        damage.setAmount(newDamage);
    }

    private float getNewDamage(@NonNullDecl Damage damage, float comfortRatio) {
        float finalBonus;

        float threshold =  config.threshold;
        if (comfortRatio < threshold) {
            float factor = (threshold - comfortRatio) / threshold;
            finalBonus = -config.maxDamagePenaltyPercent * factor;
        }
        else {
            float range = 1.0f - threshold;
            float factor = (comfortRatio - threshold) / range;
            finalBonus = config.maxDamageBonusPercent * factor;
        }

        float newAmount = damage.getAmount() * (1.0F + finalBonus);
        if (newAmount < 0) newAmount = 0;
        return newAmount;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return EntityStatMap.getComponentType();
    }

    private int getComfortStatIndex() {
        if (comfortStatIndex == -1) {
            comfortStatIndex = EntityStatType.getAssetMap().getIndex("Comfort");
        }
        return comfortStatIndex;
    }
}