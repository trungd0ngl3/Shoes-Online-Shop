package com.shoestore.shoestoreWeb.exception;

import com.shoestore.shoestoreWeb.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Log log = LogFactory.getLog(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception){
        log.error("Unhandled exception: ", exception);

        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_ERROR;

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse
                        .builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse
                        .builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse
                        .builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
                );
    }



    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constrainViolation = exception.getBindingResult()
                    .getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constrainViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        }catch (IllegalArgumentException e){
            log.error("Unhandled exception", exception);
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse
                        .builder()
                        .code(errorCode.getCode())
                        .message(mapAttributes(errorCode.getMessage(), attributes))
                        .build()
                );
    }

    private String mapAttributes(String msg, Map<String, Object> attributes){
        return msg.replace("{min}", attributes.get("min").toString());
    }
}
