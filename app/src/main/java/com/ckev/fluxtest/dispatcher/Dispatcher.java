package com.ckev.fluxtest.dispatcher;

import com.ckev.fluxtest.actions.Action;
import com.ckev.fluxtest.stores.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个app通常只有一个Dispatcher类,内部进行对Store的管理
 * 此类的作用是将action分发到store,是连接action和store的中心枢纽
 * Created by ckerv on 16/12/4.
 */
public class Dispatcher {

    private static Dispatcher INSTANCE;

    private List<Store> mStores;

    private Dispatcher() {
        mStores = new ArrayList<>();
    }

    public static Dispatcher getInstance() {
        if(INSTANCE == null) {
            synchronized (Dispatcher.class) {
                if(INSTANCE == null) {
                    INSTANCE = new Dispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 注册store,即把store添加到list中
     * @param store
     */
    public void register(Store store) {
        this.mStores.add(store);
    }

    /**
     * 注销store,从list中移除
     * @param store
     */
    public void unRegister(Store store) {
        this.mStores.remove(store);
    }

    /**
     * 分发action
     * @param action
     */
    public void dispatch(Action action) {
        post(action);
    }

    /**
     * 向注册的store list依次分发acion
     * @param action
     */
    private void post(Action action) {
        for (Store store : mStores) {
            store.onAction(action);
        }
    }

}
