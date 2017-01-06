package com.ckev.fluxtest.model;

/**
 * 此类可看做Model层,通常对数据进行封装、保存
 * 具体可对应到不同的实体类
 * Created by ckerv on 16/12/5.
 */
public class Message {

    private Object object;

    public void setMessage(Object object) {
        this.object = object;
    }

    public Object getMessage() {
        return object;
    }
}
