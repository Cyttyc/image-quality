package com.example.exception;

//账户已存在
public class AccountExistException extends BaseException{
    public AccountExistException() {
    }

    public AccountExistException(String msg) {
        super(msg);
    }

}
