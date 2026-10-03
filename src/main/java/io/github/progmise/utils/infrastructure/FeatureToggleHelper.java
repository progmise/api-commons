package io.github.progmise.utils.infrastructure;

import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.manager.FeatureManager;

public class FeatureToggleHelper {

    private static final String FEATURE_TOGGLE_METRIC = "featureToggle";
    private static final String FEATURE_NAME_TAG = "featureName";
    private static final String STATUS_TAG = "status";

    private static final Logger log = LoggerFactory.getLogger(FeatureToggleHelper.class);

    private final FeatureManager featureManager;

    public FeatureToggleHelper(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    public boolean isActive(Enum<?> feature) {
        return isActive(feature.name());
    }

    public boolean isActive(String featureName) {
        try {
            boolean result = featureManager.isActive(() -> featureName);

            Metrics
                .counter(
                    FEATURE_TOGGLE_METRIC,
                    FEATURE_NAME_TAG, featureName,
                    STATUS_TAG, result ? "Active" : "NotActive"
                )
                .increment();

            return result;
        } catch (Exception e) {
            log.error("Unable to acquire the state for feature toggle {}: {}", featureName, e.getMessage());
            return false;
        }
    }
}
