package com.unwire.journeyservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
@NoArgsConstructor
public class RequestDto {

    String user;
    String requestUser;
}
