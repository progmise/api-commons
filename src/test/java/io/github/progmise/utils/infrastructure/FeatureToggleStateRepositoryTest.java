package io.github.progmise.utils.infrastructure;

import org.junit.jupiter.api.Test;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureToggleStateRepositoryTest {

    private final Feature testFeature = () -> "MY_FEATURE";

    private final StateRepository delegate = mock(StateRepository.class);
    private final FeatureManager featureManager = mock(FeatureManager.class);

    @Test
    void servesStateFromCacheAfterInit() {
        FeatureState state = new FeatureState(testFeature, true);
        when(featureManager.getFeatures()).thenReturn(Set.of(testFeature));
        when(delegate.getFeatureState(testFeature)).thenReturn(state);

        FeatureToggleStateRepository repository = new FeatureToggleStateRepository(delegate, 0);
        repository.setFeatureManager(featureManager);

        assertTrue(repository.getFeatureState(testFeature).isEnabled());
    }

    @Test
    void setFeatureStateWritesThroughToDelegateAndCache() {
        when(featureManager.getFeatures()).thenReturn(Set.of());

        FeatureToggleStateRepository repository = new FeatureToggleStateRepository(delegate, 0);
        repository.setFeatureManager(featureManager);

        FeatureState state = new FeatureState(testFeature, false);
        repository.setFeatureState(state);

        verify(delegate).setFeatureState(state);
        assertEquals(state, repository.getFeatureState(testFeature));
    }

    @Test
    void returnsNullForUnknownFeature() {
        when(featureManager.getFeatures()).thenReturn(Set.of());

        FeatureToggleStateRepository repository = new FeatureToggleStateRepository(delegate, 0);
        repository.setFeatureManager(featureManager);

        assertNull(repository.getFeatureState(testFeature));
    }
}
