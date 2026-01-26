package org.tact.features.baxter.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.baxter.component.BaxterComponent;
import org.tact.features.baxter.config.BaxterConfig;

import javax.annotation.Nonnull;

public class BaxterMovementSystem extends EntityTickingSystem<EntityStore> {

    private final BaxterConfig config;

    public BaxterMovementSystem(
            BaxterConfig config
    ) {
        this.config = config;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                BaxterComponent.getComponentType(),
                TransformComponent.getComponentType(),
                Velocity.getComponentType()
        );
    }

    @Override
    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> baxterRef = archetypeChunk.getReferenceTo(index);
        BaxterComponent baxterComp = store.getComponent(baxterRef, BaxterComponent.getComponentType());
        TransformComponent baxterTransform = store.getComponent(baxterRef, TransformComponent.getComponentType());
        Velocity velocityComp = store.getComponent(baxterRef, Velocity.getComponentType());

        World world = commandBuffer.getExternalData().getWorld();
        Vector3d currentPos = baxterTransform.getPosition();

        Ref<EntityStore> ownerRef = world.getEntityStore().getRefFromUUID(baxterComp.getOwnerUUID());
        if (ownerRef == null || !ownerRef.isValid()) return;

        TransformComponent ownerTransform = store.getComponent(ownerRef, TransformComponent.getComponentType());
        if (ownerTransform == null) return;

        Vector3d ownerPos = ownerTransform.getPosition();

        double dx = currentPos.x - ownerPos.x;
        double dz = currentPos.z - ownerPos.z;

        double distH = Math.sqrt(dx*dx + dz*dz);

        double idealY = ownerPos.y + 3;
        double dy = idealY - currentPos.y;

        double dist3D = Math.sqrt(dx*dx + dy*dy + dz*dz);

        if (dist3D > config.teleportThreshold) {
            baxterTransform.setPosition(ownerPos.clone().add(1.0, 1.5, 0));
            velocityComp.set(Vector3d.ZERO);
            return;
        }

        if (distH > 0.1) {
            float yawRad = (float) (Math.atan2(-dx, -dz) + Math.PI);
            baxterTransform.setRotation(new Vector3f(0f, yawRad, 0f));
        }

        double stopDist = Math.max(1.5F, config.minFollowDistance);

        double targetX, targetZ;

        if (distH < 0.1) {
            targetX = ownerPos.x + stopDist;
            targetZ = ownerPos.z;
        } else {
            double dirX = dx / distH;
            double dirZ = dz / distH;

            targetX = ownerPos.x + (dirX * stopDist);
            targetZ = ownerPos.z + (dirZ * stopDist);
        }
        double baseSpeed = config.movementSpeed;
        double distToTargetXZ = Math.sqrt(Math.pow(targetX - currentPos.x, 2) + Math.pow(targetZ - currentPos.z, 2));
        double effectiveSpeed = (distToTargetXZ > 2.0) ? baseSpeed : (baseSpeed * 0.5);

        double alphaXZ = Math.min(1.0, effectiveSpeed * dt);

        double flySpeed = config.flySpeed;
        double alphaY = Math.min(1.0, flySpeed * dt);

        double nextX = currentPos.x + (targetX - currentPos.x) * alphaXZ;
        double nextZ = currentPos.z + (targetZ - currentPos.z) * alphaXZ;

        double nextY = currentPos.y + (idealY - currentPos.y) * alphaY;

        baxterTransform.setPosition(new Vector3d(nextX, nextY, nextZ));

        if (dt > 0) {
            velocityComp.set(new Vector3d(
                    (nextX - currentPos.x) / dt,
                    (nextY - currentPos.y) / dt,
                    (nextZ - currentPos.z) / dt
            ));
        }
    }
}