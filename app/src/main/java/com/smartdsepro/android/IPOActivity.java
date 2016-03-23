package com.smartdsepro.android;

import java.io.IOException;
import java.net.URISyntaxException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;


public class IPOActivity extends Activity {

    View news_tab;
    WebView ipo_text;

    ButtonController buttonController;
    View agm_activity_tab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_ipo);
        buttonController = new ButtonController(IPOActivity.this);


        agm_activity_tab = findViewById(R.id.btn_agm);
        ipo_text = (WebView) findViewById(R.id.dse_ipo);
        ipo_text.getSettings();
        ipo_text.setBackgroundColor(Color.BLACK);


        agm_activity_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent agm_activity_intent = new Intent(IPOActivity.this,
                        AGMActivity.class);
                startActivity(agm_activity_intent);
            }
        });

        news_tab = findViewById(R.id.btn_news);
        news_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent newsintent = new Intent(IPOActivity.this,
                        NewsActivity.class);
                startActivity(newsintent);

            }
        });
        new grab_ipo().execute();

    }



    class grab_ipo extends AsyncTask<Void, Void, String>
    {
        ProgressDialog progressDialog = ProgressDialog.show(IPOActivity.this, "",
                "Retrieving Data...", true);
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            if (result!=null) {
                ipo_text.loadDataWithBaseURL(null, result,  "text/html", "utf-8", null);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            SrcGrabber grabber = new SrcGrabber();
            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    String ipo_data = grabber.grabSource("http://104.131.22.246/dev/smartdsefiles/ipo.txt");
                    return ipo_data;
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