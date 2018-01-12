package no.nav.sbl.dialogarena.modiasyforest.services;

import no.nav.sbl.dialogarena.modiasyforest.rest.domain.Sykeforloep;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Hendelse;
import no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.Tidslinje;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static no.nav.sbl.dialogarena.modiasyforest.rest.domain.tidslinje.TidslinjeType.valueOf;

public class TidslinjeService {

    @Inject
    private SykeforloepService sykeforloepService;
    @Inject
    private TidslinjeHendelserService tidslinjeHendelserService;

    @Cacheable(value = "tidslinjer", keyGenerator = "userkeygenerator")
    public List<Tidslinje> hentTidslinjer(String fnr, String type) {
        List<Sykeforloep> sykeforloep = sykeforloepService.hentSykeforloep(fnr);

        if (sykeforloep.isEmpty()) {
            Tidslinje statiskTidslinje = new Tidslinje()
                    .withHendelser(tidslinjeHendelserService.hentHendelser(valueOf(type))
                            .stream()
                            .map(leggTypePaaTekstkey(type))
                            .collect(toList()));

            return asList(statiskTidslinje);
        } else {
            Comparator<Tidslinje> sisteForest = (t1, t2) -> t2.startdato.compareTo(t1.startdato);
            AtomicInteger i = new AtomicInteger(0);
            return sykeforloep.stream().map(sf -> {
                Stream<Hendelse> hendelserFraSyfoservice = sf.hendelser
                        .stream()
                        .map(leggTilAntallDager(sf))
                        .map(leggTilIDer(i));
                Stream<Hendelse> statiskeHendelser = tidslinjeHendelserService.hentHendelser(valueOf(type)).stream();
                return new Tidslinje()
                        .withStartDato(sf.oppfoelgingsdato)
                        .withHendelser(
                                concat(
                                        hendelserFraSyfoservice,
                                        statiskeHendelser
                                ).map(leggTypePaaTekstkey(type)).collect(toList()));
            }).sorted(sisteForest).collect(toList());
        }
    }

    private Function<Hendelse, Hendelse> leggTilIDer(AtomicInteger i) {
        return h -> {
            switch (h.type) {
                case AKTIVITETSKRAV_VARSEL: return h.withId("a" + i.incrementAndGet());
                case NY_NAERMESTE_LEDER: return h.withId("nl" + i.incrementAndGet());
                default: return h;
            }
        };
    }

    private Function<Hendelse, Hendelse> leggTilAntallDager(Sykeforloep s) {
        return h -> h.withAntallDager((int)DAYS.between(s.oppfoelgingsdato, h.inntruffetdato));

    }

    private Function<Hendelse, Hendelse> leggTypePaaTekstkey(String type) {
        return h -> {
            String keyMedType = h.tekstkey + '.' + type;
            return h.withTekstkey(keyMedType);
        };
    }
}
