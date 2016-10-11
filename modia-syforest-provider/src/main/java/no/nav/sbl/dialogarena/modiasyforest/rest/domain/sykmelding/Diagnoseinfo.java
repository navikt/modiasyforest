package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;

import java.time.LocalDate;

public class Diagnoseinfo {
    public Diagnose hoveddiagnose;
    public Diagnose bidiagnose;
    public String fravaersgrunnLovfestet;
    public String fravaerBeskrivelse;
    public Boolean svangerskap;
    public Boolean yrkesskade;
    public LocalDate yrkesskadeDato;
}
