package org.tact.common.environment;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentScannerSystem extends EntityTickingSystem<EntityStore> {

    private final int radius;
    private final float scanInterval;
    private final EnvironmentRegistry registry;

    private final Map<Ref<EntityStore>, CachedScan> scanCache = new ConcurrentHashMap<>();

    public EnvironmentScannerSystem(int radius, float scanInterval, EnvironmentRegistry registry) {
        this.radius = radius;
        this.scanInterval = scanInterval;
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

        scanCache.keySet().removeIf(entityRef -> !entityRef.isValid());

        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        if (player == null) return;

        CachedScan cached = scanCache.get(playerRef);

        if (cached == null) {
            cached = new CachedScan();
            scanCache.put(playerRef, cached);
        }

        cached.timeSinceLastScan += deltaTime;

        if (cached.timeSinceLastScan >= scanInterval) {
            cached.timeSinceLastScan = 0.0f;
            cached.lastResult = scanBlocks(player, playerRef);

            for (EnvironmentHandler handler : registry.getAllHandlers().values()) {
                handler.onEnvironmentScanned(player, playerRef, store, cached.lastResult, deltaTime);
            }
        }

    }

    private EnvironmentResult scanBlocks(Player player, Ref<EntityStore> playerRef) {
        EnvironmentResult result = new EnvironmentResult(radius);
        World world = player.getWorld();

        Store<EntityStore> store = playerRef.getStore();

        TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());

        if (transformComponent == null) {
            return result;
        }

        int playerX = (int) transformComponent.getPosition().x;
        int playerY = (int) transformComponent.getPosition().y;
        int playerZ = (int) transformComponent.getPosition().z;

        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    BlockType blockType = world.getBlockType(
                            playerX + x,
                            playerY + y,
                            playerZ + z
                    );
                    String blockId = blockType.getId();

                    if(blockId != null && !blockId.equals("air")) {
                        result.addBlock(blockId);
                    }
                }
            }
        }

        return result;
    }

    public void removePlayer(Ref<EntityStore> entityRef) {
        scanCache.remove(entityRef);
    }

    private static class CachedScan {
        EnvironmentResult lastResult = null;
        float timeSinceLastScan = 0.0f;
    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
