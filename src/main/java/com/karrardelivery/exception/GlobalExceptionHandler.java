package com.karrardelivery.exception;

import com.karrardelivery.dto.ErrorDto;
import com.karrardelivery.dto.ErrorListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}

