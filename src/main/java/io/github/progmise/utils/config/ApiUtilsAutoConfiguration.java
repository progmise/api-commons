package io.github.progmise.utils.config;

import io.github.progmise.utils.exception.ApiExceptionHandler;
import io.github.progmise.utils.infrastructure.FeatureToggleHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.togglz.core.manager.FeatureManager;

@AutoConfiguration
public class ApiUtilsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApiExceptionHandler apiExceptionHandler() {
        return new ApiExceptionHandler();
    }

    @Bean
    @ConditionalOnBean(FeatureManager.class)
    @ConditionalOnMissingBean
    public FeatureToggleHelper featureToggleHelper(FeatureManager featureManager) {
        return new FeatureToggleHelper(featureManager);
    }
}
