package org.tact.features.hunger.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class HungerConfig {
    public static final BuilderCodec<HungerConfig> CODEC;

    public boolean enabled = true;

    public Map<String, NutritionValue> nutritionValues = new HashMap<>();
    public float defaultSaturation = 10.0F;

    public float creativeRegenSpeed = 100.0F;
    public float saturationLossSpeed = 1.0F;
    public float saturationLossInterval = 10.0F;

    public float starvingDamage = 2.0F;
    public float starvingDamageInterval = 4.0F;

    public HungerConfig() {
        initializeDefaultFoodValues();
    }

    private void initializeDefaultFoodValues() {
    }


    private void addFood(String id, float hunger, float comfort) {
        nutritionValues.put(id, new NutritionValue(hunger, comfort));
    }

    public boolean isEdible(String itemId) {
        return nutritionValues.containsKey(itemId);
    }

    public NutritionValue getNutrition(String itemId) {
        return nutritionValues.get(itemId);
    }

    public static class NutritionValue {
        public float hunger;
        public float comfort;

        public NutritionValue(float hunger, float comfort) {
            this.hunger = hunger;
            this.comfort = comfort;
        }
    }

    static {
        BuilderCodec.Builder<HungerConfig> b = BuilderCodec.builder(
                HungerConfig.class,
                HungerConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v,
                cfg -> cfg.enabled).add();


        b.append(new KeyedCodec<>("DefaultSaturation", Codec.FLOAT),
                (cfg, v) -> cfg.defaultSaturation = v,
                cfg -> cfg.defaultSaturation).add();

        b.append(new KeyedCodec<>("CreativeRegenSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.creativeRegenSpeed = v,
                cfg -> cfg.creativeRegenSpeed).add();

        b.append(new KeyedCodec<>("SaturationLossSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.saturationLossSpeed = v,
                cfg -> cfg.saturationLossSpeed).add();

        b.append(new KeyedCodec<>("SaturationLossInterval", Codec.FLOAT),
                (cfg, v) -> cfg.saturationLossInterval = v,
                cfg -> cfg.saturationLossInterval).add();

        b.append(new KeyedCodec<>("StarvingDamage", Codec.FLOAT),
                (cfg, v) -> cfg.starvingDamage = v,
                cfg -> cfg.starvingDamage).add();

        b.append(new KeyedCodec<>("StarvingDamageInterval", Codec.FLOAT),
                (cfg, v) -> cfg.starvingDamageInterval = v,
                cfg -> cfg.starvingDamageInterval).add();


        CODEC = b.build();
    }
}
