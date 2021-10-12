package no.nav.syfo.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.*
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

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
        val tokensConfig: RedisCacheConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofHours(1L))
        cacheConfigurations[CACHENAME_DKIF_IDENT] = defaultConfig
        cacheConfigurations[CACHENAME_TOKENS] = tokensConfig
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
            .withInitialCacheConfigurations(cacheConfigurations)
            .build()
    }

    companion object {
        const val CACHENAME_DKIF_IDENT = "dkifident"
        const val CACHENAME_TOKENS = "tokens"
    }
}
