package no.nav.syfo.controller.domain.adresse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class PostAdresse implements Serializable {
    public UstrukturertAdresse ustrukturertAdresse;
}
