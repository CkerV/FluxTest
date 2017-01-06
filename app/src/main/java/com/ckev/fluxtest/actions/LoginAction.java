package com.ckev.fluxtest.actions;

import com.ckev.fluxtest.model.LoginBean;

/**
 * Created by ckerv on 16/12/5.
 */
public class LoginAction extends Action<LoginBean> {

    public static final String LOGIN_ACTION = "login.action";

    public LoginAction(String type, LoginBean data) {
        super(type, data);
    }
}
