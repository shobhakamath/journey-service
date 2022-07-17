package com.unwire.journeyservice.cache;

import static com.unwire.journeyservice.constant.JourneyConstants.REDIS_PATTERN;
import static com.unwire.journeyservice.constant.ResponseCode.NOT_FOUND;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unwire.journeyservice.exception.ResourceNotFoundException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveRedisCache {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Autowired
    public ReactiveRedisCache(
        ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }


    public <T> Mono<T> storeIn(String fullKey, T value, long expirationInSeconds) {
        return reactiveRedisTemplate.opsForValue().set( fullKey, value, Duration.ofSeconds( expirationInSeconds ) )
            .map( bool -> value );
    }

    public <T> Mono<T> getValue(String fullKey, TypeReference<T> toValueTypeRef) {
        return reactiveRedisTemplate.opsForValue().get( fullKey )
            .switchIfEmpty( Mono.just( Collections.emptyList() ) )
            .map( obj -> {
                ObjectMapper mapper = new ObjectMapper();
                T value = mapper.convertValue( obj, getJavaType( toValueTypeRef ) );
                return value;
            } );
    }

    private static <T> JavaType getJavaType(TypeReference<T> toValueTypeRef) {
        return new ObjectMapper().getTypeFactory().constructType( toValueTypeRef );
    }


    public Mono<List<String>> getKeys(String pattern) {
        return reactiveRedisTemplate.keys( MessageFormat.format( REDIS_PATTERN, pattern ) ).collectList()
            .map( list -> {
                if( list.size() == 0 ) {
                    throw new ResourceNotFoundException( NOT_FOUND.getCode(), NOT_FOUND.getMessage(), pattern );
                }
                return list;
            } );
    }

    public <T> Mono<List<T>> getValuesByListKeys(List<String> keys, TypeReference<List<T>> toValueTypeRef) {
        return
            reactiveRedisTemplate.opsForValue().multiGet( keys )
                .map( obj -> {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.convertValue( obj, toValueTypeRef );
                } );
    }

    public Mono<Long> removeByKey(String key) {
        return reactiveRedisTemplate.keys( MessageFormat.format( REDIS_PATTERN, key ) ).collectList()
            .flatMap( list -> list.isEmpty() ? Mono.just( 0L ) :
                this.reactiveRedisTemplate.unlink( list.toArray( new String[ 0 ] ) ) );
    }
}
