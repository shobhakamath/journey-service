package com.unwire.journeyservice.service;

import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.service.v1.impl.CreateJourneyService;
import com.unwire.journeyservice.service.v1.impl.DeleteJourneyService;
import com.unwire.journeyservice.service.v1.impl.GetJourneyService;
import com.unwire.journeyservice.service.v1.impl.GetUserJourneysService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "spring.redis.password=" } )
public class IntegrationTests {


    private final static RedisServer REDISSERVER = new RedisServer( 6379 );

    List<String> journeyList;

    @Autowired
    DeleteJourneyService deleteJourneyService;

    @Autowired
    CreateJourneyService createJourneyService;

    @Autowired
    GetUserJourneysService getUserJourneysService;

    @Autowired
    GetJourneyService getJourneyService;

    JourneyDto journeyDto;


    @AfterEach
     void shutDownRedisServer() {
        REDISSERVER.stop();
    }

    @BeforeEach
    void clearCache() {
        REDISSERVER.start();
        StepVerifier.create( Mono.just( "user1" )
                .flatMap( deleteJourneyService::doAction ) )
            .expectNextMatches( count -> {
                return true;
            } )
            .verifyComplete();
        journeyList=new ArrayList<>();
    }


    @Test
    public void testRedisUsers10000Records() {
        runSaveRetrieveUsersJourneys( 10000 );
    }


    @Test
    public void testRedisJourneys1000Records() {
        runSaveJourneys( 1000 );
    }


    private void runSaveJourneys(int NO_ITERATIONS) {
        Date startDate = new Date();
        for( int i = 0; i < NO_ITERATIONS; i++ ) {
            StepVerifier.create( Mono.just( getJourneyDto() )
                    .flatMap( createJourneyService::doAction ) )
                .expectNextMatches( journey -> {
                    Assertions.assertNotNull( journey.getJourneyId() );
                    journeyList.add( journey.getJourneyId() );
                    return true;
                } )
                .verifyComplete();
        }
        System.out.println(
            "Total time taken to save " + NO_ITERATIONS + " records :" + (new Date().getTime() - startDate.getTime()) );

        startDate = new Date();

        for(String journeyId:journeyList) {
            JourneyDto journeyDto=getJourneyDto();
            journeyDto.setJourneyId( journeyId );
            StepVerifier.create( Mono.just( journeyDto )
                    .flatMap( getJourneyService::doAction ) )
                .expectNextMatches( journey -> {
                    Assertions.assertEquals(journeyId,journey.getJourneyId());
                    return true;
                } ).verifyComplete();
        }
        System.out.println(
            "Total time taken to retrieve these  " + NO_ITERATIONS + " records :" + (new Date().getTime()
                - startDate.getTime()) );
    }

    private void runSaveRetrieveUsersJourneys(int NO_ITERATIONS) {
        Date startDate = new Date();
        for( int i = 0; i < NO_ITERATIONS; i++ ) {
            StepVerifier.create( Mono.just( getJourneyDto() )
                    .flatMap( createJourneyService::doAction ) )
                .expectNextMatches( journey -> {
                    Assertions.assertNotNull( journey.getJourneyId() );
                    return true;
                } )
                .verifyComplete();
        }
        System.out.println(
            "Total time taken to save " + NO_ITERATIONS + " records :" + (new Date().getTime() - startDate.getTime()) );

        startDate = new Date();
        StepVerifier.create( Mono.just( getJourneyDto() )
                .flatMap( getUserJourneysService::doAction ) )
            .expectNextMatches( journeys -> {
                Assertions.assertEquals( NO_ITERATIONS, journeys.stream()
                    .map( JourneyDto::getJourneyId )
                    .distinct()
                    .count() );
                return true;
            } ).verifyComplete();
        System.out.println(
            "Total time taken to retrieve these  " + NO_ITERATIONS + " records :" + (new Date().getTime()
                - startDate.getTime()) );
    }

    private JourneyDto getJourneyDto() {
        JourneyDto journeyDto = JourneyDto.builder()
            .source( "CPH" )
            .destination( "DXB" )
            .build();
        journeyDto.setRequestUser( "user1" );
        journeyDto.setUser( "user1" );
        return journeyDto;
    }
}
