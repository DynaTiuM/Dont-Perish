package org.tact.features.hunger.config;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import java.util.HashMap;
import java.util.Map;

public class HungerConfig {
    public static final BuilderCodec<HungerConfig> CODEC;

    public boolean enabled = true;

    public Map<String, Float> foodValues = new HashMap<>();
    public float defaultSaturation = 10.0F;

    public float creativeRegenSpeed = 100.0F;
    public float saturationLossSpeed = 1.5F;
    public float saturationLossInterval = 20.0F;

    public float starvingDamage = 2.0F;
    public float starvingDamageInterval = 4.0F;

    public HungerConfig() {
        initializeDefaultFoodValues();
    }

    private void initializeDefaultFoodValues() {
        foodValues.put("Ingredient_Dough", 2.0F);
        foodValues.put("Ingredient_Flour", 1.0F);

        foodValues.put("Plant_Fruit_Apple", 15.0F);
        foodValues.put("Plant_Fruit_Mango", 15.0F);
        foodValues.put("Plant_Fruit_Coconut", 15.0F);

        foodValues.put("Plant_Crop_Potato_Item", 7.0F);
        foodValues.put("Plant_Crop_Carrot_Item", 8.0F);
        foodValues.put("Plant_Crop_Corn_Item", 10.0F);

        foodValues.put("Food_Beef_Raw", 12.0F);
        foodValues.put("Food_Pork_Raw", 10.0F);
        foodValues.put("Food_Chicken_Raw", 8.0F);

        foodValues.put("Food_Bread", 20.0F);
        foodValues.put("Food_Wildmeat_Cooked", 35.0F);
        foodValues.put("Food_Kebab_Meat", 45.0F);
        foodValues.put("Food_Pie_Meat", 65.0F);
    }

    static {
        BuilderCodec.Builder<HungerConfig> b = BuilderCodec.builder(
                HungerConfig.class,
                HungerConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v,
                cfg -> cfg.enabled);

        b.append(new KeyedCodec<>("FoodValues", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                (cfg, v) -> cfg.foodValues = v,
                cfg -> cfg.foodValues);

        b.append(new KeyedCodec<>("DefaultSaturation", Codec.FLOAT),
                (cfg, v) -> cfg.defaultSaturation = v,
                cfg -> cfg.defaultSaturation);

        b.append(new KeyedCodec<>("CreativeRegenSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.creativeRegenSpeed = v,
                cfg -> cfg.creativeRegenSpeed);

        b.append(new KeyedCodec<>("SaturationLossSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.saturationLossSpeed = v,
                cfg -> cfg.saturationLossSpeed);

        b.append(new KeyedCodec<>("SaturationLossInterval", Codec.FLOAT),
                (cfg, v) -> cfg.saturationLossInterval = v,
                cfg -> cfg.saturationLossInterval);

        b.append(new KeyedCodec<>("StarvingDamage", Codec.FLOAT),
                (cfg, v) -> cfg.starvingDamage = v,
                cfg -> cfg.starvingDamage);

        b.append(new KeyedCodec<>("StarvingDamageInterval", Codec.FLOAT),
                (cfg, v) -> cfg.starvingDamageInterval = v,
                cfg -> cfg.starvingDamageInterval);

        CODEC = b.build();
    }
}
