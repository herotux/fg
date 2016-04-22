package org.telegram.felegram.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.felegram.utils.Utils;
import org.telegram.messenger.R;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.JsonArrayRequest;
import org.telegram.messenger.volley.toolbox.Volley;
import org.telegram.ui.LaunchActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by reza on 4/19/2016 AD.
 */
public class NotificationService extends IntentService {

    private Context context;
    private String url = "http://api.felegram.ir/v1/android/notifications";
    private String notified_prefix = "notified_";

    private int feleVersion;
    private int sdkVersion;

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service", "onHandle");
        context = this;
        try{
            if (isOnline(context)){
                Log.d("service", "is online");
                checkUpdate();
            }else{
                Log.e("service", "service stopped");
                stopMe();
            }
        }catch (Exception e){
            stopMe();
            Log.e("service", e.toString());
        }
    }

    private JSONObject paramsQuery(){
        JSONObject jsonReq = new JSONObject();
        try {
            jsonReq.put("version", feleVersion);
            jsonReq.put("sdk", sdkVersion);
            jsonReq.put("IMEI", getIMEI(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonReq;

    }

    private String queryParams(){
        return "?version=" + feleVersion + "&sdk=" + sdkVersion + "&imei=" + getIMEI(context);
    }

    private int getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private String getIMEI(Context context){
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }


    private void checkUpdate(){
        sdkVersion = Build.VERSION.SDK_INT;
        feleVersion = getVersion(context);

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);

        String urlWithParams = url + queryParams();

        JsonArrayRequest jr = new JsonArrayRequest(urlWithParams,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++){

                                JSONObject json = response.getJSONObject(i);

                                FelegramNotification notif = new FelegramNotification();

                                notif.setId(json.getInt("id"));
                                notif.setTitle(!json.isNull("title") ? json.getString("title") : null);
                                notif.setDescription(!json.isNull("description") ? json.getString("description") : null);
                                notif.setSmallIcon(!json.isNull("small_icon") ? json.getString("small_icon") : null);
                                notif.setLargeIcon(!json.isNull("large_icon") ? json.getString("large_icon") : null);
                                notif.setPicture(!json.isNull("picture") ? json.getString("picture") : null);
                                notif.setIntentData(!json.isNull("intent_data") ? json.getString("intent_data") : null);
                                notif.setIntentAction(!json.isNull("intent_action") ? json.getString("intent_action") : null);
                                notif.setPackageName(!json.isNull("package_name") ? json.getString("package_name") : null);
                                notif.setTicker(!json.isNull("ticker") ? json.getString("ticker") : null);
                                notif.setIsUpdate(!json.isNull("is_update") ? json.getBoolean("is_update") : null);
                                notif.setUpdateVersion(!json.isNull("update_version") ? json.getInt("update_version") : null);
                                notif.setUpdateChanges(!json.isNull("update_changes") ? json.getString("update_changes") : null);

                                showNotification(notif);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Json parse error", error.toString());
                    }
                })
                {
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("version",feleVersion + "");
                        params.put("sdk", sdkVersion + "");
                        params.put("imei",getIMEI(context));
                        return params;
                    }
                };

        mRequestQueue.add(jr);
    }


    private void showNotification(final FelegramNotification notif){
        if(!getNotified(notif.getId())){

            if(notif.getIsUpdate() && notif.getUpdateVersion() == feleVersion){
                stopMe();
                return;
            }

            if(Build.VERSION.SDK_INT < 16){

                new Thread() {
                    @Override
                    public void run() {
                        showSimple(notif);
                    }
                }.start();

            }else{
                new Thread() {
                    @Override
                    public void run() {
                        showPictureNotification(notif);
                    }
                }.start();
            }

            setNotified(notif.getId());

        }

    }

    private void stopMe(){
        try{
            stopSelf();
        }catch (Exception e){

        }
    }

    private void showSimple(FelegramNotification notif){
        Intent intent = null;

        if(notif.getPackageName() == null) {
            intent = new Intent(context, LaunchActivity.class);
        }else {
            if(notif.getIntentAction().equals("View")){
                intent = new Intent(intent.ACTION_VIEW);
            }else if(notif.getIntentAction().equals("Edit")){
                intent = new Intent(intent.ACTION_EDIT);
            }else{
                intent = new Intent(intent.ACTION_MAIN);
            }

            if (notif.getIntentData() != null){
                intent.setData(Uri.parse(notif.getIntentData()));
            }
            if(Utils.isPackageInstalled(context, notif.getPackageName())){
                intent.setPackage(notif.getPackageName());
            }
        }



        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        if(notif.getTicker() != null)
            b.setTicker(notif.getTicker());

        if(notif.getTitle() != null)
            b.setContentTitle(notif.getTitle());

        if(notif.getDescription() != null)
            b.setContentText(notif.getDescription());

        if(notif.getSmallIcon() != null) {
            if (notif.getSmallIcon().equals("ic_update"))
                b.setSmallIcon(R.drawable.ic_notif_update);
            else if (notif.getSmallIcon().equals("ic_favorite"))
                b.setSmallIcon(R.drawable.ic_notif_favorite);
            else if (notif.getSmallIcon().equals("ic_message"))
                b.setSmallIcon(R.drawable.ic_notif_message);
            else if (notif.getSmallIcon().equals("ic_settings"))
                b.setSmallIcon(R.drawable.ic_notif_settings);
            else if (notif.getSmallIcon().equals("ic_star"))
                b.setSmallIcon(R.drawable.ic_notif_star);
            else if (notif.getSmallIcon().equals("ic_thumb_up"))
                b.setSmallIcon(R.drawable.ic_notif_thumb_up);
            else if (notif.getSmallIcon().equals("ic_time"))
                b.setSmallIcon(R.drawable.ic_notif_time);
            else if (notif.getSmallIcon().equals("ic_telegram"))
                b.setSmallIcon(R.drawable.notification);
            else
                b.setSmallIcon(R.drawable.notification);
        }else{
            b.setSmallIcon(R.drawable.notification);
        }

        if(notif.getLargeIcon() != null)
            b.setLargeIcon(getBitmapFromURL(notif.getLargeIcon()));

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        b.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getRandom(1,20000), b.build());

        stopMe();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showPictureNotification(FelegramNotification notif) {
        Intent intent = null;

        if(notif.getPackageName() == null) {
            intent = new Intent(context, LaunchActivity.class);
        }else {
            if(notif.getIntentAction().equals("View")){
                intent = new Intent(intent.ACTION_VIEW);
            }else if(notif.getIntentAction().equals("Edit")){
                intent = new Intent(intent.ACTION_EDIT);
            }else{
                intent = new Intent(intent.ACTION_MAIN);
            }

            if (notif.getIntentData() != null){
                intent.setData(Uri.parse(notif.getIntentData()));
            }
            if(Utils.isPackageInstalled(context, notif.getPackageName())){
                intent.setPackage(notif.getPackageName());
            }
        }

        Notification.Builder b = new Notification.Builder(context);

        if(notif.getTicker() != null)
            b.setTicker(notif.getTicker());

        if(notif.getTitle() != null)
            b.setContentTitle(notif.getTitle());

        if(notif.getDescription() != null)
            b.setContentText(notif.getDescription());

        if(notif.getSmallIcon() != null) {
            if (notif.getSmallIcon().equals("ic_update"))
                b.setSmallIcon(R.drawable.ic_notif_update);
            else if (notif.getSmallIcon().equals("ic_favorite"))
                b.setSmallIcon(R.drawable.ic_notif_favorite);
            else if (notif.getSmallIcon().equals("ic_message"))
                b.setSmallIcon(R.drawable.ic_notif_message);
            else if (notif.getSmallIcon().equals("ic_settings"))
                b.setSmallIcon(R.drawable.ic_notif_settings);
            else if (notif.getSmallIcon().equals("ic_star"))
                b.setSmallIcon(R.drawable.ic_notif_star);
            else if (notif.getSmallIcon().equals("ic_thumb_up"))
                b.setSmallIcon(R.drawable.ic_notif_thumb_up);
            else if (notif.getSmallIcon().equals("ic_time"))
                b.setSmallIcon(R.drawable.ic_notif_time);
            else if (notif.getSmallIcon().equals("ic_telegram"))
                b.setSmallIcon(R.drawable.notification);
            else
                b.setSmallIcon(R.drawable.notification);
        }else{
            b.setSmallIcon(R.drawable.notification);
        }

        if(notif.getLargeIcon() != null)
            b.setLargeIcon(getBitmapFromURL(notif.getLargeIcon()));

        if(notif.getPicture() != null)
            b.setStyle(new Notification.BigPictureStyle()
                    .bigPicture(getBitmapFromURL(notif.getPicture())).setBigContentTitle(notif.getTitle()).setSummaryText(notif.getDescription()));

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(getRandom(1, 20000), b.build());

        stopMe();
    }

    public Bitmap getBitmapFromURL(String src) {

        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getNotified(int id){
        SharedPreferences preferences = context.getSharedPreferences("notifs", Activity.MODE_PRIVATE);
        return preferences.getBoolean(notified_prefix + id, false);
    }

    private void setNotified(int id){
        SharedPreferences preferences = context.getSharedPreferences("notifs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(notified_prefix + id, true).commit();
    }

    private int getRandom(int min, int max){
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }
}
