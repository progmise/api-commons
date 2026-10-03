package com.progmise.utils.infrastructure

import com.progmise.utils.util.logger
import org.togglz.core.Feature
import org.togglz.core.manager.FeatureManager
import org.togglz.core.repository.FeatureState
import org.togglz.core.repository.StateRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class FeatureToggleStateRepository(
    private val delegate: StateRepository,
    private val refreshPeriod: Long = DEFAULT_REFRESH_PERIOD,
) : StateRepository {
    private val log by logger()

    private val cache: MutableMap<String, FeatureState> = ConcurrentHashMap()
    private val executorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var featureManager: FeatureManager

    open fun setFeatureManager(featureManager: FeatureManager) {
        this.featureManager = featureManager
        init()
    }

    private fun init() {
        for (feature in featureManager.features) {
            delegate.getFeatureState(feature)?.let { cache[feature.name()] = it }
        }

        if (refreshPeriod == 0L) return
        executorService.scheduleAtFixedRate({ refreshCache() }, 0, refreshPeriod, TimeUnit.SECONDS)
    }

    override fun getFeatureState(feature: Feature): FeatureState? = cache[feature.name()]

    override fun setFeatureState(featureState: FeatureState) {
        delegate.setFeatureState(featureState)
        cache[featureState.feature.name()] = featureState
    }

    private fun refreshCache() {
        for (feature in cache.keys) {
            try {
                val state = delegate.getFeatureState(cache[feature]?.feature)
                state?.let { cache[feature] = it } ?: cache.remove(feature)
            } catch (ex: Exception) {
                log.error("Error refreshing feature toggle state for feature: {}", feature, ex)
                cache.remove(feature)
            }
        }
    }

    companion object {
        const val DEFAULT_REFRESH_PERIOD = 30L
    }
}
