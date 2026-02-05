package org.tact.features.food_decay.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.manager.FoodDecayManager;

public class FoodDecaySystem extends EntityTickingSystem<EntityStore> {
    private final FoodDecayConfig config;
    private final FoodDecayManager decayManager;
    private float playerTimer = 0.0f;
    private float chestTimer = 0.0f;

    public FoodDecaySystem(FoodDecayConfig config, FoodDecayManager decayManager) {
        this.config = config;
        this.decayManager = decayManager;
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
        if (player == null) return;

        boolean isLeader = (index == 0);

        if (isLeader) {
            playerTimer += deltaTime;

            if (playerTimer >= config.playerDecayInterval) {
                double multiplier = decayManager.calculateMultiplier(player.getWorld(), archetypeChunk.getReferenceTo(index), null);
                decayManager.processContainer(player.getInventory().getCombinedEverything(), playerTimer, multiplier);

                playerTimer = 0.0f;
            }
        }

        if (isLeader) {
            chestTimer += deltaTime;

            if (chestTimer >= config.chestDecayInterval) {
                processAllWorldChests(player.getWorld(), chestTimer);
                chestTimer = 0.0f;
            }
        }
    }

    private void processAllWorldChests(World world, float deltaTime) {
        var containerType = getContainerType(world);
        if (containerType == null) return;

        Store<ChunkStore> blockStore = world.getChunkStore().getStore();
        Query<ChunkStore> query = Query.and(containerType);

        final int[] chestsFound = {0};

        blockStore.forEachChunk(query, (archetypeChunk, buffer) -> {
            for (int i = 0; i < archetypeChunk.size(); i++) {

                ItemContainerState state = archetypeChunk.getComponent(i, containerType);

                if (state != null && state.getItemContainer() != null) {
                    chestsFound[0]++;

                    double multiplier = 1.0;
                    BlockType blockType = state.getBlockType();
                    if (blockType.getId().contains("Fridge")) multiplier = 0.5;
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
            System.out.println("Error Reflection: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}