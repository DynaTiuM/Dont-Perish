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

    public float temperatureTransitionSpeed = 0.5F;
    public float extremeTemperatureThreshold = 5.0F;
    public float heatDamage = 1.5F;
    public float coldDamage = 1.5F;
    public float damageInterval = 3.0F;

    public float dayNightTemperatureVariation = 12.0F;


    public boolean staminaLoss = true;
    public float staminaDrainAmount = 5.0f;

    public Map<String, String> protectionItems = new HashMap<>();
    public Map<String, Float> blockTemperatures = new HashMap<>();
    public float maxBlockHeatBonus = 30.0f;
    public float maxBlockColdBonus = -20.0f;


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
        blockTemperatures.put("Fluid_Lava", 5.0F);
        blockTemperatures.put("Furniture_Crude_Torch", 2.0F);
        blockTemperatures.put("Rock_Ice", -2.0F);
        blockTemperatures.put("Rock_Ice_Permafrost", -2.0F);
        blockTemperatures.put("Soil_Snow", -0.5F);
        blockTemperatures.put("Soil_Snow_Half", -0.2F);
    }

    public float getBlockTemperature(String blockId) {
        return blockTemperatures.getOrDefault(blockId, 0.0F);
    }

    static {
        BuilderCodec.Builder<TemperatureConfig> b = BuilderCodec.builder(
                TemperatureConfig.class,
                TemperatureConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled);
        b.append(new KeyedCodec<>("DefaultBaseTemperature", Codec.FLOAT),
                (cfg, v) -> cfg.defaultBaseTemperature = v, cfg -> cfg.defaultBaseTemperature);
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
        b.append(new KeyedCodec<>("StaminaLoss", Codec.BOOLEAN),
                (cfg, v) -> cfg.staminaLoss = v, cfg -> cfg.staminaLoss);
        b.append(new KeyedCodec<>("StaminaDrainAmount", Codec.FLOAT),
                (cfg, v) -> cfg.staminaDrainAmount = v, cfg -> cfg.staminaDrainAmount);
        b.append(new KeyedCodec<>("ProtectionItems", new MapCodec<>(Codec.STRING, HashMap::new)),
                (cfg, v) -> cfg.protectionItems = v, cfg -> cfg.protectionItems);
        b.append(new KeyedCodec<>("BlockTemperatures", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                (cfg, v) -> cfg.blockTemperatures = v, cfg -> cfg.blockTemperatures);
        b.append(new KeyedCodec<>("MaxBlockHeatBonus", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockHeatBonus = v, cfg -> cfg.maxBlockHeatBonus);
        b.append(new KeyedCodec<>("MaxBlockColdBonus", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockColdBonus = v, cfg -> cfg.maxBlockColdBonus);

        CODEC = b.build();
    }
}
