package com.ckev.fluxtest.actions;

/**
 * 简单的POJO类型，只提供两个字段：type 和 data, 分别记录Action的类型和数据
 * Created by ckerv on 16/12/4.
 */
public class Action<T> {

    private String type;

    private T data;

    public Action(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
