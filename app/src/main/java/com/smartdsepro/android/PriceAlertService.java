package com.smartdsepro.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class PriceAlertService extends Service {
    static Context activity;
    static Random random;
    public PriceAlertService() {

        activity =PriceAlertService.this;
        random = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Log.i(Constants.DEBUG_TAG, "Starting.....");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try
                    {
                        check_everything();


                        //Log.i(Constants.DEBUG_TAG, "Running");
                        Thread.sleep(10000);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        try
                        {
                            Thread.sleep(100000);
                        }
                        catch (Exception ef)
                        {
                            ef.printStackTrace();
                        }

                    }

                }


            }
        };
        Thread thread = new Thread(r);
        thread.start();






        return START_STICKY;
    }
    private void check_everything() throws Exception{
        String ltp_json = SrcGrabber.grabSource(Constants.LPT_VALUES);
        DevTools.write_file(activity, Constants.LTP_FILE, ltp_json);
        JSONArray ltp_array = new JSONArray(ltp_json);
        int ltp_array_size = ltp_array.length();
        JSONObject ltp_object;
        for (int i = 0; i<ltp_array_size; i++)
        {
            ltp_object = ltp_array.getJSONObject(i);
            String curr_item = ltp_object.getString("company");
            if(PriceAlertHelper.ifItemExists(activity, curr_item))
            {
                String ltp = ltp_object.getString("lastTrade");
                Log.d(Constants.DEBUG_TAG, ltp);
                doTheProcess(activity, curr_item, ltp);
            }
        }
    }

    private void doTheProcess(Context activity, String curr_item, String ltp_value) throws Exception{

        String[] high_low = PriceAlertHelper.getHighLowValue(activity, curr_item);
        Double high = Double.parseDouble(high_low[0]);
       //Log.d(Constants.DEBUG_TAG, "High - " + high);
        Double low = Double.parseDouble(high_low[1]);
        //Log.d(Constants.DEBUG_TAG, "Low  - " + low);
        Double ltp = Double.parseDouble(ltp_value);
        Log.d(Constants.DEBUG_TAG, "LTP - " + ltp);
        if(!PriceAlertHelper.getLtp(activity, curr_item).equals(ltp_value))
        {
            Log.d(Constants.DEBUG_TAG, "Entering not logic");
            if(ltp>high)
            {
                //Send High Notification
                showNotification(curr_item,"Crossed high value limit!", random.nextInt());

                Log.d(Constants.DEBUG_TAG, "HIGH");

                //Update New ltp
                PriceAlertHelper.updateLtp(activity, curr_item, ltp_value);


            }
            else if (ltp<low)
            {
                //Send Low Notification
                showNotification(curr_item, "Crossed low value limit!", random.nextInt());
                Log.d(Constants.DEBUG_TAG, "LOW");


                //Update new ltp
                PriceAlertHelper.updateLtp(activity, curr_item, ltp_value);
            }
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    //Show Notification
    private void showNotification(String title, String message, int id) {
        Intent intent = new Intent(this, PriceAlert.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smartdselogotransparent)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }



}


