package no.nav.syfo.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.*
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.web.client.RestTemplate

@EnableScheduling
@Configuration
@EnableRetry
@EnableCaching
class ApplicationConfig {
    @Bean
    fun taskScheduler(): TaskScheduler {
        return ConcurrentTaskScheduler()
    }

    @Bean
    @Primary
    fun restTemplate(vararg interceptors: ClientHttpRequestInterceptor?): RestTemplate {
        val template = RestTemplate()
        template.interceptors = listOf(*interceptors)
        return template
    }
}
