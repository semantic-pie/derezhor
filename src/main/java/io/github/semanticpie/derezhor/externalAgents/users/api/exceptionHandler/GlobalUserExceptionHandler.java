package io.github.semanticpie.derezhor.externalAgents.users.api.exceptionHandler;

import io.github.semanticpie.derezhor.externalAgents.users.errorsResponse.ApiPieTunesErrorInfo;
import io.github.semanticpie.derezhor.externalAgents.users.services.exceptions.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalUserExceptionHandler {

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    ApiPieTunesErrorInfo
    handleUserAlreadyExistsException(HttpServletRequest req, Exception ex) {
        return new ApiPieTunesErrorInfo(HttpStatus.CONFLICT.value(), req.getRequestURL(), ex.getMessage());
    }
}