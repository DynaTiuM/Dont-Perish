package org.tact.core.registry;


import org.tact.api.Feature;
import java.util.*;
import java.util.logging.Logger;

public class FeatureRegistry {
    private static final Logger LOGGER = Logger.getLogger(FeatureRegistry.class.getName());
    private final Map<String, Feature> features = new LinkedHashMap<>();

    public void register(Feature feature) {
        if (features.containsKey(feature.getId())) {
            LOGGER.warning("Feature " + feature.getId() + " is already registered!");
            return;
        }
        features.put(feature.getId(), feature);
        LOGGER.info("Registered feature: " + feature.getId());
    }

    public Optional<Feature> getFeature(String id) {
        return Optional.ofNullable(features.get(id));
    }

    public Collection<Feature> getAllFeatures() {
        return Collections.unmodifiableCollection(features.values());
    }

    public Collection<Feature> getEnabledFeatures() {
        return features.values().stream()
                .filter(Feature::isEnabled)
                .toList();
    }
}