package org.tact.common.environment;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentResult {
    private final Map<String, Integer> blockCounts = new HashMap<>();

    private final int radius;

    public EnvironmentResult(int radius) {
        this.radius = radius;
    }

    public void addBlock(String blockId) {
        blockCounts.merge(blockId, 1, Integer::sum);
    }

    public int getCount(String blockId) {
        return blockCounts.getOrDefault(blockId, 0);
    }

    public boolean contains(String blockId) {
        return blockCounts.containsKey(blockId);
    }

    public Map<String, Integer> getBlockCounts() {
        return blockCounts;
    }

    public int getRadius() {
        return radius;
    }
}
