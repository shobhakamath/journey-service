package com.unwire.journeyservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
@NoArgsConstructor
@ToString
public class JourneyDto extends RequestDto {

    String journeyId;

    @NotNull
    @NotEmpty( message = "Source cannot be empty" )
    String source;

    @NotEmpty( message = "Destination cannot be empty" )
    String destination;
}
