package com.unwire.journeyservice.service.v1.impl;

import static com.unwire.journeyservice.constant.ResponseCode.INVALID_USER_PERMISSION;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.exception.JourneyException;
import com.unwire.journeyservice.service.AbstractBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetJourneyService extends AbstractBusinessService<JourneyDto, JourneyDto> {

    private final ReactiveRedisCache reactiveRedisCache;

    @Autowired
    public GetJourneyService(ReactiveRedisCache reactiveRedisCache) {
        this.reactiveRedisCache = reactiveRedisCache;
    }

    @Override
    protected String getServiceName() {
        return GetJourneyService.class.getName();
    }

    @Override
    protected Mono<JourneyDto> validateRequest(JourneyDto request) {
        return reactiveRedisCache.getKeys( request.getRequestUser() )
            .map( list -> {
                return list.stream()
                    .filter( journey -> journey.contains( request.getJourneyId() ) )
                    .findFirst()
                    .orElseThrow( () ->
                        new JourneyException( INVALID_USER_PERMISSION.getCode(),
                            INVALID_USER_PERMISSION.getMessage(), request.getRequestUser() )
                    );
            } )
            .flatMap( key -> {
                return reactiveRedisCache.getValue( key, new TypeReference<JourneyDto>() {
                } );
            } );
    }


    @Override
    public Mono<JourneyDto> execute(JourneyDto journeyDto) {
        return Mono.just( journeyDto );
    }
}
