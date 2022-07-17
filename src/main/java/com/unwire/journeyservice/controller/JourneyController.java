package com.unwire.journeyservice.controller;

import com.unwire.journeyservice.dto.ErrorResponseDto;
import com.unwire.journeyservice.dto.JourneyDto;
import com.unwire.journeyservice.dto.ResponseDto;
import com.unwire.journeyservice.service.v1.impl.CreateJourneyService;
import com.unwire.journeyservice.service.v1.impl.DeleteJourneyService;
import com.unwire.journeyservice.service.v1.impl.GetJourneyService;
import com.unwire.journeyservice.service.v1.impl.GetUserJourneysService;
import com.unwire.journeyservice.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping( "/v1/journey" )
@Api
public class JourneyController {

    private final CreateJourneyService createJourneyService;
    private final GetJourneyService getJourneyService;
    private final GetUserJourneysService getUserJourneysService;
    private final DeleteJourneyService deleteJourneyService;
    private final ResponseUtil responseUtil;

    @Autowired
    public JourneyController(CreateJourneyService createJourneyService,
        GetJourneyService getJourneyService,
        GetUserJourneysService getUserJourneysService,
        DeleteJourneyService deleteJourneyService,
        ResponseUtil responseUtil) {
        this.createJourneyService = createJourneyService;
        this.getJourneyService = getJourneyService;
        this.getUserJourneysService = getUserJourneysService;
        this.deleteJourneyService = deleteJourneyService;
        this.responseUtil = responseUtil;
    }

    @ApiOperation( "This API is used to save a user journey." )
    @ApiResponses( value = { @ApiResponse( code = 0, message = "Success", response = ResponseDto.class ),
        @ApiResponse( code = 0, message = "Backend Error", response = ErrorResponseDto.class ),
    } )
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public Mono<ResponseDto<JourneyDto>> saveJourney(
        @RequestHeader( value = "method", defaultValue = "en", required = false ) int method,
        @RequestHeader( value = "api-user-id", required = true ) String api_user_id,
        @RequestHeader( value = "locale", defaultValue = "en", required = false ) String locale,
        @RequestBody @Valid JourneyDto journeyDto) {
        journeyDto.setRequestUser( api_user_id );
        return Mono.just( journeyDto )
            .flatMap( createJourneyService::doAction )
            .flatMap( journey -> responseUtil.defaultReadResponse( journey, locale ) );
    }

    @ApiOperation( "This API is used to retrieve the journey given a journeyid." )
    @ApiResponses( value = { @ApiResponse( code = 0, message = "Success", response = ResponseDto.class ),
        @ApiResponse( code = 0, message = "Not Found", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "User cannot retrieve journey {0}", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "Backend Error", response = ErrorResponseDto.class ),
    } )
    @GetMapping( value = "/{journey_id}" )
    public Mono<ResponseDto<JourneyDto>> getJourneys(@PathVariable String journey_id,
        @RequestHeader( value = "api-user-id", required = true ) String api_user_id,
        @RequestHeader( value = "locale", defaultValue = "en", required = false ) String locale) {
        JourneyDto journeyDto = JourneyDto.builder()
            .journeyId( journey_id )
            .build();
        journeyDto.setRequestUser( api_user_id );
        return Mono.just( journeyDto )
            .flatMap( getJourneyService::doAction )
            .flatMap( journey -> responseUtil.defaultReadResponse( journey, locale ) );
    }

    @ApiOperation( "This API is used to retrieve the journeys of a given user." )
    @ApiResponses( value = { @ApiResponse( code = 0, message = "Success", response = ResponseDto.class ),
        @ApiResponse( code = 0, message = "Not Found", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "User cannot retrieve journey {0}", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "Backend Error", response = ErrorResponseDto.class ),
    } )
    @GetMapping( value = "/user/{user_id}/journeys" )
    public Mono<ResponseDto<List<JourneyDto>>> getJourneyByUserId(@PathVariable String user_id,
        @RequestHeader( value = "method", defaultValue = "en", required = false ) int method,
        @RequestHeader( value = "api-user-id", required = true ) String api_user_id,
        @RequestHeader( value = "locale", defaultValue = "en", required = false ) String locale) {
        JourneyDto journeyDto = JourneyDto.builder().build();
        journeyDto.setUser( user_id );
        journeyDto.setRequestUser( api_user_id );
        return Mono.just( journeyDto )
            .flatMap( getUserJourneysService::doAction )
            .flatMap( journeys -> responseUtil.defaultReadResponse( journeys, locale ) );
    }


    @ApiOperation( "This API is used to delete the journeys of a user." )
    @ApiResponses( value = { @ApiResponse( code = 0, message = "Success", response = ResponseDto.class ),
        @ApiResponse( code = 0, message = "Not Found", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "User cannot retrieve journey {0}", response = ErrorResponseDto.class ),
        @ApiResponse( code = 0, message = "Backend Error", response = ErrorResponseDto.class ),
    } )
    @DeleteMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public Mono<ResponseDto<Long>> delete(@RequestHeader( value = "api-user-id", required = true ) String api_user_id,
        @RequestHeader( value = "locale", defaultValue = "en", required = false ) String locale) {
        return Mono.just( api_user_id )
            .flatMap( deleteJourneyService::doAction )
            .flatMap( journey -> responseUtil.defaultReadResponse( journey, locale ) );
    }
}
