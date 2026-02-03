package org.tact.features.comfort.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class ComfortConfig {
    public static final BuilderCodec<ComfortConfig> CODEC;
    public boolean enabled = true;
    public float comfortLossSpeed = 0.5f;

    public float globalGainMultiplier = 0.3F;

    public float maxStaminaPenaltyPercent = 0.15F;
    public float maxStaminaBonusPercent = 0.8F;

    public float maxDamagePenaltyPercent = 0.15F;
    public float maxDamageBonusPercent = 0.5F;

    public float creativeRegenSpeed = 50.0F;

    public Map<String, Float> comfortValues = new HashMap<>();

    public ComfortConfig() {
        initializeComfortBlocks();
    }

    private void initializeComfortBlocks() {
        register("Furniture_Tavern_Bed");

        String[] village = {"Bench", "Chair", "Stool", "Table"};
        for (String s : village) register("Furniture_Village_" + s);

        String[] ancient = {"Bench", "Chair", "Table", "Wardrobe"};
        for (String s : ancient) register("Furniture_Ancient_" + s);

        String[] crude = {"Stool", "Table", "Wardrobe"};
        for (String s : crude) register("Furniture_Crude_" + s);

        String[] desert = {"Chair", "Table", "Wardrobe"};
        for (String s : desert) register("Furniture_Desert_" + s);

        String[] jungle = {"Bench", "Chair", "Table", "Wardrobe"};
        for (String s : jungle) register("Furniture_Jungle_" + s);

        String[] lumberjack = {"Chair", "Table", "Wardrobe"};
        for (String s : lumberjack) register("Furniture_Lumberjack_" + s);

        String[] kweebec = {"Chair", "Table", "Wardrobe"};
        for (String s : kweebec) register("Furniture_Kweebec_" + s);

        String[] human_ruins = {"Bench", "Chair", "Desk", "Table", "Wardrobe"};
        for (String s : human_ruins) register("Furniture_Human_Ruins_" + s);

        String[] feran = {"Bench", "Stool", "Table", "Wardrobe"};
        for (String s : feran) register("Furniture_Feran_" + s);

        String[] frozen_castle = {"Bench", "Chair", "Table", "Wardrobe"};
        for (String s : frozen_castle) register("Furniture_Frozen_Castle_" + s);

        register("Furniture_Faun_Stool");
        register("Furniture_Temple_Emerald_Stool");
        register("Furniture_Castle_Bench");
    }

    private void register(String blockId) {
        float value = 0.0F;

        if (blockId.contains("Bed")) value = 2.0F;
        else if (blockId.contains("Wardrobe")) value = 1.2F;
        else if (blockId.contains("Table") || blockId.contains("Bench")) value = 1.0F;
        else if (blockId.contains("Chair") || blockId.contains("Stool")) value = 0.5F;
        else if (blockId.contains("Desk")) value = 1.5F;

        if (value > 0.0F) {
            comfortValues.put(blockId, value);
        }
    }

    public float getBlockComfort(String blockId) {
        return comfortValues.getOrDefault(blockId, 0.0F);
    }

    static {
        BuilderCodec.Builder<ComfortConfig> b = BuilderCodec.builder(
                ComfortConfig.class,
                ComfortConfig::new
        );

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v,
                cfg -> cfg.enabled);


        b.append(new KeyedCodec<>("ComfortLossSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.comfortLossSpeed = v,
                cfg -> cfg.comfortLossSpeed);

        b.append(new KeyedCodec<>("GlobalGainMultiplier", Codec.FLOAT),
                (cfg, v) -> cfg.globalGainMultiplier = v,
                cfg -> cfg.globalGainMultiplier);

        CODEC = b.build();
    }
}
