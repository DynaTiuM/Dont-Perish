package org.tact.features.itemStats.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class ItemStatsConfig {
    public static final Codec<ItemStatsConfig> CODEC;
    public Map<String, ItemStats> items = new HashMap<>();
    public boolean enabled = true;

    public ItemStatsConfig() {
        ItemStats iceCubeStats = new ItemStats();
        iceCubeStats.thermalOffset = -20.0F;
        iceCubeStats.comfortModifier = -5.0F;
        iceCubeStats.activeInInventory = true;
        items.put("DP_Compact_Ice_Cube", iceCubeStats);

        ItemStats fan = new ItemStats();
        fan.thermalOffset = -12.0F;
        fan.activeInHand = true;
        fan.requireUsage = true;
        items.put("DP_Fan", fan);

        ItemStats torch = new ItemStats();
        torch.thermalOffset = 2.0F;
        torch.activeInHand = true;
        items.put("Furniture_Crude_Torch", torch);

        ItemStats sunHat = new ItemStats();
        sunHat.insulationHeating = 0.4F;
        sunHat.insulationCooling = 0.0F;
        sunHat.activeInArmor = true;
        items.put("DP_Sun_Hat", sunHat);

        ItemStats coat = new ItemStats();
        coat.insulationCooling = 0.6F;
        coat.thermalOffset = 8.0F;
        coat.activeInArmor = true;
        items.put("DP_Coat", coat);

        ItemStats scarf = new ItemStats();
        scarf.insulationCooling = 0.2F;
        scarf.thermalOffset = 3.0F;
        scarf.activeInArmor = true;
        items.put("DP_Scarf", scarf);

        ItemStats gloves = new ItemStats();
        gloves.insulationCooling = 0.15F;
        gloves.thermalOffset = 1.0F;
        gloves.activeInArmor = true;
        items.put("DP_Gloves", gloves);
    }

    static {
        BuilderCodec.Builder<ItemStatsConfig> builder = BuilderCodec.builder(ItemStatsConfig.class, ItemStatsConfig::new);
        builder.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v, cfg -> cfg.enabled).add();
        builder.append(new KeyedCodec<>("Items", new MapCodec<>(ItemStats.CODEC, HashMap::new)),
                (cfg, v) -> cfg.items = v, cfg -> cfg.items).add();
        CODEC = builder.build();
    }

    public ItemStats getStats(String itemId) {
        if (!enabled) return null;
        return items.getOrDefault(itemId, null);
    }
}