package com.unwire.journeyservice.service;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.constant.ResponseCode;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.exception.JourneyException;
import com.unwire.journeyservice.exception.ResourceNotFoundException;
import com.unwire.journeyservice.service.v1.impl.GetJourneyService;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class GetJourneyServiceTest {

    @Mock
    ReactiveRedisCache reactiveRedisCache;

    GetJourneyService getJourneyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks( this );
        getJourneyService = new GetJourneyService( reactiveRedisCache );
    }

    @Test
    public void testRetrieveJourneyNotBelongedToUser() {

        Mockito.when( reactiveRedisCache.getKeys( any() ) )
            .thenReturn( Mono.just(
                Collections.singletonList( "journeyId" ) ) );

        Mockito.when( reactiveRedisCache.getValue( any(), any( TypeReference.class ) ) )
            .thenReturn( Mono.just(
                Collections.singletonList( "abcdef" ) ) );

        StepVerifier
            .create( getJourneyService.doAction( JourneyDto.builder()
                    .journeyId( "journey2" )
                .build() ) )
            .expectErrorMatches( throwable -> {
                Assertions.assertTrue( throwable instanceof JourneyException );
                Assertions.assertEquals( ResponseCode.INVALID_USER_PERMISSION.getCode(),
                    ((JourneyException) throwable).getCode() );
                return true;
            } )

            .verify();
    }

    @Test
    public void testRetrieveJourneyNotAvailable() {

        Mockito.when( reactiveRedisCache.getKeys( any() ) )
            .thenReturn( Mono.error( new ResourceNotFoundException( ResponseCode.NOT_FOUND.getCode(),
                ResponseCode.NOT_FOUND.getMessage()
            ) ) );

        StepVerifier
            .create( getJourneyService.doAction( JourneyDto.builder().build() ) )
            .expectErrorMatches( throwable -> {
                Assertions.assertTrue( throwable instanceof ResourceNotFoundException );
                Assertions.assertEquals( ResponseCode.NOT_FOUND.getCode(),
                    ((ResourceNotFoundException) throwable).getCode() );
                return true;
            } )

            .verify();
    }


    @Test
    public void testSuccessFlow() {

        Mockito.when( reactiveRedisCache.getKeys( any() ) )
            .thenReturn( Mono.just(
                Collections.singletonList( "journeyId" ) ) );

        Mockito.when( reactiveRedisCache.getValue( any(), any( TypeReference.class ) ) )
            .thenReturn( Mono.just( JourneyDto.builder()
                .source( "CPH" )
                .destination( "DXB" )
                .build() ) );

        StepVerifier
            .create( getJourneyService.doAction( getJourneyDto() ) )
            .consumeNextWith( journeyDto -> {
                Assertions.assertEquals( "CPH", journeyDto.getSource() );
                Assertions.assertEquals( "DXB", journeyDto.getDestination() );
            } )
            .verifyComplete();
    }

    private JourneyDto getJourneyDto() {
        JourneyDto journeyDto = JourneyDto.builder()
            .journeyId( "journeyId" )
            .build();
        journeyDto.setRequestUser( "user1" );
        return journeyDto;
    }
}
