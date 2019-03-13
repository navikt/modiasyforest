package no.nav.sbl.dialogarena.modiasyforest.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHENAME_AKTORID = "aktoerid";
    public static final String CACHENAME_ARBEIDSFORHOLD = "arbeidsforhold";
    public static final String CACHENAME_DISKRESJONSKODE = "diskresjonskode";
    public static final String CACHENAME_AKTORFNR = "aktoerfnr";
    public static final String CACHENAME_DKIFFNR = "dkiffnr";
    public static final String CACHENAME_EGENANSATT = "egenansatt";
    public static final String CACHENAME_ORGANISASJONNAVN = "organisasjonnavn";
    public static final String CACHENAME_SYFOFINNLEDERE = "syfofinnledere";
    public static final String CACHENAME_SYFOLEDERE = "syfoledere";
    public static final String CACHENAME_SYFOSYKEFORLOP = "syfosykeforlop";
    public static final String CACHENAME_SYKEPENGESOKNAD = "sykepengesoknad";
    public static final String CACHENAME_SYKMELDING = "sykmelding";
    public static final String CACHENAME_TPSBRUKER = "tpsbruker";
    public static final String CACHENAME_TPSNAVN = "tpsnavn";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(asList(
                new ConcurrentMapCache(CACHENAME_AKTORID),
                new ConcurrentMapCache(CACHENAME_AKTORFNR),
                new ConcurrentMapCache(CACHENAME_ARBEIDSFORHOLD),
                new ConcurrentMapCache(CACHENAME_DISKRESJONSKODE),
                new ConcurrentMapCache(CACHENAME_DKIFFNR),
                new ConcurrentMapCache(CACHENAME_EGENANSATT),
                new ConcurrentMapCache(CACHENAME_ORGANISASJONNAVN),
                new ConcurrentMapCache(CACHENAME_SYFOFINNLEDERE),
                new ConcurrentMapCache(CACHENAME_SYFOLEDERE),
                new ConcurrentMapCache(CACHENAME_SYFOSYKEFORLOP),
                new ConcurrentMapCache(CACHENAME_SYKEPENGESOKNAD),
                new ConcurrentMapCache(CACHENAME_SYKMELDING),
                new ConcurrentMapCache(CACHENAME_TPSBRUKER),
                new ConcurrentMapCache(CACHENAME_TPSNAVN)
        ));
        return cacheManager;
    }
}
