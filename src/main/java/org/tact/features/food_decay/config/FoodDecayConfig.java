package org.tact.features.food_decay.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class FoodDecayConfig {
    public static final Codec<FoodDecayConfig> CODEC;

    public boolean enabled = true;

    public float degradationSpeed = 1.0F;
    public float decayInterval = 5.0F;

    public Map<String, Double> decayTimes = new HashMap<>();

    public FoodDecayConfig() {
    }

    private void addDecay(String id, double seconds) {
        decayTimes.put(id, seconds);
    }

    public double getDecayTime(String itemId) {
        return decayTimes.getOrDefault(itemId, -1.0);
    }

    public boolean isPerishable(String itemId) {
        return decayTimes.containsKey(itemId);
    }

    static {
        BuilderCodec.Builder<FoodDecayConfig> builder = BuilderCodec.builder(FoodDecayConfig.class, FoodDecayConfig::new);
        builder.append(
                new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (obj, v) -> obj.enabled = v,
                obj -> obj.enabled
        ).add();

        builder.append(
                new KeyedCodec<>("DegradationSpeed", Codec.FLOAT),
                (obj, v) -> obj.degradationSpeed = v,
                obj -> obj.degradationSpeed
        ).add();

        builder.append(
                new KeyedCodec<>("DecayInterval", Codec.FLOAT),
                (obj, v) -> obj.decayInterval = v,
                obj -> obj.decayInterval
        ).add();

        CODEC = builder.build();
    }
}
