package com.example.stock.ExceptionHandlers;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseRegister {
    private String message;
    private List<String> details;
}
