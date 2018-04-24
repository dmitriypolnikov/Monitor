package com.freewebsitemonitor.DI;

import android.provider.Settings;

import com.freewebsitemonitor.Activities.ForgotPassword;
import com.freewebsitemonitor.Activities.Login;
import com.freewebsitemonitor.Activities.Main2Activity;
import com.freewebsitemonitor.Activities.MonitorInfo;
import com.freewebsitemonitor.Activities.MyProfile;
import com.freewebsitemonitor.Activities.Verification;
import com.freewebsitemonitor.Fragment.AddMonitor;
import com.freewebsitemonitor.Fragment.FAQs;
import com.freewebsitemonitor.Fragment.Home;
import com.freewebsitemonitor.BackgroundService.DemoService;
import com.freewebsitemonitor.DI.DaggerDIComponants;
import com.freewebsitemonitor.MyApp;
import com.freewebsitemonitor.Splash;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jaspreet on 11/9/17.
 */

@Singleton
@Component(modules = {DIModule.class})
public interface DIComponants {


    void inject(DemoService demoService);

    static final class Initializer {
        private Initializer() {
        }

        public static DIComponants init(MyApp app) {
            return DaggerDIComponants.builder().dIModule(new DIModule(app))
                    .build();
        }
    }

    void inject(Login login);

    void inject(Home home);

//    void inject(SaveMonitor login);

    void inject(ForgotPassword login);

    void inject(Verification verification);

    void inject(Splash Splash);

    void inject(Main2Activity main2Activity);

//    void inject(SignUp login);

    void inject(MyProfile myProfile);

    void inject(FAQs myProfile);

    void inject(AddMonitor myProfile);

    void inject(MonitorInfo myProfile);

    void inject(Settings login);

}
