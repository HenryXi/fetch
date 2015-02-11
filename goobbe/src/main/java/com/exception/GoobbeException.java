package com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by henxii on 2/11/15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GoobbeException extends RuntimeException {
    public GoobbeException(){
        super();
    }
    public GoobbeException(String info){
        super(info);
    }
}
