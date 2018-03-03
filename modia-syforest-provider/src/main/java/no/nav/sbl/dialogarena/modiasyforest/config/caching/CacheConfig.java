package no.nav.sbl.dialogarena.modiasyforest.config.caching;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.sf.ehcache.config.PersistenceConfiguration.Strategy.NONE;
import static net.sf.ehcache.store.MemoryStoreEvictionPolicy.LRU;


@Configuration
@EnableCaching
public class CacheConfig {

    public static final CacheConfiguration AKTOER = setupCache("aktoer");
    public static final CacheConfiguration TPS = setupCache("tps");
    public static final CacheConfiguration DISKRESJONSKODE = setupCache("diskresjonskode");
    public static final CacheConfiguration SYKMELDING = setupCache("sykmelding");
    public static final CacheConfiguration SYKEPENGESOKNAD = setupCache("sykepengesoknad");
    public static final CacheConfiguration ORGANISASJON = setupCache("organisasjon");
    public static final CacheConfiguration ARBEIDSFORHOLD = setupCache("arbeidsforhold");
    public static final CacheConfiguration DKIF = setupCache("dkif");
    public static final CacheConfiguration SYFO = setupCache("syfo");
    public static final CacheConfiguration EGENANSATT = setupCache("egenansatt");
    public static final CacheConfiguration TIDSLINJER = setupCache("tidslinjer");
    public static final CacheConfiguration TILGANG = setupCache("tilgang");

    @Bean
    public CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(AKTOER);
        config.addCache(TPS);
        config.addCache(DISKRESJONSKODE);
        config.addCache(SYKMELDING);
        config.addCache(SYKEPENGESOKNAD);
        config.addCache(ORGANISASJON);
        config.addCache(ARBEIDSFORHOLD);
        config.addCache(DKIF);
        config.addCache(SYFO);
        config.addCache(EGENANSATT);
        config.addCache(TIDSLINJER);
        config.addCache(TILGANG);
        return CacheManager.newInstance(config);
    }

    @Bean
    public EhCacheCacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public UserKeyGenerator userkeygenerator() {
        return new UserKeyGenerator();
    }

    @Bean
    public KeyGenerator keygenerator() {
        return new KeyGenerator();
    }

    private static CacheConfiguration setupCache(String name) {
        return new CacheConfiguration(name, 1000)
                .memoryStoreEvictionPolicy(LRU)
                .timeToIdleSeconds(3600)
                .timeToLiveSeconds(3600)
                .persistence(new PersistenceConfiguration().strategy(NONE));
    }

}

