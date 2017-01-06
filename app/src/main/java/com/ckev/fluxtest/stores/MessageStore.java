package com.ckev.fluxtest.stores;


import com.ckev.fluxtest.dispatcher.Dispatcher;
import com.ckev.fluxtest.model.Message;
import com.ckev.fluxtest.actions.MessageAction;
import com.ckev.fluxtest.actions.Action;

/**
 * {@link Store}的具体实现类,将数据变动传递到view层
 * 在{@link #onAction(Action)}方法里进行相应的逻辑判断和传递数据
 * Created by ckerv on 16/12/5.
 */
public class MessageStore extends Store {


    public MessageStore() {
        super();
        message = new Message();
    }

    private Message message;

    /**
     * 只提供Message的get方法,Message的状态只能由{@link Dispatcher#dispatch(Action)}进行更新
     * @return
     */
    public Object getMessage() {
        return message.getMessage();
    }

    /**
     * 进行相应的逻辑判断,并且传递数据到view
     * @param action
     */
    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case MessageAction.MESSAGE_NEW_ACTION :
                message.setMessage((String) action.getData());
                break;
            default : break;
        }
        //发送事件到view层
        emitStoreChange();
    }

    @Override
    protected StoreChangeEvent changeEvent() {
        return new StoreChangeEvent();
    }
}
