package com.unwire.journeyservice.service.v1.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.dto.JourneyDto;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class JourneyService {

    ReactiveRedisCache reactiveRedisCache;

    @Autowired
    public JourneyService(ReactiveRedisCache reactiveRedisCache) {
        this.reactiveRedisCache = reactiveRedisCache;
    }

    public Mono<JourneyDto> save(String key) {
        return Mono.just( JourneyDto.builder()
                .journeyId( UUID.randomUUID().toString() )
                .source( "DXB" )
                .destination( "CPH" )
//                .departureDateTime( Instant.now() )
//                .arrivalDateTime( Instant.now() )
                .build() )
            .flatMap( journey -> reactiveRedisCache.storeIn( journey.getJourneyId(), journey, 10000L ) )
            .flatMap( journeyDto -> reactiveRedisCache.getValue( key, new TypeReference<List<String>>() {
                } )

                .flatMap( list -> {
                    list.add( journeyDto.getJourneyId() );
                    return reactiveRedisCache.storeIn( key, list, 10000L );
                } )
                .map( bool -> journeyDto ) );
    }

    public Mono<JourneyDto> getByJourneyId(String journeyId) {
        return reactiveRedisCache.getValue( journeyId, new TypeReference<JourneyDto>() {
        } );
    }

    public Mono<List<JourneyDto>> getByUserId(String userId) {
        return reactiveRedisCache.getValue( userId, new TypeReference<List<String>>() {
            } )
            .flatMap( list -> list.isEmpty() ? Mono.just( Collections.emptyList() ) :
                reactiveRedisCache.getValuesByListKeys( list, new TypeReference<List<JourneyDto>>() {
                } ) );
    }

    public Mono<Long> clearCache(String key) {
        return reactiveRedisCache.removeByKey( key );
    }
}
