package no.nav.syfo.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.*
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration
import java.util.*


@Configuration
@EnableCaching
@Profile("remote")
class CacheConfig {
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory?): CacheManager {
        val cacheConfigurations: MutableMap<String, RedisCacheConfiguration> = HashMap()
        val defaultConfig: RedisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(2L))
        cacheConfigurations[TILGANGTILBRUKER] = defaultConfig
        cacheConfigurations[TILGANGTILTJENESTEN] = defaultConfig
        cacheConfigurations[TILGANGTILENHET] = defaultConfig
        cacheConfigurations[CACHENAME_DKIF_IDENT] = defaultConfig
        cacheConfigurations[CACHENAME_EREG_VIRKSOMHETSNAVN] = defaultConfig
        cacheConfigurations[CACHENAME_VEILEDER_ENHETER] = defaultConfig
        cacheConfigurations[CACHENAME_SYFOSERVICE_LEDERE] = defaultConfig
        cacheConfigurations[CACHENAME_BRUKER] = defaultConfig
        cacheConfigurations[CACHENAME_NARMESTELEDER_LEDERE] = defaultConfig
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .withInitialCacheConfigurations(cacheConfigurations)
                .build()
    }

    companion object {
        const val TILGANGTILBRUKER = "aktoerid"
        const val TILGANGTILTJENESTEN = "aktoerfnr"
        const val TILGANGTILENHET = "arbeidsforhold"
        const val CACHENAME_DKIF_IDENT = "dkifident"
        const val CACHENAME_EREG_VIRKSOMHETSNAVN = "virksomhetsnavn"
        const val CACHENAME_VEILEDER_ENHETER = "organisasjonnavn"
        const val CACHENAME_SYFOSERVICE_LEDERE = "syfoservicefinnledere"
        const val CACHENAME_BRUKER = "bruker"
        const val CACHENAME_NARMESTELEDER_LEDERE = "ledere"
    }
}
