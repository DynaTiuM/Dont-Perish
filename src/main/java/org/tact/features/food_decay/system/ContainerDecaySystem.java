package org.tact.features.food_decay.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.features.food_decay.manager.FoodDecayManager;


public class ContainerDecaySystem extends EntityTickingSystem<EntityStore> {
    private final FoodDecayManager decayManager;
    private final DecayTickControlSystem controller;

    public ContainerDecaySystem(FoodDecayManager decayManager, DecayTickControlSystem controller) {
        this.decayManager = decayManager;
        this.controller = controller;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        if (index != 0 || !controller.shouldProcess()) return;

        Player player = archetypeChunk.getComponent(index, Player.getComponentType());

        processAllWorldChests(player.getWorld(), controller.getTimeToProcess());
    }

    private void processAllWorldChests(World world, float deltaTime) {
        var containerType = getContainerType(world);
        if (containerType == null) return;

        world.getChunkStore().getStore().forEachChunk(Query.and(containerType), (archetypeChunk, buffer) -> {
            for (int i = 0; i < archetypeChunk.size(); i++) {
                ItemContainerState state = archetypeChunk.getComponent(i, containerType);
                if (state != null && state.getItemContainer() != null) {
                    double multiplier = 1.0;
                    decayManager.processContainer(state.getItemContainer(), deltaTime, multiplier);
                }
            }
        });
    }


    @SuppressWarnings("unchecked")
    private ComponentType<ChunkStore, ItemContainerState> getContainerType(World world) {
        try {
            Object registry = world.getChunkStore().getStore().getRegistry();

            java.lang.reflect.Field field = registry.getClass().getDeclaredField("componentTypes");
            field.setAccessible(true);

            Object[] types = (Object[]) field.get(registry);

            for (Object rawType : types) {
                if (rawType == null) continue;

                ComponentType<?, ?> type = (ComponentType<?, ?>) rawType;

                if (type.getTypeClass().equals(ItemContainerState.class)) {
                    return (ComponentType<ChunkStore, ItemContainerState>) type;
                }
            }
        } catch (Exception e) {
            System.out.println("Reflection Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
