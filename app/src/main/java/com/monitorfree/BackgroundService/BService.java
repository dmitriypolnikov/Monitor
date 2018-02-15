package com.monitorfree.BackgroundService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyApp.component().inject(this);


        Toast.makeText(this, "start sticky", Toast.LENGTH_SHORT).show();
        fun();

        return START_STICKY;
    }

    void fun() {

        Timer t = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                myApp.timerCount++;

                Log.d("countDB", "s " + myApp.globalMonitorList.size());

                ArrayList<AddMonitor> arrMonitor = myApp.globalMonitorList;
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());

                for (int index = 0; index < arrMonitor.size(); index++) {

                    String monitorDate = arrMonitor.get(index).getStartDate().split(" ")[0];
                    int interval_time = Integer.valueOf(arrMonitor.get(index).getInterval());

                    if (arrMonitor.get(index).getType().equals("1")) {    //Http monitor

                        if ((formattedDate.equals(monitorDate)) && (myApp.timerCount % interval_time == 0))
                        {
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());
                            boolean isGet = isHttpMonitor(arrMonitor.get(index).getAddress());
                            if (isGet) {
                                Log.d("Http monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1");
                            } else {
                                Log.d("Http monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2");
                            }
                        }
                        else {
                            Log.d("Http monitor", "No still start");
                        }

                    } else if (arrMonitor.get(index).getType().equals("2")) {     //Ping monitor

                        if ((formattedDate.equals(monitorDate)) && (myApp.timerCount % interval_time == 0)) {

                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

                            boolean isGet = isPingMonitor(arrMonitor.get(index).getAddress());
                            if (isGet) {
                                Log.d("Ping monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1");
                            } else {
                                Log.d("Ping monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2");
                            }
                        }
                        else {
                            Log.d("Ping monitor", "No still start");
                        }

                    } else if (arrMonitor.get(index).getType().equals("3")) {     //Keyword monitor

                        if ((formattedDate.equals(monitorDate)) && (myApp.timerCount % interval_time == 0)) {

                            String search_keyword = arrMonitor.get(index).getKeywords();
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

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
                                        userRequests.funSendStatus(strMonitor_id, myApp, "1");
                                    } else {
                                        Log.d("Keyword monitor", "false");
                                        userRequests.funSendStatus(strMonitor_id, myApp, "2");
                                    }
                                }
                                @Override
                                public void onFailure(Call<Page> call, Throwable t) {

                                }
                            });
                        }
                        else {
                            Log.d("Ping monitor", "No still start");
                        }

                    } else if (arrMonitor.get(index).getType().equals("4")) {     //Port monitor
                        if ((formattedDate.equals(monitorDate)) && (myApp.timerCount % interval_time == 0)) {

                            String port = String.valueOf(arrMonitor.get(index).getPort());
                            String strMonitor_id = String.valueOf(arrMonitor.get(index).getId());

                            boolean isGet = isPortMonitor(arrMonitor.get(index).getAddress(), port);
                            if (isGet) {
                                Log.d("Port monitor", "true");
                                userRequests.funSendStatus(strMonitor_id, myApp, "1");
                            } else {
                                Log.d("Port monitor", "false");
                                userRequests.funSendStatus(strMonitor_id, myApp, "2");
                            }
                        }
                    }
                }
            }
        };

//        if (myApp.globalMonitorList.size() > 0) {
            t.scheduleAtFixedRate(task, 0, 1000);
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

        String protocal = "", host = "";
        if (monitorAddress.toLowerCase().contains("http")){
            protocal = monitorAddress.substring(0, 4);
            host = monitorAddress.replace("http://", "");
        } else if ( monitorAddress.toLowerCase().contains("https")) {
            protocal = monitorAddress.substring(0, 5);
            host = monitorAddress.replace("https://", "");
        }

        ConnectivityManager connMan = (ConnectivityManager) myApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(protocal, host, port);
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

    static class Page {
        String content;

        Page(String content) {
            this.content = content;
        }
    }

    static final class PageAdapter implements Converter<ResponseBody, Page> {
        static final Converter.Factory FACTORY = new Converter.Factory() {
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

    interface PageService {
        @GET
        Call<Page> get(@Url HttpUrl url);
    }


}
