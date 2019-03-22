package no.nav.sbl.dialogarena.modiasyforest.controller.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class PostAdresse {
    public UstrukturertAdresse ustrukturertAdresse;
}
