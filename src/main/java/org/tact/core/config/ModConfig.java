package org.tact.core.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.tact.features.baxter.config.BaxterConfig;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.seasons.config.SeasonsConfig;

public class ModConfig {
    public static final BuilderCodec<ModConfig> CODEC;

    public HungerConfig hunger = new HungerConfig();
    public BaxterConfig baxter = new BaxterConfig();
    public SeasonsConfig seasons = new SeasonsConfig();

    public ModConfig() {}

    static {
        BuilderCodec.Builder<ModConfig> b = BuilderCodec.builder(ModConfig.class, ModConfig::new);

        b.append(new KeyedCodec<>("Hunger", HungerConfig.CODEC),
                (cfg, v) -> cfg.hunger = v,
                cfg -> cfg.hunger);

        b.append(new KeyedCodec<>("Baxter", BaxterConfig.CODEC),
                (cfg, v) -> cfg.baxter = v,
                cfg -> cfg.baxter);

        b.append(new KeyedCodec<>("Seasons", SeasonsConfig.CODEC),
                (cfg, v) -> cfg.seasons = v,
                cfg -> cfg.seasons);

        CODEC = b.build();
    }
}