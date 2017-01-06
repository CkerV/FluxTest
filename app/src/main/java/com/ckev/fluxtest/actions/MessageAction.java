package com.ckev.fluxtest.actions;

/**
 * 继承自{@link Action}
 * Created by ckerv on 16/12/5.
 */
public class MessageAction extends Action<String> {

    public static final String MESSAGE_NEW_ACTION = "message_new_action";

    public MessageAction(java.lang.String type, String data) {
        super(type, data);
    }
}
