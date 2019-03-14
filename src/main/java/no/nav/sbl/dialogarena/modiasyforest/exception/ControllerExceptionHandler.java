package no.nav.sbl.dialogarena.modiasyforest.exception;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.utils.Metrikk;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    private Metrikk metrikk;

    @Inject
    public ControllerExceptionHandler(Metrikk metrikk) {
        this.metrikk = metrikk;
    }

    @ExceptionHandler({Exception.class, IllegalArgumentException.class, ForbiddenException.class})
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof ForbiddenException) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            ForbiddenException unfe = (ForbiddenException) ex;

            return handleForbiddenException(unfe, headers, status, request);
        } else if (ex instanceof IllegalArgumentException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            IllegalArgumentException cnae = (IllegalArgumentException) ex;

            return handleIllegalArgumentException(cnae, headers, status, request);
        } else {
            metrikk.tellHttpKall(HttpStatus.INTERNAL_SERVER_ERROR.value());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    private ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Handling er forbudt";

        return handleExceptionInternal(ex, new ApiError(errorMessage), headers, status, request);
    }

    private ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Vi kunne ikke tolke inndataene";

        return handleExceptionInternal(ex, new ApiError(errorMessage), headers, status, request);
    }

    private ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
