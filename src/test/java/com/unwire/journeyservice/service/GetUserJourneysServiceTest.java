package com.unwire.journeyservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.constant.ResponseCode;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.exception.JourneyException;
import com.unwire.journeyservice.exception.ResourceNotFoundException;
import com.unwire.journeyservice.service.v1.impl.GetUserJourneysService;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class GetUserJourneysServiceTest {

    @Mock
    ReactiveRedisCache reactiveRedisCache;

    GetUserJourneysService getUserJourneysService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks( this );
        getUserJourneysService = new GetUserJourneysService( reactiveRedisCache );
    }

    @Test
    public void testRetrieveJourneyNotBelongedToUser() {

        Mockito.when( reactiveRedisCache.getValue( any(), any( TypeReference.class ) ) )
            .thenReturn( Mono.just(
                Collections.singletonList( "abcdef" ) ) );

        StepVerifier
            .create( getUserJourneysService.doAction( getErrorJourneyDto() ) )
            .expectErrorMatches( throwable -> {
                Assertions.assertTrue( throwable instanceof JourneyException );
                Assertions.assertEquals( ResponseCode.INVALID_USER_PERMISSION.getCode(),
                    ((JourneyException) throwable).getCode() );
                return true;
            } )

            .verify();
    }

    @Test
    public void testRetrieveJourneysForUserResourceNotFoundException() {

        Mockito.when( reactiveRedisCache.getValue( any(), any( TypeReference.class ) ) )
            .thenReturn( Mono.empty() );
        Mockito.when( reactiveRedisCache.getKeys( any() ) )
            .thenReturn( Mono.error( new ResourceNotFoundException("","") ) );


        StepVerifier
            .create( getUserJourneysService.doAction( getSuccessJourneyDto() ) )
            .expectErrorMatches( throwable -> {
                Assertions.assertTrue( throwable instanceof ResourceNotFoundException );

                return true;
            }).verify();
    }

    @Test
    public void testRetrieveJourneysForUsers() {

        Mockito.when( reactiveRedisCache.getValue( any(), any( TypeReference.class ) ) )
            .thenReturn( Mono.just( Collections.singletonList( "journey1" ) ) );
        Mockito.when( reactiveRedisCache.getKeys( any() ) )
            .thenReturn( Mono.just( Collections.singletonList( "journey1_user_" ) ));

        Mockito.when( reactiveRedisCache.getValuesByListKeys( anyList(), any( TypeReference.class ) ) )
            .thenReturn( Mono.just( Collections.singletonList( JourneyDto.builder()
                .journeyId( "journey1" )
                .source( "CPH" )
                .destination( "DXB" )
                .build() ) ) );

        StepVerifier
            .create( getUserJourneysService.doAction( getSuccessJourneyDto() ) )
            .consumeNextWith( journeys -> {
                Assertions.assertEquals( 1, journeys.size() );
                Assertions.assertEquals( "journey1", journeys.get( 0 ).getJourneyId() );
                Assertions.assertEquals( "CPH", journeys.get( 0 ).getSource() );
                Assertions.assertEquals( "DXB", journeys.get( 0 ).getDestination() );
            } )
            .verifyComplete();
    }

    private JourneyDto getSuccessJourneyDto() {
        JourneyDto journeyDto = JourneyDto.builder()
            .build();
        journeyDto.setRequestUser( "user1" );
        journeyDto.setUser( "user1" );
        return journeyDto;
    }

    private JourneyDto getErrorJourneyDto() {
        JourneyDto journeyDto = JourneyDto.builder()
            .build();
        journeyDto.setRequestUser( "user1" );
        journeyDto.setUser( "user2" );
        return journeyDto;
    }
}
