package com.divjazz.recommendic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Response<T>(String time, int code, HttpStatus status, String message, String exception, T data) {
}
