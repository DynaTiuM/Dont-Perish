package org.tact.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.DontPerish;
import org.tact.components.BaxterComponent;

import javax.annotation.Nonnull;

public class BaxterSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> baxterRef = archetypeChunk.getReferenceTo(index);

        BaxterComponent baxterComp =
                store.getComponent(baxterRef, BaxterComponent.getComponentType());
        TransformComponent baxterTransform =
                store.getComponent(baxterRef, TransformComponent.getComponentType());
        Velocity velocityComp =
                store.getComponent(baxterRef, Velocity.getComponentType());

        World world = commandBuffer.getExternalData().getWorld();

        Vector3d currentPos = baxterTransform.getPosition();
        Vector3d currentVel = velocityComp.getVelocity();

        Ref<EntityStore> ownerRef = world.getEntityStore().getRefFromUUID(baxterComp.getOwnerUUID());
        if (ownerRef == null || !ownerRef.isValid()) return;

        TransformComponent ownerTransform = store.getComponent(ownerRef, TransformComponent.getComponentType());
        if (ownerTransform == null) return;

        Vector3d targetPos = ownerTransform.getPosition();

        // Rotation
        double dx = targetPos.x - currentPos.x;
        double dz = targetPos.z - currentPos.z;

        if (dx * dx + dz * dz > 0.0001) {
            float yawRad = (float) Math.atan2(dx, dz) + (float) Math.PI;

            baxterTransform.setRotation(new Vector3f(0f, yawRad, 0f));
        }

        // Movement
        double dy = (targetPos.y + 1.5) - currentPos.y;
        double distance = Math.sqrt(dx * dx + dz * dz);

        double deadZone = 0.2;

        double targetVelX = 0;
        double targetVelZ = 0;
        double friction;

        double distError = distance - baxterComp.getStopDistance();

        double dirX = (distance > 0) ? dx / distance : 0;
        double dirZ = (distance > 0) ? dz / distance : 0;

        if (distance > 20.0) {
            baxterTransform.setPosition(targetPos.clone().add(0, 1.5, 0));
            velocityComp.set(Vector3d.ZERO);
            return;
        }

        if (Math.abs(distError) > deadZone) {
            if (distError > 0) {
                targetVelX = dirX * baxterComp.getSpeed();
                targetVelZ = dirZ * baxterComp.getSpeed();
                friction = 0.9;
            } else {
                targetVelX = -dirX * baxterComp.getSpeed() * 0.5;
                targetVelZ = -dirZ * baxterComp.getSpeed() * 0.5;
                friction = 0.8;
            }
        } else {
            friction = 0.5;
        }

        double targetVelY = dy * 0.1;

        double newX = (currentVel.x * friction) + (targetVelX * (1.0 - friction));
        double newY = (currentVel.y * 0.9) + (targetVelY * 0.1);
        double newZ = (currentVel.z * friction) + (targetVelZ * (1.0 - friction));

        velocityComp.set(new Vector3d(newX, newY, newZ));
        baxterTransform.setPosition(currentPos.add(newX, newY, newZ));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return DontPerish.baxterComponent;
    }
}