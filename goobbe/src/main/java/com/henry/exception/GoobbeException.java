package com.henry.exception;

public class GoobbeException extends RuntimeException {

    public GoobbeException(){
        super();
    }
    public GoobbeException(String info){
        super(info);
    }
}
