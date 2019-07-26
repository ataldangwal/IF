package com.openogy.classes;

import android.app.Application;


public class GlobalVariable extends Application {

    private static GlobalVariable instance;

    //Global Variable
    private String logintoken;

    public String getLogintoken() {
        return logintoken;
    }

    public void setLogintoken(String token) {
        this.logintoken = token;
    }
}
