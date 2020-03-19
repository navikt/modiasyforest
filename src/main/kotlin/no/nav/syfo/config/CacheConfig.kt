package no.nav.syfo.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
        cacheConfigurations[CACHENAME_VEILEDER_ENHETER] = defaultConfig
        cacheConfigurations[CACHENAME_VEILEDER_LDAP] = defaultConfig
        cacheConfigurations[CACHENAME_PERSON_INFO] = defaultConfig
        cacheConfigurations[CACHENAME_SYKEPENGESOKNAD] = defaultConfig
        cacheConfigurations[CACHENAME_TPSBRUKER] = defaultConfig
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
        const val CACHENAME_VEILEDER_ENHETER = "organisasjonnavn"
        const val CACHENAME_VEILEDER_LDAP = "syfofinnledere"
        const val CACHENAME_PERSON_INFO = "syfoledere"
        const val CACHENAME_SYKEPENGESOKNAD = "sykepengesoknad"
        const val CACHENAME_TPSBRUKER= "tpsbruker"
    }
}
