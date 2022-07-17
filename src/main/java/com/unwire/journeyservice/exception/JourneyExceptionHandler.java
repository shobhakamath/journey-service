package com.unwire.journeyservice.exception;

import com.unwire.journeyservice.constant.ResponseCode;
import com.unwire.journeyservice.dto.ErrorResponseDto;
import com.unwire.journeyservice.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class JourneyExceptionHandler {

    private final ResponseUtil responseUtil;

    @Autowired
    public JourneyExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }


    @ExceptionHandler( { JourneyException.class } )
    public final ResponseEntity<ErrorResponseDto> handleUserNotFoundException(JourneyException e,
        WebRequest request) {
        ErrorResponseDto error = responseUtil.error( e.getCode(),
            e.getArgs() );
        return new ResponseEntity<>( error, HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler( ResourceNotFoundException.class )
    public final ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException e,
        WebRequest request) {
        ErrorResponseDto error = responseUtil.error( e.getCode(),
            e.getArgs() );
        return new ResponseEntity<>( error, HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler( Exception.class )
    public final ResponseEntity<ErrorResponseDto> handleException(Exception e,
        WebRequest request) {
        ErrorResponseDto error = responseUtil.error( ResponseCode.ERROR_INTERNAL_SERVER.getCode() );
        return new ResponseEntity<>( error, HttpStatus.INTERNAL_SERVER_ERROR );
    }

    private void generateErrorLog(String className, String message, Object e) {
        log.error( "HS: Got [[" + className + "]] exception with message: " + message, e );
        log.error( String.format( "HS: Got [[%s]] exception with message: %s", className, message ),
            e );
    }
}