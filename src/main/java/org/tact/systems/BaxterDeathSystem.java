package org.tact.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.DontPerish;

import javax.annotation.Nonnull;

public class BaxterDeathSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> chunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> cmd
    ) {

        Ref<EntityStore> baxterRef = chunk.getReferenceTo(index);
        EntityStatMap stats = store.getComponent(baxterRef, EntityStatMap.getComponentType());

        if (stats != null) {
            int healthIndex = DefaultEntityStatTypes.getHealth();
            float currentHealth = stats.get(healthIndex).get();

            if (currentHealth <= 0) {
                cmd.removeEntity(baxterRef, RemoveReason.REMOVE);
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return DontPerish.baxterComponent;
    }
}