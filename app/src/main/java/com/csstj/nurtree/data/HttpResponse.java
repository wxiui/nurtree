package com.csstj.nurtree.data;

import com.csstj.nurtree.data.model.ExamInfo;

public class HttpResponse {

    //状态码
    private int code;
    //返回消息
    private String msg;
    //数据对象
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}