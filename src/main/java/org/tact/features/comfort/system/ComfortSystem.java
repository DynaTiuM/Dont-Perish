package org.tact.features.comfort.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
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
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        float newComfort = comfortComponent.getCurrentComfort() - (config.comfortLossSpeed * deltaTime);
        newComfort += comfortComponent.getEnvironmentalGain() * deltaTime;
        newComfort = Math.max(0, Math.min(newComfort, config.maxComfort));
        comfortComponent.setCurrentComfort(newComfort);

        float diff = comfortComponent.getCurrentComfort() - comfortComponent.getLerpedComfort();
        float lerp = comfortComponent.getLerpedComfort() + diff * Math.min(deltaTime * config.lerpSpeed, 1.0F);

        comfortComponent.setLerpedComfort(lerp);

        float comfortRatio = comfortComponent.getLerpedComfort() / config.maxComfort;

        EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
        this.handleStaminaRegen(statMap, comfortRatio, deltaTime);
        HudManager.updateChild(player, "comfort", ComfortHud.class, (hud, uiBuilder) -> {
            hud.render(uiBuilder, comfortRatio);
        });
    }

    private void handleStaminaRegen(EntityStatMap statMap, float comfortRatio, float deltaTime) {
        int staminaIdx = DefaultEntityStatTypes.getStamina();
        EntityStatValue stamina = statMap.get(staminaIdx);

        if (stamina != null && stamina.get() < stamina.getMax()) {
            float bonusPerSecond = (float) Math.log1p(1.7F * comfortRatio);

            float amountToAdd = bonusPerSecond * deltaTime;

            statMap.addStatValue(staminaIdx, amountToAdd);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), ComfortComponent.getComponentType());
    }
}
