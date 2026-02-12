package org.tact.features.comfort.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class ComfortComponent implements Component<EntityStore>{
    public static final BuilderCodec<ComfortComponent> CODEC;

    private float environmentalGain;
    private float comfortBuffer;
    private float auraGain;

    private float lastWeatherMalus;

    public static ComponentType<EntityStore, ComfortComponent> TYPE;

    private transient float lastAppliedBonus = -1.0f;

    public ComfortComponent() {
        this.environmentalGain = 0.0F;
        this.auraGain = 0.0F;
        this.comfortBuffer = 0.0F;
        this.lastWeatherMalus = 0.0F;
    }

    static {
        BuilderCodec.Builder<ComfortComponent> builder = BuilderCodec.builder(
                ComfortComponent.class,
                ComfortComponent::new
        );
        builder.append(
                new KeyedCodec<>("ComfortBuffer", Codec.FLOAT),
                (cfg, v) -> cfg.comfortBuffer = v,
                cfg -> cfg.comfortBuffer
        ).add();

        CODEC = builder.build();
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        ComfortComponent cloned = new ComfortComponent();
        cloned.environmentalGain = this.environmentalGain;
        cloned.auraGain = this.auraGain;
        cloned.comfortBuffer = this.comfortBuffer;
        cloned.lastAppliedBonus = this.lastAppliedBonus;
        cloned.lastWeatherMalus = this.lastWeatherMalus;
        return cloned;
    }

    public float getEnvironmentalGain() {
        return this.environmentalGain;
    }
    public void setEnvironmentalGain(float gain) {
        this.environmentalGain = gain;
    }

    public float getAuraGain() { return this.auraGain; }
    public void setAuraGain(float gain) { this.auraGain = gain; }

    public float getComfortBuffer() { return comfortBuffer; }
    public void addComfortBuffer(float amount) { this.comfortBuffer += amount; }
    public void reduceComfortBuffer(float amount) {
        this.comfortBuffer = Math.max(0, this.comfortBuffer - amount);
    }

    public float getLastAppliedBonus() { return lastAppliedBonus; }
    public void setLastAppliedBonus(float v) { this.lastAppliedBonus = v; }

    public void setLastWeatherMalus(float malus) { this.lastWeatherMalus = malus; }
    public float getLastWeatherMalus() { return this.lastWeatherMalus; }
    public static ComponentType<EntityStore, ComfortComponent> getComponentType() {
        return TYPE;
    }
}