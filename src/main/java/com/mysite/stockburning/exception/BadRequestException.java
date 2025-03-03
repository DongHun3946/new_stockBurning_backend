package com.mysite.stockburning.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message); //RuntimeException 의 생성자에 message 전달
    }
}
