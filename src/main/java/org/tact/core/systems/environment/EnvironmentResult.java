package org.tact.core.systems.environment;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentResult {
    private final Map<String, Integer> blockCounts = new HashMap<>();

    private final int radius;

    private boolean isUnderRoof = false;
    private String blockUnderFeet = "Empty";

    public EnvironmentResult(int radius) {
        this.radius = radius;
    }

    public void addBlock(String blockId) {
        blockCounts.merge(blockId, 1, Integer::sum);
    }

    public Map<String, Integer> getBlockCounts() {
        return blockCounts;
    }

    public int getRadius() {
        return radius;
    }

    public void setBlockUnderFeet(String blockId) { this.blockUnderFeet = blockId; }
    public String getBlockUnderFeet() { return blockUnderFeet; }

    public void setRoof(boolean isUnderRoof) {
        this.isUnderRoof = isUnderRoof;
    }
    public boolean isUnderRoof() { return isUnderRoof; }
}
