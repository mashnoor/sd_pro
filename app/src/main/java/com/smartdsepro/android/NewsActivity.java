/*This class is used for showing the list of items in the screen
 * (c) Nowfel Mashnoor. CEO, Mashnoor Lab
 * 
 */

package com.smartdsepro.android;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsActivity extends Activity {
	/*
	 * As we are parsing json array from the server for showing list view, so we
	 * have to use JSONArray class and JSONObject class for retriving
	 * information from file For that reason, we are Declaring jsonarray and
	 * jsonobject class instance
	 */
    // Declare Variables

    JSONObject dse_json_object = null;
    JSONArray dse_jsonArray = null;
    ArrayList<DSE_Company_data> dse_latest_news;

    ListView dse_list_view;
    ButtonController buttonController;
    View ipo_activity_tab, agm_activity_tab;
    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_news);

        dse_latest_news = new ArrayList<DSE_Company_data>();
        buttonController = new ButtonController(NewsActivity.this);

        agm_activity_tab = findViewById(R.id.btn_agm);
        agm_activity_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent agm_activity_intent = new Intent(NewsActivity.this,
                        AGMActivity.class);
                startActivity(agm_activity_intent);
            }
        });

        ipo_activity_tab = findViewById(R.id.btn_ipo);
        ipo_activity_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent ipo_activity_intent = new Intent(NewsActivity.this,
                        IPOActivity.class);
                startActivity(ipo_activity_intent);

            }
        });
        new read_and_write_news().execute("");

    }

    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_latest_news_list);
        adapter = new DSE_Data_adapter();
        dse_list_view.setAdapter(adapter);

    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.dse_news_like,
                    dse_latest_news);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.dse_news_like, parent, false);

            }

            DSE_Company_data current = getItem(position);
            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_news_tradingcode);
            TextView tvnewsdetail = (TextView) workingView
                    .findViewById(R.id.dse_news_detail);

            tvcompany.setText(current.getCompany_Name());
            tvnewsdetail.setText(Html.fromHtml(current.getNews()).toString());

            return workingView;
        }

    }



    class read_and_write_news extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(NewsActivity.this,
                "", "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            // if (!has_active_net) {
            // Toast.makeText(getApplicationContext(),
            // "No internet connection. Showing Last retrived data.",
            // Toast.LENGTH_LONG).show();
            // }
            if (result == null) {
                Toast.makeText(NewsActivity.this, "Can't connect to the server! Check your internet.",
                        Toast.LENGTH_LONG).show();

            } else {
                showInListView();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            SrcGrabber grabber = new SrcGrabber();
            String string = null;

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = grabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/newsgrab.txt");
                } else {
                    return null;
                }

            } catch (IOException | URISyntaxException e1) {

            }

            try {
                dse_jsonArray = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                return null;
            }

            int jsonarraysize = dse_jsonArray.length();

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    dse_json_object = dse_jsonArray.getJSONObject(i);
                    String companyString = dse_json_object
                            .getString("tradingcode");
                    String news = dse_json_object.getString("news");

                    DSE_Company_data currentCompany_data = new DSE_Company_data(
                            companyString, news);
                    dse_latest_news.add(currentCompany_data);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }

}
