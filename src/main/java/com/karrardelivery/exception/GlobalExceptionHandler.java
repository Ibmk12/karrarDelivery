package com.karrardelivery.exception;

import com.karrardelivery.dto.ErrorDto;
import com.karrardelivery.dto.ErrorListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.karrardelivery.constant.ErrorCodes.VALIDATION_ERR_CODE;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorListDto> handleRuntimeException(RuntimeException e) {
        log.error("inside handleRuntimeException exception message: {}", e.getMessage(), e);
        ErrorListDto ErrorListDto = new ErrorListDto();
        ErrorDto ErrorDto = com.karrardelivery.dto.ErrorDto.builder()
                .errorCode(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .errorMessage(e.getMessage())
                .build();
        ErrorListDto.getErrorDtoList().add(ErrorDto);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorListDto);
    }

    @ExceptionHandler({DuplicateResourceException.class})
    public ResponseEntity<ErrorListDto> handleDuplicateResourceException(DuplicateResourceException e) {
        log.error("Inside handleDuplicateResourceException - message: {}", e.getMessage(), e);

        ErrorDto errorDto = ErrorDto.builder()
                .errorCode(e.getCode())
                .errorMessage(e.getMessage())
                .build();

        ErrorListDto errorListDto = new ErrorListDto();
        errorListDto.getErrorDtoList().add(errorDto);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorListDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorListDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        List<ErrorDto> errorDtos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapToErrorDto)
                .toList();

        ErrorListDto errorListDto = new ErrorListDto();
        errorListDto.getErrorDtoList().addAll(errorDtos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorListDto);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorListDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("Inside handleResourceNotFoundException - message: {}", e.getMessage(), e);

        ErrorDto errorDto = ErrorDto.builder()
                .errorCode(e.getCode())
                .errorMessage(e.getMessage())
                .build();

        ErrorListDto errorListDto = new ErrorListDto();
        errorListDto.getErrorDtoList().add(errorDto);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorListDto);
    }

    private ErrorDto mapToErrorDto(FieldError fieldError) {
        return ErrorDto.builder()
                .errorCode(VALIDATION_ERR_CODE)
                .errorMessage(fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .build();
    }
}

