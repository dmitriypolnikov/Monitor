package com.monitorfree.BackgroundService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.monitorfree.MyApp;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.AddMonitor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Dmitriy on 2/26/2018.
 */

public class DemoService extends JobService {

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        Log.d("DemoService", "true");

        MyApp.component().inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = job.getExtras();
        assert extras != null;

        String monitorID = extras.getString("monitorID");
        String address = extras.getString("address");
        String keywords = extras.getString("keywords");
        String port = extras.getString("port");
        String type = extras.getString("type");
        String active = extras.getString("active");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mobileDateTime = df1.format(c.getTime());


        if (type.equals("1")) {    //Http monitor

            if (active.equals("1"))
            {
                boolean isGet = isHttpMonitor(address);

                if (isGet) {
                    Log.d("Http monitor", "true");
                    userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                } else {
                    Log.d("Http monitor", "false");
                    userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                }
            }
            else {
                Log.d("Http monitor", "No still start");
            }

        } else if (type.equals("2")) {     //Ping monitor

            if (active.equals("1")) {

                boolean isGet = isPingMonitor(address);
                if (isGet) {
                    Log.d("Ping monitor", "true");
                    userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                } else {
                    Log.d("Ping monitor", "false");
                    userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                }
            }
            else {
                Log.d("Ping monitor", "No still start");
            }

        } else if (type.equals("3")) {     //Keyword monitor

            if (active.equals("1")) {

                boolean isGet = isHttpMonitor(address);
                if (isGet) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(HttpUrl.parse(address))
                            .addConverterFactory(DemoService.PageAdapter.FACTORY)
                            .build();


                    PageService requestAddress = retrofit.create(PageService.class);
                    Call<Page> pageCall = requestAddress.get(HttpUrl.parse(address));
                    pageCall.enqueue(new Callback<Page>() {
                        @Override
                        public void onResponse(Call<Page> call, Response<Page> response) {
                            Log.i("ADASDASDASD", response.body().content);

                            boolean isGet = response.body().content.toLowerCase().contains(keywords.toLowerCase());
                            if (isGet) {
                                Log.d("Keyword monitor", "true");
                                userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                            } else {
                                Log.d("Keyword monitor", "false");
                                userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                            }
                        }

                        @Override
                        public void onFailure(Call<Page> call, Throwable t) {
                            Log.d("connection fail", "false");
                        }
                    });
                }
            }
            else {
                Log.d("Keyword monitor", "No still start");
            }

        } else if (type.equals("4")) {     //Port monitor
            if (active.equals("1")) {

                boolean isGet = isPortMonitor(address, port);
                if (isGet) {
                    Log.d("Port monitor", "true");
                    userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                } else {
                    Log.d("Port monitor", "false");
                    userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                }
            }
            else {
                Log.d("Port monitor", "No still start");
            }
        }

        return false; // No more work to do
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false; // No more work to do
    }

    public boolean isPingMonitor(String ping) {

        if (ping.toLowerCase().contains("http")){
            ping = ping.replace("http://", "");
        } else if ( ping.toLowerCase().contains("https")) {
            ping = ping.replace("https://", "");
        }

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + ping);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isHttpMonitor(String monitorAddress) {

        ConnectivityManager connMan = (ConnectivityManager) myApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(monitorAddress);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public boolean isPortMonitor(String monitorAddress, String port) {

        String address = monitorAddress.substring(0, monitorAddress.length() - 1);
        address = address + ":" + port + "/";

        ConnectivityManager connMan = (ConnectivityManager) myApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(address);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static class Page {
        public String content;

        Page(String content) {
            this.content = content;
        }
    }

    public static final class PageAdapter implements Converter<ResponseBody, Page> {
        public static final Converter.Factory FACTORY = new Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, java.lang.annotation.Annotation[] annotations, Retrofit retrofit) {
                if (type == DemoService.Page.class) return new DemoService.PageAdapter();
                return null;
            }
        };

        @Override
        public DemoService.Page convert(ResponseBody responseBody) throws IOException {
            Document document = Jsoup.parse(responseBody.string());
            Element value = document.body();
            String content = value.html();
            return new DemoService.Page(content);
        }
    }

    public interface PageService {
        @GET
        Call<Page> get(@Url HttpUrl url);
    }
}
