package com.monitorfree.RequestModel;

import com.monitorfree.UserModel.AddMonitor;
import com.monitorfree.UserModel.MonitorStatus;

import java.util.List;

/**
 * Created by jaspreet on 11/22/17.
 */

public class RootMonitorStatus {

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    Status status;

    public MonitorStatus getData() {
        return data;
    }

    public void setData(MonitorStatus data) {
        this.data = data;
    }

    MonitorStatus data;
}
