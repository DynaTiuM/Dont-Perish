package org.tact.features.comfort.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class ComfortConfig {
    public static final BuilderCodec<ComfortConfig> CODEC;
    public boolean enabled = true;
    public float comfortLossSpeed = 0.15f;
    public float comfortLossInterval = 1.0F;

    public float globalGainMultiplier = 0.3F;

    public float maxStaminaPenaltyPercent = 0.15F;
    public float maxStaminaBonusPercent = 0.5F;

    public float maxDamagePenaltyPercent = 0.15F;
    public float maxDamageBonusPercent = 0.15F;

    public float maxBlockPenaltyPercent = 0.10F;
    public float maxBlockBonusPercent = 0.15F;

    public float creativeRegenSpeed = 100.0F;

    public float musicComfortBonus = 3.0F;
    public float speechComfortBonus = 2.0F;
    public float laughterComfortBonus = 3.0F;

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
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled).add();

        b.append(new KeyedCodec<>("ComfortLossSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.comfortLossSpeed = v, cfg -> cfg.comfortLossSpeed).add();

        b.append(new KeyedCodec<>("ComfortLossInterval", Codec.FLOAT),
                (cfg, v) -> cfg.comfortLossInterval = v, cfg -> cfg.comfortLossInterval).add();

        b.append(new KeyedCodec<>("GlobalGainMultiplier", Codec.FLOAT),
                (cfg, v) -> cfg.globalGainMultiplier = v, cfg -> cfg.globalGainMultiplier).add();

        b.append(new KeyedCodec<>("CreativeRegenSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.creativeRegenSpeed = v, cfg -> cfg.creativeRegenSpeed).add();

        b.append(new KeyedCodec<>("MaxStaminaPenaltyPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxStaminaPenaltyPercent = v, cfg -> cfg.maxStaminaPenaltyPercent).add();

        b.append(new KeyedCodec<>("MaxStaminaBonusPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxStaminaBonusPercent = v, cfg -> cfg.maxStaminaBonusPercent).add();

        b.append(new KeyedCodec<>("MaxDamagePenaltyPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxDamagePenaltyPercent = v, cfg -> cfg.maxDamagePenaltyPercent).add();

        b.append(new KeyedCodec<>("MaxDamageBonusPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxDamageBonusPercent = v, cfg -> cfg.maxDamageBonusPercent).add();

        b.append(new KeyedCodec<>("MaxBlockPenaltyPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockPenaltyPercent = v, cfg -> cfg.maxBlockPenaltyPercent).add();

        b.append(new KeyedCodec<>("MaxBlockBonusPercent", Codec.FLOAT),
                (cfg, v) -> cfg.maxBlockBonusPercent = v, cfg -> cfg.maxBlockBonusPercent).add();

        b.append(new KeyedCodec<>("ComfortValues", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                (cfg, v) -> cfg.comfortValues = v, cfg -> cfg.comfortValues).add();


        b.append(new KeyedCodec<>("MusicComfortBonus", Codec.FLOAT),
                (cfg, v) -> cfg.musicComfortBonus = v, cfg -> cfg.musicComfortBonus).add();
        b.append(new KeyedCodec<>("SpeechComfortBonus", Codec.FLOAT),
                (cfg, v) -> cfg.speechComfortBonus = v, cfg -> cfg.speechComfortBonus).add();
        b.append(new KeyedCodec<>("LaughterComfortBonus", Codec.FLOAT),
                (cfg, v) -> cfg.laughterComfortBonus = v, cfg -> cfg.laughterComfortBonus).add();

        CODEC = b.build();
    }
}
