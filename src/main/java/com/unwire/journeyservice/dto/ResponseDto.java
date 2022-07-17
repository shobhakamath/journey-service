package com.unwire.journeyservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@JsonPropertyOrder( { "code", "message", "data" } )
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE )
@ApiModel( description = "This model contains success response data" )
public class ResponseDto<T> {

    int code;
    String message;
    T data;
}
