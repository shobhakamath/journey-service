package com.unwire.journeyservice.service;

import reactor.core.publisher.Mono;

/*
 * Here I stands for Input or Request
 * and
 * O stands for Output and Response
 * */
public interface BusinessService<I, O> {

    Mono<O> doAction(I req);

    Mono<O> execute(I request);
}
