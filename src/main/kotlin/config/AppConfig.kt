package hu.fkv.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@PropertySource(value = ["classpath:application.properties"])
open class AppConfig {
    companion object {
        @JvmStatic
        @Bean
        fun propertyConfigInDev() = PropertySourcesPlaceholderConfigurer()
    }
}
