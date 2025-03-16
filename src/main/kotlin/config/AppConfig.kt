package hu.fkv.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@PropertySource(value = ["classpath:application.properties"])
open class AppConfig {
    @Value("\${app.test.api.url}")
    lateinit var testApiUrl: String
    @Value("\${app.test.api.user}")
    lateinit var testApiUser: String
    @Value("\${app.test.api.pass}")
    lateinit var testApiPass: String

    companion object {
        @JvmStatic
        @Bean
        fun propertyConfigInDev() = PropertySourcesPlaceholderConfigurer()
    }
}
