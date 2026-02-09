package org.tact.core.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.tact.features.baxter.config.BaxterConfig;
import org.tact.features.comfort.config.ComfortConfig;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.temperature.config.TemperatureConfig;

public class ModConfig {
    public static final BuilderCodec<ModConfig> CODEC;

    public float version = 1.1F;

    public HungerConfig hunger = new HungerConfig();
    public BaxterConfig baxter = new BaxterConfig();
    public SeasonsConfig seasons = new SeasonsConfig();
    public TemperatureConfig temperature = new TemperatureConfig();
    public ComfortConfig comfort = new ComfortConfig();
    public FoodDecayConfig foodDecay = new FoodDecayConfig();
    public GlobalFoodConfig globalFood = new GlobalFoodConfig();


    public ModConfig() {}

    static {
        BuilderCodec.Builder<ModConfig> b = BuilderCodec.builder(ModConfig.class, ModConfig::new);

        b.append(new KeyedCodec<>("Version", Codec.FLOAT),
                (cfg, v) -> cfg.version = v,
                cfg -> cfg.version).add();

        b.append(new KeyedCodec<>("Hunger", HungerConfig.CODEC),
                (cfg, v) -> cfg.hunger = v,
                cfg -> cfg.hunger).add();

        b.append(new KeyedCodec<>("Baxter", BaxterConfig.CODEC),
                (cfg, v) -> cfg.baxter = v,
                cfg -> cfg.baxter).add();

        b.append(new KeyedCodec<>("Seasons", SeasonsConfig.CODEC),
                (cfg, v) -> cfg.seasons = v,
                cfg -> cfg.seasons).add();

        b.append(new KeyedCodec<>("Temperature", TemperatureConfig.CODEC),
                (cfg, v) -> cfg.temperature = v,
                cfg -> cfg.temperature).add();

        b.append(new KeyedCodec<>("Comfort", ComfortConfig.CODEC),
                (cfg, v) -> cfg.comfort = v,
                cfg -> cfg.comfort).add();

        b.append(new KeyedCodec<>("FoodDecay", FoodDecayConfig.CODEC),
                (cfg, v) -> cfg.foodDecay = v,
                cfg -> cfg.foodDecay).add();

        b.append(new KeyedCodec<>("Food", GlobalFoodConfig.CODEC),
                (cfg, v) -> cfg.globalFood = v,
                cfg -> cfg.globalFood).add();

        CODEC = b.build();
    }
}