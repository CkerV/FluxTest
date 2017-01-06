package com.ckev.fluxtest.actions;

import com.ckev.fluxtest.dispatcher.Dispatcher;
import com.ckev.fluxtest.model.LoginBean;

/**
 * 一个有语义的Helper类,通过此类由Dispatacher分发action到各个store
 * Created by ckerv on 16/12/4.
 */
public class ActionCreator {

    private static ActionCreator INSTANCE;

    private final Dispatcher mDispatcher;

    private ActionCreator(Dispatcher mDispatcher) {
        this.mDispatcher = mDispatcher;
    }

    public static ActionCreator getInstance(Dispatcher dispatcher) {
        if(INSTANCE == null) {
            synchronized (ActionCreator.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ActionCreator(dispatcher);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 外部调用此方法进行MessageAction的分发
     * @param data
     */
    public void sendMessage(String data) {
        mDispatcher.dispatch(new MessageAction(MessageAction.MESSAGE_NEW_ACTION, data));
    }

    /**
     * 外部调用此方法进行LoginAction的分发
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        mDispatcher.dispatch(new LoginAction(LoginAction.LOGIN_ACTION, new LoginBean(username, password)));
    }


}
