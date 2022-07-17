package com.unwire.journeyservice.service.v1.impl;

import static com.unwire.journeyservice.constant.JourneyConstants.REDIS_PATTERN_SAVE;

import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.service.AbstractBusinessService;
import java.text.MessageFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateJourneyService extends AbstractBusinessService<JourneyDto, JourneyDto> {

    private final ReactiveRedisCache reactiveRedisCache;
    private final Long expirationTime;

    @Autowired
    public CreateJourneyService(ReactiveRedisCache reactiveRedisCache,
        @Value( "${spring.cache.redis.expiration-seconds}" ) Long expirationTime) {
        this.reactiveRedisCache = reactiveRedisCache;
        this.expirationTime = expirationTime;
    }

    @Override
    protected String getServiceName() {
        return CreateJourneyService.class.getName();
    }

    @Override
    protected Mono<JourneyDto> validateRequest(JourneyDto request) {
        return Mono.just( request );
    }

    @Override
    public Mono<JourneyDto> execute(JourneyDto journeyDto) {
        journeyDto.setJourneyId( UUID.randomUUID().toString() );
        return reactiveRedisCache
            .storeIn(
                MessageFormat.format( REDIS_PATTERN_SAVE, journeyDto.getRequestUser(), journeyDto.getJourneyId() ),
                journeyDto, expirationTime );
    }
}
