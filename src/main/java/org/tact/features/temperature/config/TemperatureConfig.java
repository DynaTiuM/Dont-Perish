package org.tact.features.temperature.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class TemperatureConfig {
    public static final BuilderCodec<TemperatureConfig> CODEC;

    public boolean enabled = true;

    public float defaultBaseTemperature = 20.0f;

    public float fastResponseSpeed = 0.6F;
    public float slowResponseSpeed = 0.014F;
    public float comfortZoneThreshold = 15.0F;

    public float extremeTemperatureThreshold = 5.0F;
    public float heatDamage = 1.5F;
    public float coldDamage = 1.5F;
    public float damageInterval = 3.0F;

    public float sunExposureHeat = 5.0F;
    public float dayNightTemperatureVariation = 6.0F;

    public boolean staminaLoss = true;
    public float staminaDrainAmount = 5.0F;

    public Map<String, String> protectionItems = new HashMap<>();
    public Map<String, Float> blockTemperatures = new HashMap<>();
    public Map<String, Float> floorTemperatures = new HashMap<>();
    public float maxBlockHeatBonus = 30.0F;
    public float maxBlockColdBonus = -20.0F;

    // Level of the sea
    public float optimalAltitude = 115.0F;
    public float altitudeMaxDrop = 20.0F;
    public float altitudeSpread = 100.0F;

    public TemperatureConfig() {
        initDefaultProtectionItems();
        initBlockTemperatures();
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

    private void initBlockTemperatures() {
        blockTemperatures.put("*Bench_Campfire_State_Definitions_Processing", 5.0F);
        blockTemperatures.put("*Bench_Furnace_State_Definitions_Processing", 9.0F);
        blockTemperatures.put("Fluid_Lava", 3.0F);
        blockTemperatures.put("Furniture_Crude_Torch", 2.0F);

        floorTemperatures.put("Rock_Ice", -2.0F);
        floorTemperatures.put("Rock_Ice_Permafrost", -2.0F);
        floorTemperatures.put("Soil_Snow", -2.0F);
        floorTemperatures.put("Soil_Snow_Half", -1.0F);
        floorTemperatures.put("Rubble_Ice", -1.0F);
        floorTemperatures.put("Rubble_Ice_Medium", -2.0F);
        floorTemperatures.put("Wood_Ice_Trunk", -2.0F);

        floorTemperatures.put("Fluid_Water", -5.0F);

        floorTemperatures.put("Wood_Fire_Trunk", 2.0F);
        floorTemperatures.put("Wood_Fire_Trunk_Full", 2.0F);
    }

    public float getBlockTemperature(String blockId) {
        return blockTemperatures.getOrDefault(blockId, 0.0F);
    }
    public float getFloorTemperature(String blockId) {
        return floorTemperatures.getOrDefault(blockId, 0.0F);
    }

    static {
        BuilderCodec.Builder<TemperatureConfig> b = BuilderCodec.builder(
                TemperatureConfig.class,
                TemperatureConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled).add();
        b.append(new KeyedCodec<>("DefaultBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.defaultBaseTemperature = v, cfg -> cfg.defaultBaseTemperature).add();

        b.append(new KeyedCodec<>("FastResponseSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.fastResponseSpeed = v, cfg -> cfg.fastResponseSpeed).add();
        b.append(new KeyedCodec<>("SlowResponseSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.slowResponseSpeed = v, cfg -> cfg.slowResponseSpeed).add();
        b.append(new KeyedCodec<>("ComfortZoneThreshold", Codec.FLOAT),
                (cfg, v) -> cfg.comfortZoneThreshold = v, cfg -> cfg.comfortZoneThreshold).add();

        b.append(new KeyedCodec<>("ExtremeTemperatureThreshold", Codec.FLOAT),
                (cfg, v) -> cfg.extremeTemperatureThreshold = v, cfg -> cfg.extremeTemperatureThreshold).add();
        b.append(new KeyedCodec<>("HeatDamage", Codec.FLOAT),
                (cfg, v) -> cfg.heatDamage = v, cfg -> cfg.heatDamage).add();
        b.append(new KeyedCodec<>("ColdDamage", Codec.FLOAT),
                (cfg, v) -> cfg.coldDamage = v, cfg -> cfg.coldDamage).add();
        b.append(new KeyedCodec<>("DamageInterval", Codec.FLOAT),
                (cfg, v) -> cfg.damageInterval = v, cfg -> cfg.damageInterval).add();
        b.append(new KeyedCodec<>("StaminaLoss", Codec.BOOLEAN),
                (cfg, v) -> cfg.staminaLoss = v, cfg -> cfg.staminaLoss).add();
        b.append(new KeyedCodec<>("StaminaDrainAmount", Codec.FLOAT),
                (cfg, v) -> cfg.staminaDrainAmount = v, cfg -> cfg.staminaDrainAmount).add();
        b.append(new KeyedCodec<>("ProtectionItems", new MapCodec<>(Codec.STRING, HashMap::new)),
                (cfg, v) -> cfg.protectionItems = v, cfg -> cfg.protectionItems).add();
        b.append(new KeyedCodec<>("BlockTemperatures", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                (cfg, v) -> cfg.blockTemperatures = v, cfg -> cfg.blockTemperatures).add();
        b.append(new KeyedCodec<>("FloorTemperatures", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                (cfg, v) -> cfg.floorTemperatures = v, cfg -> cfg.floorTemperatures).add();
        b.append(new KeyedCodec<>("MaxBlockHeatBonus", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockHeatBonus = v, cfg -> cfg.maxBlockHeatBonus).add();
        b.append(new KeyedCodec<>("MaxBlockColdBonus", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockColdBonus = v, cfg -> cfg.maxBlockColdBonus).add();
        b.append(new KeyedCodec<>("SunExposureHeat", Codec.FLOAT),
                (cfg, v) -> cfg.sunExposureHeat = v, cfg -> cfg.sunExposureHeat).add();

        b.append(new KeyedCodec<>("DayNightTemperatureVariation", Codec.FLOAT),
                (cfg, v) -> cfg.dayNightTemperatureVariation = v, cfg -> cfg.dayNightTemperatureVariation).add();

        b.append(new KeyedCodec<>("OptimalAltitude", Codec.FLOAT),
                (cfg, v) -> cfg.optimalAltitude = v, cfg -> cfg.optimalAltitude).add();
        b.append(new KeyedCodec<>("AltitudeMaxDrop", Codec.FLOAT),
                (cfg, v) -> cfg.altitudeMaxDrop = v, cfg -> cfg.altitudeMaxDrop).add();
        b.append(new KeyedCodec<>("AltitudeSpread", Codec.FLOAT),
                (cfg, v) -> cfg.altitudeSpread = v, cfg -> cfg.altitudeSpread).add();

        CODEC = b.build();
    }
}
