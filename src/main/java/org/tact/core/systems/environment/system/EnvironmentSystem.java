package org.tact.core.systems.environment.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.core.systems.environment.EnvironmentHandler;
import org.tact.core.systems.environment.EnvironmentRegistry;
import org.tact.core.systems.environment.EnvironmentResult;
import org.tact.core.systems.environment.component.EnvironmentComponent;

public class EnvironmentSystem extends EntityTickingSystem<EntityStore> {

    private final int radius;
    private final int ticksInterval;
    private final EnvironmentRegistry registry;

    public EnvironmentSystem(int radius, int ticksInterval, EnvironmentRegistry registry) {
        this.radius = radius;
        this.ticksInterval = ticksInterval;
        this.registry = registry;
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
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        EnvironmentComponent envState = archetypeChunk.getComponent(index, EnvironmentComponent.getComponentType());

        if (player == null || envState == null) return;

        long worldTick = player.getWorld().getTick();

        if ((worldTick + index) % this.ticksInterval == 0) {
            EnvironmentResult newResult = scanBlocks(player, playerRef);
            envState.lastResult = newResult;
        }

        if (envState.lastResult != null) {
            for (EnvironmentHandler handler : registry.getAllHandlers().values()) {

                handler.onEnvironmentScanned(player, playerRef, store, envState.lastResult, deltaTime);
            }
        }

    }

    private EnvironmentResult scanBlocks(Player player, Ref<EntityStore> playerRef) {
        EnvironmentResult result = new EnvironmentResult(radius);
        World world = player.getWorld();

        Store<EntityStore> store = playerRef.getStore();
        TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (transformComponent == null) return result;
        if (world == null) return result;

        double rawX = transformComponent.getPosition().x;
        double rawY = transformComponent.getPosition().y;
        double rawZ = transformComponent.getPosition().z;

        int playerX = (int) Math.floor(rawX);
        int playerY = (int) Math.floor(rawY);
        int playerZ = (int) Math.floor(rawZ);

        // Floor check
        int floorY = (int) Math.floor(rawY - 0.1);
        String fluidId = getFluidIdAsString(world, playerX, floorY + 1, playerZ);
        if (fluidId != null) {
            result.setBlockUnderFeet(fluidId);
        }
        else {
            BlockType floorBlockType = world.getBlockType(playerX, floorY, playerZ);
            if (floorBlockType != null && !floorBlockType.getId().equals("Empty")) {
                result.setBlockUnderFeet(floorBlockType.getId());
            }
        }

        // Roof check
        int maxRoofScan = 30;
        for (int height = 2; height <= maxRoofScan; height++) {
            BlockType skyBlockType = world.getBlockType(
                    playerX,
                    playerY + height,
                    playerZ
            );

            if (skyBlockType != null && !skyBlockType.getId().equals("Empty")) {
                result.setRoof(true, height);
                break;
            }
        }

        // Environment Check (comfort & temperature)
        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    int blockX = playerX + x;
                    int blockY = playerY + y;
                    int blockZ = playerZ + z;
                    BlockType blockType = world.getBlockType(
                            blockX,
                            blockY,
                            blockZ
                    );

                    if(isValidBlock(blockType)) {
                        String blockId = blockType.getId();
                        result.addBlock(blockId);
                    }
                    else {
                        fluidId = getFluidIdAsString(world, blockX, blockY, blockZ);

                        if (fluidId != null) {
                            result.addBlock(fluidId);
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean isValidBlock(BlockType block) {
        return block != null && block.getId() != null && !block.getId().equals("Empty");
    }

    private String getFluidIdAsString(World world, int x, int y, int z) {
        int fluidId = world.getFluidId(x, y, z);

        if (fluidId == 0) return null;
        if (fluidId == 7 || fluidId == 8) return "Fluid_Water";
        if (fluidId == 6 || fluidId == 11) return "Fluid_Lava";

        return null;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
