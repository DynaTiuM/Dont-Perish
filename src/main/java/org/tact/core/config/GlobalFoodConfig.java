package org.tact.core.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalFoodConfig {
    public static final Codec<GlobalFoodConfig> CODEC;

    public List<FoodDefinition> foods = new ArrayList<>();

    private final double STANDARD_DECAY = 5760.0;
    private final double FAST_DECAY = 2880.0;
    private final double INSTANT_DECAY = 60.0;
    private final double NO_DECAY = -1.0;

    public GlobalFoodConfig() {
        foods.add(new FoodDefinition("Ingredient_Dough", STANDARD_DECAY, 2.0F, 0.0F));
        foods.add(new FoodDefinition("Ingredient_Flour", NO_DECAY, 1.0F, 0.0F));

        foods.add(new FoodDefinition("Plant_Fruit_Apple", INSTANT_DECAY, 15.0F, 2.0F));
        foods.add(new FoodDefinition("Plant_Fruit_Mango", FAST_DECAY, 15.0F, 3.0F));
        foods.add(new FoodDefinition("Plant_Fruit_Coconut", STANDARD_DECAY, 15.0F, 2.0F));

        foods.add(new FoodDefinition("Plant_Crop_Potato_Item", STANDARD_DECAY, 7.0F, 2.0F));
        foods.add(new FoodDefinition("Plant_Crop_Carrot_Item", STANDARD_DECAY, 8.0F, 2.0F));
        foods.add(new FoodDefinition("Plant_Crop_Corn_Item", STANDARD_DECAY, 10.0F, 2.0F));

        foods.add(new FoodDefinition("Food_Beef_Raw", STANDARD_DECAY, 12.0F, 0.0F));
        foods.add(new FoodDefinition("Food_Pork_Raw", STANDARD_DECAY, 10.0F, 0.0F));
        foods.add(new FoodDefinition("Food_Chicken_Raw", STANDARD_DECAY, 8.0F, 0.0F));

        foods.add(new FoodDefinition("Food_Bread", STANDARD_DECAY, 20.0F, 5.0F));
        foods.add(new FoodDefinition("Food_Wildmeat_Cooked", STANDARD_DECAY, 35.0F, 5.0F));
        foods.add(new FoodDefinition("Food_Kebab_Meat", STANDARD_DECAY, 45.0F, 15.0F));
        foods.add(new FoodDefinition("Food_Pie_Meat", STANDARD_DECAY, 65.0F, 15.0F));
    }

    static {
        BuilderCodec.Builder<GlobalFoodConfig> builder = BuilderCodec.builder(GlobalFoodConfig.class, GlobalFoodConfig::new);
        ArrayCodec<FoodDefinition> arrayCodec = new ArrayCodec<>(FoodDefinition.CODEC, FoodDefinition[]::new);
        builder.append(
                new KeyedCodec<>("GlobalFood", arrayCodec),
                (obj, v) -> {
                    obj.foods = new ArrayList<>(Arrays.asList(v));
                },
                obj -> {
                    return obj.foods.toArray(new FoodDefinition[0]);
                }
        ).add();

        CODEC = builder.build();
    }
}