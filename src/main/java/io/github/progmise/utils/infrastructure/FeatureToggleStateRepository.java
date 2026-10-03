package io.github.progmise.utils.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FeatureToggleStateRepository implements StateRepository {

    public static final long DEFAULT_REFRESH_PERIOD_SECONDS = 30L;

    private static final Logger log = LoggerFactory.getLogger(FeatureToggleStateRepository.class);

    private final StateRepository delegate;
    private final long refreshPeriod;
    private final Map<String, FeatureState> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private FeatureManager featureManager;

    public FeatureToggleStateRepository(StateRepository delegate) {
        this(delegate, DEFAULT_REFRESH_PERIOD_SECONDS);
    }

    public FeatureToggleStateRepository(StateRepository delegate, long refreshPeriodSeconds) {
        this.delegate = delegate;
        this.refreshPeriod = refreshPeriodSeconds;
    }

    public void setFeatureManager(FeatureManager featureManager) {
        this.featureManager = featureManager;
        init();
    }

    private void init() {
        for (Feature feature : featureManager.getFeatures()) {
            FeatureState state = delegate.getFeatureState(feature);
            if (state != null) {
                cache.put(feature.name(), state);
            }
        }

        if (refreshPeriod == 0L) {
            return;
        }
        executorService.scheduleAtFixedRate(this::refreshCache, 0, refreshPeriod, TimeUnit.SECONDS);
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {
        return cache.get(feature.name());
    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        delegate.setFeatureState(featureState);
        cache.put(featureState.getFeature().name(), featureState);
    }

    private void refreshCache() {
        for (String feature : cache.keySet()) {
            try {
                FeatureState state = delegate.getFeatureState(cache.get(feature).getFeature());
                if (state != null) {
                    cache.put(feature, state);
                } else {
                    cache.remove(feature);
                }
            } catch (Exception e) {
                log.error("Error refreshing feature toggle state for feature: {}", feature, e);
                cache.remove(feature);
            }
        }
    }
}
