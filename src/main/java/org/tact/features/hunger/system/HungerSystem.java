package org.tact.features.hunger.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
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
import org.tact.common.utils.StatHelper;
import org.tact.features.hunger.component.HungerComponent;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.hunger.ui.HungerHud;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;

public class HungerSystem extends EntityTickingSystem<EntityStore> {
    private static final float LERP_SPEED = 5.0F;
    private static final float LERP_THRESHOLD = 0.1F;

    private final HungerConfig config;

    private DamageCause starvationDamageCause;
    private int hungerStatIndex = -1;

    public HungerSystem(
            HungerConfig config
    ) {
        this.config = config;

        registerFoodConsumptionCallback();
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
        HungerComponent hunger = chunk.getComponent(index, HungerComponent.getComponentType());
        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);

        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
        EntityStatValue hungerStat = statMap.get(getHungerStatIndex());

        float currentHunger = hungerStat.get();
        float updatedHunger;

        if (player.getGameMode() == GameMode.Adventure) {
            updatedHunger = processAdventureMode(
                    hunger,
                    hungerStat,
                    currentHunger,
                    deltaTime,
                    entityRef,
                    commandBuffer
            );
        } else {
            updatedHunger = processCreativeMode(hungerStat, currentHunger, deltaTime);
        }

        if (updatedHunger != currentHunger) {
            statMap.setStatValue(getHungerStatIndex(), updatedHunger);
        }

        updateHungerDisplay(hunger, updatedHunger, hungerStat.getMax(), deltaTime);

        updateHud(player, hunger, hungerStat);
    }

    private float processAdventureMode(
            HungerComponent hunger,
            EntityStatValue hungerStat,
            float currentHunger,
            float deltaTime,
            Ref<EntityStore> entityRef,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        float updatedHunger = currentHunger;

        hunger.addElapsedTime(deltaTime);
        if (hunger.getElapsedTime() >= config.saturationLossInterval) {
            hunger.resetElapsedTime();
            updatedHunger = StatHelper.clamp(
                    hungerStat,
                    currentHunger - config.saturationLossSpeed
            );
        }

        if (updatedHunger <= hungerStat.getMin()) {
            processStarvation(
                    hunger,
                    deltaTime,
                    entityRef,
                    commandBuffer
            );
        } else {
            hunger.resetStarvingElapsedTime();
        }

        return updatedHunger;
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

    private float processCreativeMode(
            EntityStatValue hungerStat,
            float currentHunger,
            float deltaTime
    ) {
        if (currentHunger < hungerStat.getMax()) {
            return StatHelper.clamp(
                    hungerStat,
                    currentHunger + config.creativeRegenSpeed * deltaTime
            );
        }
        return currentHunger;
    }

    private void updateHungerDisplay(
            HungerComponent hunger,
            float targetHunger,
            float maxHunger,
            float deltaTime
    ) {
        float diff = targetHunger - hunger.getLerpedHunger();

        if (Math.abs(diff) < LERP_THRESHOLD) {
            hunger.setLerpedHunger(targetHunger);
        } else {
            float lerpedValue = hunger.getLerpedHunger() +
                    diff * Math.min(deltaTime * LERP_SPEED, 1.0F);
            hunger.setLerpedHunger(lerpedValue);
        }
    }

    private void updateHud(Player player, HungerComponent hunger, EntityStatValue hungerStat) {
        float percentage = hunger.getLerpedHunger() / hungerStat.getMax();
        HudManager.updateChild(player, "hunger", HungerHud.class, (hud, builder) -> {
            hud.render(builder, percentage);

        });
    }

    public void onFoodConsumed(Ref<EntityStore> entityRef, Item item) {
        float saturationValue = config.foodValues.getOrDefault(
                item.getId(),
                config.defaultSaturation
        );

        Store<EntityStore> store = entityRef.getStore();
        Player player = store.getComponent(entityRef, Player.getComponentType());

        if (player != null) {
            EntityStatMap statMap = store.getComponent(
                    player.getReference(),
                    EntityStatMap.getComponentType()
            );
            EntityStatValue hungerStat = statMap.get(getHungerStatIndex());

            float newValue = StatHelper.clamp(
                    hungerStat,
                    hungerStat.get() + saturationValue
            );
            statMap.setStatValue(getHungerStatIndex(), newValue);
        }
    }

    private void registerFoodConsumptionCallback() {
        Object bridgeObj = System.getProperties().get("hunger.bridge");

        if (bridgeObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> bridge = (Map<String, Object>) bridgeObj;

            BiConsumer<Object, Object> callback = (entity, item) -> {
                try {
                    onFoodConsumed((Ref<EntityStore>) entity, (Item) item);
                } catch (ClassCastException e) {
                    e.printStackTrace(System.err);
                }
            };

            bridge.put("callback", callback);
        }
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