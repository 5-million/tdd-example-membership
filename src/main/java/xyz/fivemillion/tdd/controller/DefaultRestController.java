package xyz.fivemillion.tdd.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import xyz.fivemillion.tdd.dto.ErrorResponse;
import xyz.fivemillion.tdd.error.MembershipError;
import xyz.fivemillion.tdd.exception.MembershipException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class DefaultRestController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return this.makeResponseEntity(errorList.toString());
    }

    private ResponseEntity<Object> makeResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
    }

    @ExceptionHandler({MembershipException.class})
    public ResponseEntity<ErrorResponse> handleRestApiException(final MembershipException exception) {
        log.warn("Membership Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorCode());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final MembershipError error) {
        return ResponseEntity.status(error.getHttpStatus())
                .body(new ErrorResponse(error.name(), error.getDescription()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
        log.warn("exception occur: ", exception);
        return this.makeErrorResponseEntity(MembershipError.UNKNOWN_EXCEPTION);
    }
}
