package no.nav.syfo.api.exception

import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.util.WebUtils
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@ControllerAdvice
class ControllerExceptionHandler @Inject constructor(
    private val metrikk: Metrikk
) {
    private val BAD_REQUEST_MSG = "Vi kunne ikke tolke inndataene"
    private val FORBIDDEN_MSG = "Handling er forbudt"
    private val INTERNAL_MSG = "Det skjedde en uventet feil"
    private val UNAUTHORIZED_MSG = "Autorisasjonsfeil"

    @ExceptionHandler(
        Exception::class,
        IllegalArgumentException::class,
        JwtTokenUnauthorizedException::class,
        ForbiddenException::class
    )
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ApiError> {
        val headers = HttpHeaders()
        return if (ex is JwtTokenUnauthorizedException) {
            handleJwtTokenUnauthorizedException(ex, headers, request)
        } else if (ex is ForbiddenException) {
            handleForbiddenException(ex, headers, request)
        } else if (ex is IllegalArgumentException) {
            handleIllegalArgumentException(ex, headers, request)
        } else {
            val status = HttpStatus.INTERNAL_SERVER_ERROR
            handleExceptionInternal(ex, ApiError(status.value(), INTERNAL_MSG), headers, status, request)
        }
    }

    private fun handleJwtTokenUnauthorizedException(ex: JwtTokenUnauthorizedException, headers: HttpHeaders, request: WebRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.UNAUTHORIZED
        return handleExceptionInternal(ex, ApiError(status.value(), UNAUTHORIZED_MSG), headers, status, request)
    }

    private fun handleForbiddenException(ex: ForbiddenException, headers: HttpHeaders, request: WebRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.FORBIDDEN
        return handleExceptionInternal(ex, ApiError(status.value(), FORBIDDEN_MSG), headers, status, request)
    }

    private fun handleIllegalArgumentException(ex: IllegalArgumentException, headers: HttpHeaders, request: WebRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        return handleExceptionInternal(ex, ApiError(status.value(), BAD_REQUEST_MSG), headers, status, request)
    }

    private fun handleExceptionInternal(ex: Exception, body: ApiError, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<ApiError> {
        metrikk.tellHttpKall(status.value())
        if (HttpStatus.INTERNAL_SERVER_ERROR == status) {
            log.error("Uventet feil: {} : {}", ex.javaClass.toString(), ex.message, ex)
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST)
        }
        return ResponseEntity(body, headers, status)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)
    }
}
