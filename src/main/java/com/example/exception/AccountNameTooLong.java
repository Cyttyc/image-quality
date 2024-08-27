package com.example.exception;

//用户名长度超出范围
public class AccountNameTooLong extends BaseException{
    public AccountNameTooLong() {
    }

    public AccountNameTooLong(String msg) {
        super(msg);
    }
}
