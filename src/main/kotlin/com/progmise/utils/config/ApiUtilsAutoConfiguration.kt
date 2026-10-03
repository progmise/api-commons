package com.progmise.utils.config

import com.progmise.utils.exception.ApiExceptionHandler
import com.progmise.utils.infrastructure.FeatureToggleHelper
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.togglz.core.manager.FeatureManager

@AutoConfiguration
open class ApiUtilsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    open fun apiExceptionHandler(): ApiExceptionHandler = ApiExceptionHandler()

    @Bean
    @ConditionalOnBean(FeatureManager::class)
    @ConditionalOnMissingBean
    open fun featureToggleHelper(featureManager: FeatureManager): FeatureToggleHelper = FeatureToggleHelper(featureManager)
}
