package org.tact.features.hunger.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FoodProperties {
    public static final Codec<FoodProperties> CODEC;

    private float hunger;
    private float comfort;

    public FoodProperties() {
        this(0, 0);
    }

    public FoodProperties(float hunger, float comfort) {
        this.hunger = hunger;
        this.comfort = comfort;
    }

    static {
        BuilderCodec.Builder<FoodProperties> builder = BuilderCodec.builder(FoodProperties.class, FoodProperties::new);

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

    public float getHunger() { return hunger; }
    public float getComfort() { return comfort; }
}
