package org.tact.features.itemStats.system;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
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
            CommandBuffer<EntityStore> buffer
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

        ItemStatSnapshot totals = ItemStatCalculator.calculate(player, config);

        applySpeed(store, entityRef, totals.speedModifier);

        if (totals.healthChange != 0) {
            applyHealth(player, entityRef, buffer, totals.healthChange, deltaTime);
        }

        if (totals.staminaDrain != 0) {
            applyStamina(store, entityRef, totals.staminaDrain, deltaTime);
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