package com.progmise.utils.infrastructure

import com.progmise.utils.util.logger
import io.micrometer.core.instrument.Metrics
import org.togglz.core.manager.FeatureManager

open class FeatureToggleHelper(
    private val featureManager: FeatureManager,
) {
    private val log by logger()

    fun isActive(feature: Enum<*>): Boolean = isActive(feature.name)

    fun isActive(featureName: String): Boolean =
        try {
            val result = featureManager.isActive { featureName }

            Metrics
                .counter(FEATURE_TOGGLE_METRIC, FEATURE_NAME_TAG, featureName, STATUS_TAG, if (result) "Active" else "NotActive")
                .increment()

            result
        } catch (e: Exception) {
            log.error("Unable to acquire the state for feature toggle {}: {}", featureName, e.message)
            false
        }

    private companion object {
        const val FEATURE_TOGGLE_METRIC = "featureToggle"
        const val FEATURE_NAME_TAG = "featureName"
        const val STATUS_TAG = "status"
    }
}
