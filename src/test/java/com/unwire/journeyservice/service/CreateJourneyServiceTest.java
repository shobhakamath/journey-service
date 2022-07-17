package com.unwire.journeyservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import com.unwire.journeyservice.cache.ReactiveRedisCache;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.service.v1.impl.CreateJourneyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CreateJourneyServiceTest {

    @Mock
    ReactiveRedisCache reactiveRedisCache;

    CreateJourneyService createJourneyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks( this );
        createJourneyService = new CreateJourneyService( reactiveRedisCache, 0L );
    }


    @Test
    public void testErrorCreateJourney() {
        Mockito.when( reactiveRedisCache.storeIn( any(), any( JourneyDto.class ), anyLong() ) )
            .thenReturn( Mono.error( new Exception("exception") ));
        StepVerifier
            .create( createJourneyService.doAction( getJourneyDto() ))
            .expectErrorMatches( throwable -> {
                Assertions.assertTrue( throwable instanceof Exception );
                Assertions.assertEquals( "exception",throwable.getMessage() );
                return true;
            } )

            .verify();
    }
    @Test
    public void testCreateJourney() {
        Mockito.when( reactiveRedisCache.storeIn( any(), any( JourneyDto.class ), anyLong() ) )
            .thenReturn( Mono.just(getJourneyDto()));
        StepVerifier
            .create( createJourneyService.doAction( getJourneyDto() ))
            .consumeNextWith( journeyDto -> {
                Assertions.assertEquals( "CPH", journeyDto.getSource() );
                Assertions.assertEquals( "DXB", journeyDto.getDestination() );
            } )
            .verifyComplete();
    }
    private JourneyDto getJourneyDto() {
        JourneyDto journeyDto = JourneyDto.builder()
            .source( "CPH" )
            .destination( "DXB" )
            .build();
        journeyDto.setRequestUser( "user1" );
        return journeyDto;
    }
}
