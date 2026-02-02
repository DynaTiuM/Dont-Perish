package org.tact.features.comfort.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class ComfortConfig {
    public static final BuilderCodec<ComfortConfig> CODEC;
    public boolean enabled = true;
    public float comfortLossSpeed = 0.5f;
    public float maxComfort = 100.0F;
    public float lerpSpeed = 2.0F;

    public Map<String, Float> comfortValues = new HashMap<>();

    public ComfortConfig() {
        comfortValues.put("Furniture_Tavern_Bed", 3.0F);
    }

    public float getBlockComfort(String blockId) {
        return comfortValues.getOrDefault(blockId, 0.0F);
    }

    static {
        BuilderCodec.Builder<ComfortConfig> b = BuilderCodec.builder(
                ComfortConfig.class,
                ComfortConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v,
                cfg -> cfg.enabled);


        b.append(new KeyedCodec<>("ComfortLossSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.comfortLossSpeed = v,
                cfg -> cfg.comfortLossSpeed);

        CODEC = b.build();
    }
}
