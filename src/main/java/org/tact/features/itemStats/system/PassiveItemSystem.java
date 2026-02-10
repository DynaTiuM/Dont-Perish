package org.tact.features.itemStats.system;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.itemStats.component.UsageBufferComponent;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.model.ItemStatSnapshot;
import org.tact.features.itemStats.util.ItemStatCalculator;

public class PassiveItemSystem extends EntityTickingSystem<EntityStore> {
    private final ItemStatsConfig config;

    public PassiveItemSystem(ItemStatsConfig config) {
        this.config = config;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            ArchetypeChunk<EntityStore> archetypeChunk,
            Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> bufferCommand
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        ComponentType<EntityStore, InteractionManager> managerType = InteractionModule.get().getInteractionManagerComponent();
        InteractionManager interactionManager = store.getComponent(playerRef, managerType);
        UsageBufferComponent buffer = archetypeChunk.getComponent(index, UsageBufferComponent.getComponentType());

        ItemStatSnapshot totals = ItemStatCalculator.calculate(player, interactionManager, config, deltaTime, buffer, true);

        applySpeed(store, playerRef, totals.speedModifier);

        if (totals.healthChange != 0) {
            applyHealth(player, playerRef, bufferCommand, totals.healthChange, deltaTime);
        }

        if (totals.staminaDrain != 0) {
            applyStamina(store, playerRef, totals.staminaDrain, deltaTime);
        }
    }


    private void applySpeed(Store<EntityStore> store, Ref<EntityStore> ref, float mod) {
        // TODO
    }

    private void applyHealth(Player p, Ref<EntityStore> ref, CommandBuffer<EntityStore> buf, float amountPerSec, float dt) {
        // TODO
    }

    private void applyStamina(Store<EntityStore> store, Ref<EntityStore> ref, float drainPerSec, float dt) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());

        stats.subtractStatValue(DefaultEntityStatTypes.getStamina(), drainPerSec * dt);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}