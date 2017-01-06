package com.ckev.fluxtest.stores;

import com.ckev.fluxtest.actions.Action;

import org.greenrobot.eventbus.EventBus;

/**
 * 处理action的基类,通常可根据不同的业务逻辑实现{@link #onAction(Action)}
 * 通过EventBus传递数据
 * Created by ckerv on 16/12/4.
 */
public abstract class Store {

    private static final EventBus mBus = EventBus.getDefault();

    protected Store() {

    }

    public void register(Object view) {
        this.mBus.register(view);
    }

    public void unRegister(Object view) {
        this.mBus.unregister(view);
    }

    /**
     * post事件到view层,进行UI的后续处理
     */
    protected void emitStoreChange() {
        this.mBus.post(changeEvent());
    }



    /**
     * 处理Action的逻辑,子类必须实现此方法
     * @param action
     */
    public abstract void onAction(Action action);

    /**
     * post事件
     * @return
     */
    protected abstract StoreChangeEvent changeEvent();

    public class StoreChangeEvent{}
}
