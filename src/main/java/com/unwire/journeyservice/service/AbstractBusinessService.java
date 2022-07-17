package com.unwire.journeyservice.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractBusinessService<I, O> implements BusinessService<I, O> {

    @Override
    public Mono<O> doAction(I request) {
        return Mono.just( request )
            .flatMap( this::validateRequest )
            .flatMap( this::execute )
            .flatMap( response -> postExecution( request, response ) )
            .doOnError( Mono::error );
    }

    protected abstract String getServiceName();

    protected abstract Mono<I> validateRequest(I request);


    protected Mono<O> postExecution(I request, O response) {
        return Mono.just( response );
    }
}
