package com.freewebsitemonitor.DI;

import android.app.Application;

import com.freewebsitemonitor.RequestModel.UserRequests;
import com.freewebsitemonitor.UserModel.AddMonitor;
import com.freewebsitemonitor.UserModel.User;
import com.freewebsitemonitor.MyApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jaspreet on 11/9/17.
 */


@Module
public class DIModule {


    @Provides
    @Singleton
    MyApp getMyApp() {
        return new MyApp();
    }

    MyApp app;

    @Provides
    @Singleton
    public User getUser() {
        return new User();
    }

    @Singleton
    public DIModule(MyApp application) {
        app = application;
    }

    @Provides
    @Singleton
    protected Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    protected UserRequests userRequests() {
        return new UserRequests();
    }

    @Provides
    @Singleton
    protected AddMonitor addMonitor() {
        return new AddMonitor();
    }


}
