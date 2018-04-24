package com.freewebsitemonitor.RequestModel;

/**
 * Created by jaspreet on 11/22/17.
 */

public class Status {

    String status;
    int error;
    String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
