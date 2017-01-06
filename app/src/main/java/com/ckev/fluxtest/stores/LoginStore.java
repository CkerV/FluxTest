package com.ckev.fluxtest.stores;

import com.ckev.fluxtest.actions.Action;
import com.ckev.fluxtest.actions.LoginAction;
import com.ckev.fluxtest.model.LoginBean;
import com.ckev.fluxtest.model.LoginResponseBean;

/**
 * Created by ckerv on 16/12/5.
 */
public class LoginStore extends Store {

    public LoginStore() {
        super();
        loginResponseBean = new LoginResponseBean();
    }

    private LoginResponseBean loginResponseBean;

    public LoginResponseBean getLoginResponseBean() {
        return loginResponseBean;
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case LoginAction.LOGIN_ACTION:
                LoginBean loginBean = (LoginBean) action.getData();
                if(loginBean.getUserName().equals("123") && loginBean.getPassWord().equals("123")) {
                    loginResponseBean.setSuccess(true);
                } else {
                    loginResponseBean.setSuccess(false);
                }
                break;
            default:break;
        }
        emitStoreChange();
    }

    @Override
    protected StoreChangeEvent changeEvent() {
        return new StoreChangeEvent();
    }

}
