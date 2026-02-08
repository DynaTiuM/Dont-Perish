package org.tact.features.seasons.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class SeasonsConfig {
    public static final BuilderCodec<SeasonsConfig> CODEC;

    public boolean enabled = true;

    public float springDuration = 14_400.0F; // 5 days
    public float summerDuration = 11_520.0F; // 4 days
    public float autumnDuration = 14_400.0F; // 5 days
    public float winterDuration = 11_520.0F; // 4 days

    // Temperature mechanics
    public float springTemperatureModifier = -5.0F;
    public float summerTemperatureModifier = 15.0F;
    public float autumnTemperatureModifier = -10.0F;
    public float winterTemperatureModifier = -25.0F;

    // Weather
    public boolean enableWeatherControl = true;
    public float springRainDuration = 120.0f;

    // Default Day & Night durations of Hytale
    // Total duration = 2880s
    public int baseDayDurationSeconds = 1728;
    public int baseNightDurationSeconds = 1152;

    public SeasonsConfig() {
    }

    public float getSeasonDuration(int seasonOrdinal) {
        return switch(seasonOrdinal) {
            case 0 -> springDuration;
            case 1 -> summerDuration;
            case 2 -> autumnDuration;
            case 3 -> winterDuration;
            default -> 4500.0F;
        };
    }

    public float getSeasonTemperatureModifier(int seasonOrdinal) {
        return switch(seasonOrdinal) {
            case 0 -> springTemperatureModifier;
            case 1 -> summerTemperatureModifier;
            case 2 -> autumnTemperatureModifier;
            case 3 -> winterTemperatureModifier;
            default -> 0.0F;
        };
    }


    static {
        BuilderCodec.Builder<SeasonsConfig> b = BuilderCodec.builder(
                SeasonsConfig.class,
                SeasonsConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled).add();

        b.append(new KeyedCodec<>("SpringDuration", Codec.FLOAT),
                (cfg, v) -> cfg.springDuration = v, cfg -> cfg.springDuration).add();
        b.append(new KeyedCodec<>("SummerDuration", Codec.FLOAT),
                (cfg, v) -> cfg.summerDuration = v, cfg -> cfg.summerDuration).add();
        b.append(new KeyedCodec<>("AutumnDuration", Codec.FLOAT),
                (cfg, v) -> cfg.autumnDuration = v, cfg -> cfg.autumnDuration).add();
        b.append(new KeyedCodec<>("WinterDuration", Codec.FLOAT),
                (cfg, v) -> cfg.winterDuration = v, cfg -> cfg.winterDuration).add();

        b.append(new KeyedCodec<>("EnableWeatherControl", Codec.BOOLEAN),
                (cfg, v) -> cfg.enableWeatherControl = v, cfg -> cfg.enableWeatherControl).add();
        b.append(new KeyedCodec<>("SpringRainDuration", Codec.FLOAT),
                (cfg, v) -> cfg.springRainDuration = v, cfg -> cfg.springRainDuration).add();

        b.append(new KeyedCodec<>("SpringBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.springTemperatureModifier = v, cfg -> cfg.springTemperatureModifier).add();
        b.append(new KeyedCodec<>("SummerBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.summerTemperatureModifier = v, cfg -> cfg.summerTemperatureModifier).add();
        b.append(new KeyedCodec<>("AutumnBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.autumnTemperatureModifier = v, cfg -> cfg.autumnTemperatureModifier).add();
        b.append(new KeyedCodec<>("WinterBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.winterTemperatureModifier = v, cfg -> cfg.winterTemperatureModifier).add();

        b.append(new KeyedCodec<>("BaseDayDurationSeconds", Codec.INTEGER),
                (cfg, v) -> cfg.baseDayDurationSeconds = v, cfg -> cfg.baseDayDurationSeconds).add();

        b.append(new KeyedCodec<>("BaseNightDurationSeconds", Codec.INTEGER),
                (cfg, v) -> cfg.baseNightDurationSeconds = v, cfg -> cfg.baseNightDurationSeconds).add();


        CODEC = b.build();
    }
}
