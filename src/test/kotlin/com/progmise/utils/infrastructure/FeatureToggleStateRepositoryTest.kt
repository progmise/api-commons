package com.progmise.utils.infrastructure

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.togglz.core.Feature
import org.togglz.core.manager.FeatureManager
import org.togglz.core.repository.FeatureState
import org.togglz.core.repository.StateRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeatureToggleStateRepositoryTest {
    private val testFeature: Feature = Feature { "MY_FEATURE" }

    private val delegate: StateRepository = mock()
    private val featureManager: FeatureManager = mock()

    @Test
    fun `serves state from cache after init`() {
        val state = FeatureState(testFeature, true)
        whenever(featureManager.features).thenReturn(setOf(testFeature))
        whenever(delegate.getFeatureState(testFeature)).thenReturn(state)

        val repository = FeatureToggleStateRepository(delegate, refreshPeriod = 0)
        repository.setFeatureManager(featureManager)

        assertTrue(repository.getFeatureState(testFeature)!!.isEnabled)
    }

    @Test
    fun `setFeatureState writes through to delegate and cache`() {
        whenever(featureManager.features).thenReturn(emptySet())

        val repository = FeatureToggleStateRepository(delegate, refreshPeriod = 0)
        repository.setFeatureManager(featureManager)

        val state = FeatureState(testFeature, false)
        repository.setFeatureState(state)

        verify(delegate).setFeatureState(state)
        assertEquals(state, repository.getFeatureState(testFeature))
    }

    @Test
    fun `returns null for unknown feature`() {
        whenever(featureManager.features).thenReturn(emptySet())

        val repository = FeatureToggleStateRepository(delegate, refreshPeriod = 0)
        repository.setFeatureManager(featureManager)

        assertNull(repository.getFeatureState(testFeature))
    }
}
