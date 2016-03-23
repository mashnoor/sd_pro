package com.smartdsepro.android;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;

public class WeeklyReport extends AppCompatActivity {

    WebView dse_weekly_report_webView;
    ButtonController buttonController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_report);
        buttonController = new ButtonController(this);
        dse_weekly_report_webView = (WebView) findViewById(R.id.dse_weekly_report);

        dse_weekly_report_webView.getSettings();
        dse_weekly_report_webView.setBackgroundColor(Color.BLACK);
        new grab_expert_analysis().execute();


    }

    class grab_expert_analysis extends AsyncTask<Void, Void, String>
    {
        ProgressDialog progressDialog = ProgressDialog.show(WeeklyReport.this, "",
                "Retrieving Data...", true);
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            if (result!=null) {
                dse_weekly_report_webView.loadDataWithBaseURL(null, result,  "text/html", "utf-8", null);
            }
            else
            {
                Toast.makeText(WeeklyReport.this, "Problem connecting to server!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            SrcGrabber grabber = new SrcGrabber();
            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    String expert_analysis_data = grabber.grabSource(Constants.WEEKLY_REPORT);
                    return expert_analysis_data;
                }
                else {
                    return null;
                }

            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }


        }

    }

}
