package com.monitorfree.BackgroundService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monitorfree.Fragment.Home;
import com.monitorfree.MyApp;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.AddMonitor;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class BService extends Service {

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;

    ArrayList<AddMonitor> arrMonitor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyApp.component().inject(this);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        Gson gson = new Gson();

        List<AddMonitor> callLog = new ArrayList<AddMonitor>();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String getJson = mPrefs.getString("monitorList", "");
        if (getJson.isEmpty()) {
            arrMonitor = new ArrayList<AddMonitor>();
        } else {
            Type type = new TypeToken<List<AddMonitor>>() {
            }.getType();
            arrMonitor = gson.fromJson(getJson, type);
        }

//        arrMonitor = myApp.globalBGMonitorList;

        initializeTimerTask();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");

        Intent broadcastIntent = new Intent("com.monitorfree.BackgroundService.RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.scheduleAtFixedRate(timerTask, 1000, 60 * 1000);
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
//        timerTask = new TimerTask() {
//            public void run() {

                myApp.timerCount++;

                Log.d("countDB", "s " + arrMonitor.size());

                Calendar c = Calendar.getInstance();

                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String mobileDateTime = df1.format(c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                for (int index = 0; index < arrMonitor.size(); index++) {

                    String monitorDate = arrMonitor.get(index).getStartDate().split(" ")[0];
                    Date strDate = null;
                    try {
                        strDate = df.parse(monitorDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int interval_time = Integer.valueOf(arrMonitor.get(index).getInterval());

                    if (arrMonitor.get(index).getType().equals("1")) {    //Http monitor

                        if ((System.currentTimeMillis() > strDate.getTime()) && (myApp.timerCount % interval_time == 0) && (arrMonitor.get(index).getActive().equals("1")))
                        {
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());
                            boolean isGet = isHttpMonitor(arrMonitor.get(index).getAddress());

                            if (isGet) {
                                Log.d("Http monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1", mobileDateTime);
                            } else {
                                Log.d("Http monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2", mobileDateTime);
                            }
                        }
                        else {
                            Log.d("Http monitor", "No still start");
                        }

                    } else if (arrMonitor.get(index).getType().equals("2")) {     //Ping monitor

                        if ((System.currentTimeMillis() > strDate.getTime()) && (myApp.timerCount % interval_time == 0) && (arrMonitor.get(index).getActive().equals("1"))) {

                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

                            boolean isGet = isPingMonitor(arrMonitor.get(index).getAddress());
                            if (isGet) {
                                Log.d("Ping monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1", mobileDateTime);
                            } else {
                                Log.d("Ping monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2", mobileDateTime);
                            }
                        }
                        else {
                            Log.d("Ping monitor", "No still start");
                        }

                    } else if (arrMonitor.get(index).getType().equals("3")) {     //Keyword monitor

                        if ((System.currentTimeMillis() > strDate.getTime()) && (myApp.timerCount % interval_time == 0) && (arrMonitor.get(index).getActive().equals("1"))) {

                            String search_keyword = arrMonitor.get(index).getKeywords();
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

                            boolean isGet = isHttpMonitor(arrMonitor.get(index).getAddress());
                            if (isGet) {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUrl.parse(arrMonitor.get(index).getAddress()))
                                        .addConverterFactory(PageAdapter.FACTORY)
                                        .build();


                                PageService requestAddress = retrofit.create(PageService.class);
                                Call<Page> pageCall = requestAddress.get(HttpUrl.parse(arrMonitor.get(index).getAddress()));
                                pageCall.enqueue(new Callback<Page>() {
                                    @Override
                                    public void onResponse(Call<Page> call, Response<Page> response) {
                                        Log.i("ADASDASDASD", response.body().content);

                                        boolean isGet = response.body().content.toLowerCase().contains(search_keyword.toLowerCase());
                                        if (isGet) {
                                            Log.d("Keyword monitor", "true");
                                            userRequests.funSendStatus(strMonitor_id, myApp, "1", mobileDateTime);
                                        } else {
                                            Log.d("Keyword monitor", "false");
                                            userRequests.funSendStatus(strMonitor_id, myApp, "2", mobileDateTime);
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

                    } else if (arrMonitor.get(index).getType().equals("4")) {     //Port monitor
                        if ((System.currentTimeMillis() > strDate.getTime()) && (myApp.timerCount % interval_time == 0) && (arrMonitor.get(index).getActive().equals("1"))) {

                            String port = String.valueOf(arrMonitor.get(index).getPort());
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

                            boolean isGet = isPortMonitor(arrMonitor.get(index).getAddress(), port);
                            if (isGet) {
                                Log.d("Port monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1", mobileDateTime);
                            } else {
                                Log.d("Port monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2", mobileDateTime);
                            }
                        }
                        else {
                            Log.d("Port monitor", "No still start");
                        }
                    }
                }
//            }
//        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
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
        public static final Converter.Factory FACTORY = new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, java.lang.annotation.Annotation[] annotations, Retrofit retrofit) {
                if (type == BService.Page.class) return new BService.PageAdapter();
                return null;
            }
        };

        @Override
        public BService.Page convert(ResponseBody responseBody) throws IOException {
            Document document = Jsoup.parse(responseBody.string());
            Element value = document.body();
            String content = value.html();
            return new BService.Page(content);
        }
    }

    public interface PageService {
        @GET
        Call<Page> get(@Url HttpUrl url);
    }


}
