package com.myloanapp.exceptions;

public class LoanNotFoundException extends Exception{
    public LoanNotFoundException(String message){
        super(message);
    }
}
