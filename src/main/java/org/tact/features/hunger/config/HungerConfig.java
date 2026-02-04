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

    public Map<String, FoodProperties> foodValues = new HashMap<>();
    public float defaultSaturation = 10.0F;

    public float creativeRegenSpeed = 100.0F;
    public float saturationLossSpeed = 1.0F;
    public float saturationLossInterval = 10.0F;

    public float starvingDamage = 2.0F;
    public float starvingDamageInterval = 4.0F;

    public final float ANIMATION_SPEED = 30.0F;

    public HungerConfig() {
        initializeDefaultFoodValues();
    }

    private void initializeDefaultFoodValues() {
        addFood("Ingredient_Dough", 2.0F, 0.0F);
        addFood("Ingredient_Flour", 1.0F, 0.0F);

        addFood("Plant_Fruit_Apple", 15.0F, 2.0F);
        addFood("Plant_Fruit_Mango", 15.0F, 3.0F);
        addFood("Plant_Fruit_Coconut", 15.0F, 2.0F);

        addFood("Plant_Crop_Potato_Item", 7.0F, 2.0F);
        addFood("Plant_Crop_Carrot_Item", 8.0F, 2.0F);
        addFood("Plant_Crop_Corn_Item", 10.0F, 2.0F);

        addFood("Food_Beef_Raw", 12.0F, 0.0F);
        addFood("Food_Pork_Raw", 10.0F, 0.0F);
        addFood("Food_Chicken_Raw", 8.0F, 0.0F);

        addFood("Food_Bread", 20.0F, 5.0F);
        addFood("Food_Wildmeat_Cooked", 35.0F, 5.0F);
        addFood("Food_Kebab_Meat", 45.0F, 15.0F);
        addFood("Food_Pie_Meat", 65.0F, 15.0F);
    }

    private void addFood(String id, float hunger, float comfort) {
        foodValues.put(id, new FoodProperties(hunger, comfort));
    }

    public FoodProperties getFoodProperties(String key) {
        return this.foodValues.getOrDefault(key, new FoodProperties());
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
