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

    // Duration of the seasons (in seconds)

    /*
    public float springDuration = 4800.0F;
    public float summerDuration = 4500.0F;
    public float autumnDuration = 5400.0F;
    public float winterDuration = 4500.0F;
  */
     public float springDuration = 25.0F;
     public float summerDuration = 2005.0F;
     public float autumnDuration = 25.0F;
     public float winterDuration = 25.0F;

    // Temperature mechanics
    public float springBaseTemperature = 15.0F;
    public float summerBaseTemperature = 35.0F;
    public float autumnBaseTemperature = 10.0F;
    public float winterBaseTemperature = 0.0F;

    public float temperatureTransitionSpeed = 0.5F;
    public float extremeTemperatureThreshold = 5.0F;
    public float heatDamage = 1.0F;
    public float coldDamage = 1.5F;
    public float damageInterval = 3.0F;

    public boolean staminaLoss = true;
    public float staminaDrainAmount = 5.0f;

    public Map<String, String> protectionItems = new HashMap<>();

    // Meteo
    public boolean enableWeatherControl = true;
    public float springRainDuration = 120.0f;

    public int baseDayDurationSeconds = 1200;
    public int baseNightDurationSeconds = 600;

    public SeasonsConfig() {
        initDefaultProtectionItems();
    }

    private void initDefaultProtectionItems() {
        // TODO: Create the items
        // SUMMER
        protectionItems.put("IceCube", "SUMMER");
        protectionItems.put("Item_Fan", "SUMMER");
        protectionItems.put("Item_SunHat", "SUMMER");

        // WINTER
        protectionItems.put("WinterCoat", "WINTER");
        protectionItems.put("Item_Scarf", "WINTER");
        protectionItems.put("Item_Gloves", "WINTER");
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

    public float getSeasonBaseTemp(int seasonOrdinal) {
        return switch(seasonOrdinal) {
            case 0 -> springBaseTemperature;
            case 1 -> summerBaseTemperature;
            case 2 -> autumnBaseTemperature;
            case 3 -> winterBaseTemperature;
            default -> 20.0F;
        };
    }

    static {
        BuilderCodec.Builder<SeasonsConfig> b = BuilderCodec.builder(
                SeasonsConfig.class,
                SeasonsConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled);

        b.append(new KeyedCodec<>("SpringDuration", Codec.FLOAT),
                (cfg, v) -> cfg.springDuration = v, cfg -> cfg.springDuration);
        b.append(new KeyedCodec<>("SummerDuration", Codec.FLOAT),
                (cfg, v) -> cfg.summerDuration = v, cfg -> cfg.summerDuration);
        b.append(new KeyedCodec<>("AutumnDuration", Codec.FLOAT),
                (cfg, v) -> cfg.autumnDuration = v, cfg -> cfg.autumnDuration);
        b.append(new KeyedCodec<>("WinterDuration", Codec.FLOAT),
                (cfg, v) -> cfg.winterDuration = v, cfg -> cfg.winterDuration);

        b.append(new KeyedCodec<>("SpringBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.springBaseTemperature = v, cfg -> cfg.springBaseTemperature);
        b.append(new KeyedCodec<>("SummerBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.summerBaseTemperature = v, cfg -> cfg.summerBaseTemperature);
        b.append(new KeyedCodec<>("AutumnBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.autumnBaseTemperature = v, cfg -> cfg.autumnBaseTemperature);
        b.append(new KeyedCodec<>("WinterBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.winterBaseTemperature = v, cfg -> cfg.winterBaseTemperature);

        b.append(new KeyedCodec<>("TemperatureTransitionSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.temperatureTransitionSpeed = v, cfg -> cfg.temperatureTransitionSpeed);
        b.append(new KeyedCodec<>("ExtremeTemperatureThreshold", Codec.FLOAT),
                (cfg, v) -> cfg.extremeTemperatureThreshold = v, cfg -> cfg.extremeTemperatureThreshold);
        b.append(new KeyedCodec<>("HeatDamage", Codec.FLOAT),
                (cfg, v) -> cfg.heatDamage = v, cfg -> cfg.heatDamage);
        b.append(new KeyedCodec<>("ColdDamage", Codec.FLOAT),
                (cfg, v) -> cfg.coldDamage = v, cfg -> cfg.coldDamage);
        b.append(new KeyedCodec<>("DamageInterval", Codec.FLOAT),
                (cfg, v) -> cfg.damageInterval = v, cfg -> cfg.damageInterval);

        b.append(new KeyedCodec<>("ProtectionItems", new MapCodec<>(Codec.STRING, HashMap::new)),
                (cfg, v) -> cfg.protectionItems = v, cfg -> cfg.protectionItems);

        b.append(new KeyedCodec<>("EnableWeatherControl", Codec.BOOLEAN),
                (cfg, v) -> cfg.enableWeatherControl = v, cfg -> cfg.enableWeatherControl);
        b.append(new KeyedCodec<>("SpringRainDuration", Codec.FLOAT),
                (cfg, v) -> cfg.springRainDuration = v, cfg -> cfg.springRainDuration);


        b.append(new KeyedCodec<>("StaminaLoss", Codec.BOOLEAN),
                (cfg, v) -> cfg.staminaLoss = v, cfg -> cfg.staminaLoss);
        b.append(new KeyedCodec<>("StaminaDrainAmount", Codec.FLOAT),
                (cfg, v) -> cfg.staminaDrainAmount = v, cfg -> cfg.staminaDrainAmount);

        b.append(new KeyedCodec<>("BaseDayDurationSeconds", Codec.INTEGER),
                (cfg, v) -> cfg.baseDayDurationSeconds = v, cfg -> cfg.baseDayDurationSeconds);

        b.append(new KeyedCodec<>("BaseNightDurationSeconds", Codec.INTEGER),
                (cfg, v) -> cfg.baseNightDurationSeconds = v, cfg -> cfg.baseNightDurationSeconds);

        CODEC = b.build();
    }
}
