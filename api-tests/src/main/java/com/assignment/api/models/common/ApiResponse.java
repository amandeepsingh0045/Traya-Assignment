package com.assignment.api.models.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic Petstore message/error response, e.g.
 * {"code":200,"type":"unknown","message":"logged in user session:1697..."}
 * {"code":1,"type":"error","message":"Pet not found"}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {
    private Integer code;
    private String type;
    private String message;
}
