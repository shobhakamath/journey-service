package com.unwire.journeyservice.utils;

import com.unwire.journeyservice.constant.ResponseCode;
import com.unwire.journeyservice.dto.ErrorResponseDto;
import com.unwire.journeyservice.dto.ResponseDto;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ResponseUtil {

    private final MessageSource messageSource;

    @Autowired
    public ResponseUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public <T> Mono<ResponseDto<T>> defaultReadResponse(T data, String locale) {

        String message = this.messageSource.getMessage( ResponseCode.SUCCESS.getCode(), null,
            this.getLocale( locale ) );
        return Mono.just( data ).map(
            response -> new ResponseDto<>( Integer.parseInt( ResponseCode.SUCCESS.getCode() ), message,
                response ) );
    }

    public <T> Flux<ResponseDto<T>> defaultReadResponseFlux(Flux<T> data) {
        return data.map( response -> new ResponseDto<>( 0, "success", response ) );
    }

    private Locale getLocale(String locale) {
        return locale != null ? new Locale( locale ) : Locale.ENGLISH;
    }


    public ErrorResponseDto error(String code, Object... args) {
        String message = this.messageSource.getMessage( String.valueOf( code ), args, Locale.ENGLISH );
        if( StringUtils.isBlank( message ) ) {
            message = ResponseCode.BACKEND_ERROR.getMessage();
        } else {
            if( args.length > 0 ) {
                message = MessageFormat.format( message, args );
            }
        }

        return new ErrorResponseDto( Integer.parseInt( code ), message, (new Date()).getTime() );
    }
}
