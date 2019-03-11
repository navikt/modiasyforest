package no.nav.sbl.dialogarena.modiasyforest.exception;

import lombok.Getter;

@Getter
public class ApiError {
    private String error;

    public ApiError(String error) {
        this.error = error;
    }
}

