package no.nav.sbl.dialogarena.modiasyforest.rest.domain.sykmelding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Diagnoseinfo {
    public Diagnose hoveddiagnose;
    public List<Diagnose> bidiagnoser = new ArrayList<>();
    public String fravaersgrunnLovfestet;
    public String fravaerBeskrivelse;
    public Boolean svangerskap;
    public Boolean yrkesskade;
    public LocalDate yrkesskadeDato;
}
