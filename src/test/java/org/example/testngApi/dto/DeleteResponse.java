package org.example.testngApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteResponse {
    private Object body;
    private String statusCode;
    private int statusCodeValue;
}
