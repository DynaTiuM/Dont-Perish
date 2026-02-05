package org.tact.core.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FoodDefinition {
    public String itemId;
    public double decayTime;
    public float hunger;
    public float comfort;

    public FoodDefinition() {

    }

    public FoodDefinition(String itemId, double decayTime, float hunger, float comfort) {
        this.itemId = itemId;
        this.decayTime = decayTime;
        this.hunger = hunger;
        this.comfort = comfort;
    }

    public static final BuilderCodec<FoodDefinition> CODEC;

    static {
        BuilderCodec.Builder<FoodDefinition> builder = BuilderCodec.builder(
                FoodDefinition.class,
                FoodDefinition::new
        );

        builder.append(
                new KeyedCodec<>("Id", Codec.STRING),
                (obj, v) -> obj.itemId = v,
                obj -> obj.itemId
        ).add();

        builder.append(
                new KeyedCodec<>("DecayTime", Codec.DOUBLE),
                (obj, v) -> obj.decayTime = v,
                obj -> obj.decayTime
        ).add();

        builder.append(
                new KeyedCodec<>("Hunger", Codec.FLOAT),
                (obj, v) -> obj.hunger = v,
                obj -> obj.hunger
        ).add();

        builder.append(
                new KeyedCodec<>("Comfort", Codec.FLOAT),
                (obj, v) -> obj.comfort = v,
                obj -> obj.comfort
        ).add();

        CODEC = builder.build();
    }
}