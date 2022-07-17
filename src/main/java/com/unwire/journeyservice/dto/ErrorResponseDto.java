package com.unwire.journeyservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ApiModel( description = "This model contains error information in api response" )
@JsonIgnoreProperties( ignoreUnknown = true )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private Integer errorCode;
    private String message;
    private long timestamp;
}
