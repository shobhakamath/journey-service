package com.unwire.journeyservice.service.v1.impl;

import static com.unwire.journeyservice.constant.ResponseCode.INVALID_USER_PERMISSION;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.exception.JourneyException;
import com.unwire.journeyservice.service.AbstractBusinessService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetUserJourneysService extends AbstractBusinessService<JourneyDto, List<JourneyDto>> {

    private final ReactiveRedisCache reactiveRedisCache;

    @Autowired
    public GetUserJourneysService(ReactiveRedisCache reactiveRedisCache) {
        this.reactiveRedisCache = reactiveRedisCache;
    }

    @Override
    protected String getServiceName() {
        return GetUserJourneysService.class.getName();
    }

    @Override
    protected Mono<JourneyDto> validateRequest(JourneyDto request) {
        if( !request.getUser().equals( request.getRequestUser() ) ) {
            throw new JourneyException( INVALID_USER_PERMISSION.getCode(),
                INVALID_USER_PERMISSION.getMessage(), request.getRequestUser() );
        }
        return Mono.just( request );
    }


    @Override
    public Mono<List<JourneyDto>> execute(JourneyDto journeyDto) {
        return reactiveRedisCache.getKeys( journeyDto.getRequestUser() )
            .flatMap( list -> reactiveRedisCache.getValuesByListKeys( list, new TypeReference<List<JourneyDto>>() {
            } ) );
    }
}
