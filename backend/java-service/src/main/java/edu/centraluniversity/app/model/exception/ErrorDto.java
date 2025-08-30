package edu.centraluniversity.app.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDto {
    private String errorCode;
    private String errorMessage;
}

