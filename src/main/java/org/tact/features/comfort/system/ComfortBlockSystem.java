package org.tact.features.comfort.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.comfort.config.ComfortConfig;

import javax.annotation.Nullable;

public class ComfortBlockSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {

    private final ComfortConfig config;
    private int comfortStatIndex = -1;

    public ComfortBlockSystem(ComfortConfig config) {
        super(DamageBlockEvent.class);
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
        @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
        @NonNullDecl Store<EntityStore> store,
        @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
        @NonNullDecl DamageBlockEvent damageBlockEvent
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        if(player == null) return;

        EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
        if(statMap == null) return;

        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());
        if(comfortStat == null || comfortStat.get() <= 0) return;

        float comfortRatio = comfortStat.get() / comfortStat.getMax();


        float newBlockDamage = getNewBlockDamage(damageBlockEvent, comfortRatio);

        damageBlockEvent.setDamage(newBlockDamage);
    }

    private float getNewBlockDamage(@NonNullDecl DamageBlockEvent damageBlockEvent, float comfortRatio) {
        float finalBonus;

        float threshold =  config.threshold;
        if (comfortRatio < threshold) {
            float factor = (threshold - comfortRatio) / threshold;
            finalBonus = -config.maxBlockPenaltyPercent * factor;
        }
        else {
            float range = 1.0f - threshold;
            float factor = (comfortRatio - threshold) / range;
            finalBonus = config.maxBlockBonusPercent * factor;
        }

        float newBlockDamage = damageBlockEvent.getDamage() * (1.0F + finalBonus);
        if (newBlockDamage < 0) newBlockDamage = 0;
        return newBlockDamage;
    }


    private int getComfortStatIndex() {
        if (comfortStatIndex == -1) {
            comfortStatIndex = EntityStatType.getAssetMap().getIndex("Comfort");
        }
        return comfortStatIndex;
    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                Player.getComponentType(),
                EntityStatMap.getComponentType()
        );
    }
}
