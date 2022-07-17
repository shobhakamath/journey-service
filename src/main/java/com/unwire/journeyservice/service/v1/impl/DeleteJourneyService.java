package com.unwire.journeyservice.service.v1.impl;

import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.service.AbstractBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteJourneyService extends AbstractBusinessService<String, Long> {

    private final ReactiveRedisCache reactiveRedisCache;

    @Autowired
    public DeleteJourneyService(ReactiveRedisCache reactiveRedisCache) {
        this.reactiveRedisCache = reactiveRedisCache;
    }

    @Override
    protected String getServiceName() {
        return DeleteJourneyService.class.getName();
    }

    @Override
    protected Mono<String> validateRequest(String request) {
        return Mono.just( request );
    }


    @Override
    public Mono<Long> execute(String key) {
        return reactiveRedisCache.removeByKey( key );
    }
}
