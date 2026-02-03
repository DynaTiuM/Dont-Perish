package org.tact.features.comfort.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
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
        ComfortComponent comfort = archetypeChunk.getComponent(index, ComfortComponent.getComponentType());
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());

        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());
        float currentComfort = comfortStat.get();

        float newComfort = currentComfort - (config.comfortLossSpeed * deltaTime);
        newComfort += comfort.getEnvironmentalGain() * deltaTime;

        newComfort = StatHelper.clamp(comfortStat, newComfort);

        if (newComfort != currentComfort) {
            statMap.setStatValue(getComfortStatIndex(), newComfort);
        }

        float diff = newComfort - comfort.getLerpedComfort();
        float lerp = comfort.getLerpedComfort() + diff * Math.min(deltaTime * config.lerpSpeed, 1.0f);
        comfort.setLerpedComfort(lerp);


        float comfortRatio = comfort.getLerpedComfort() / comfortStat.getMax();
        HudManager.updateChild(player, "comfort", ComfortHud.class, (hud, builder) -> {
            hud.render(builder, comfortRatio);
        });

        handleMaxStaminaBonus(statMap, comfortRatio, comfort);
    }

    private void handleMaxStaminaBonus(EntityStatMap statMap, float comfortRatio, ComfortComponent comfort) {
        int staminaIdx = DefaultEntityStatTypes.getStamina();

        float effectiveRatio = (float) Math.log1p(comfortRatio);
        float maxBonusPercent = 0.8F;
        float finalBonus = effectiveRatio * maxBonusPercent;

        if (Math.abs(finalBonus - comfort.getLastAppliedBonus()) < 0.005F) {
            return;
        }
        comfort.setLastAppliedBonus(finalBonus);

        if (finalBonus < 0.01F) {
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
