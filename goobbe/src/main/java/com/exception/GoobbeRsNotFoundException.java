package com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class GoobbeRsNotFoundException extends RuntimeException {

    public GoobbeRsNotFoundException(){
        super();
    }
    public GoobbeRsNotFoundException(String info){
        super(info);
    }
}
