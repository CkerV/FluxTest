package com.ckev.fluxtest.model;

/**
 * Created by ckerv on 16/12/5.
 */
public class LoginBean {

    private String userName;
    private String passWord;


    public LoginBean(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
