package no.nav.sbl.dialogarena.modiasyforest.exception;

import lombok.extern.slf4j.Slf4j;
import no.nav.sbl.dialogarena.modiasyforest.utils.Metrikk;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;
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

    private final String BAD_REQUEST_MSG = "Vi kunne ikke tolke inndataene";
    private final String FORBIDDEN_MSG = "Handling er forbudt";
    private final String INTERNAL_MSG = "Det skjedde en uventet feil";
    private final String UNAUTHORIZED_MSG = "Autorisasjonsfeil";

    private Metrikk metrikk;

    @Inject
    public ControllerExceptionHandler(Metrikk metrikk) {
        this.metrikk = metrikk;
    }

    @ExceptionHandler({Exception.class, OIDCUnauthorizedException.class, IllegalArgumentException.class, ForbiddenException.class})
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof OIDCUnauthorizedException) {
            OIDCUnauthorizedException notAuthorizedException = (OIDCUnauthorizedException) ex;

            return handleOIDCUnauthorizedException(notAuthorizedException, headers, request);
        } else if (ex instanceof ForbiddenException) {
            ForbiddenException forbiddenException = (ForbiddenException) ex;

            return handleForbiddenException(forbiddenException, headers, request);
        } else if (ex instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) ex;

            return handleIllegalArgumentException(illegalArgumentException, headers, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, new ApiError(status.value(), INTERNAL_MSG), headers, status, request);
        }
    }

    private ResponseEntity<ApiError> handleOIDCUnauthorizedException(OIDCUnauthorizedException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return handleExceptionInternal(ex, new ApiError(status.value(), UNAUTHORIZED_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return handleExceptionInternal(ex, new ApiError(status.value(), FORBIDDEN_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex, new ApiError(status.value(), BAD_REQUEST_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        metrikk.tellHttpKall(status.value());

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
