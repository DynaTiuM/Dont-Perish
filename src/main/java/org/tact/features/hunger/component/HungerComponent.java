package org.tact.features.hunger.component;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class HungerComponent implements Component<EntityStore> {
    public static final BuilderCodec<HungerComponent> CODEC;

    private float starvingElapsedTime;

    private float digestionBuffer;

    public static ComponentType<EntityStore, HungerComponent> TYPE;

    public HungerComponent() {
        this.starvingElapsedTime = 0.0F;
        this.digestionBuffer = 0.0F;
    }

    public HungerComponent(HungerComponent hungerComponent) {
        this.starvingElapsedTime = hungerComponent.starvingElapsedTime;
        this.digestionBuffer = hungerComponent.digestionBuffer;
    }

    static {
        BuilderCodec.Builder<HungerComponent> builder = BuilderCodec.builder(
                HungerComponent.class,
                HungerComponent::new
        );

        builder.append(
                new KeyedCodec<>("StarvingElapsedTime", Codec.FLOAT),
                (cfg, v) -> cfg.starvingElapsedTime = v,
                cfg -> cfg.starvingElapsedTime
        ).add();

        builder.append(
                new KeyedCodec<>("DigestionBuffer", Codec.FLOAT),
                (cfg, v) -> cfg.digestionBuffer = v,
                cfg -> cfg.digestionBuffer
        ).add();

        CODEC = builder.build();
    }


    @Nullable
    public Component<EntityStore> clone() {
        return new HungerComponent(this);
    }

    public float getStarvingElapsedTime() {
        return this.starvingElapsedTime;
    }

    public void addStarvingElapsedTime(float dt) {
        this.starvingElapsedTime += dt;
    }

    public void resetStarvingElapsedTime() {
        this.starvingElapsedTime = 0.0F;
    }

    public float getDigestionBuffer() { return digestionBuffer; }

    public void addDigestionBuffer(float amount) { this.digestionBuffer += amount; }

    public void reduceDigestionBuffer(float amount) {
        this.digestionBuffer = Math.max(0, this.digestionBuffer - amount);
    }

    public static ComponentType<EntityStore, HungerComponent> getComponentType() {
        return TYPE;
    }
}