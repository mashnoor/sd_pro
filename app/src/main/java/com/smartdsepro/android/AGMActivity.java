package com.smartdsepro.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class AGMActivity extends AppCompatActivity {





    JSONObject agm_object = null;
    JSONArray agm_array = null;
    ArrayList<AGM> agms;
    ListView agm_list_view;
    ButtonController buttonController;
    ArrayAdapter<AGM> adapter;
    View ipo_activity_tab, news_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agm);
        buttonController = new ButtonController(this);
        agms = new ArrayList<AGM>();

        ipo_activity_tab = findViewById(R.id.btn_ipo);
        ipo_activity_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent ipo_activity_intent = new Intent(AGMActivity.this,
                        IPOActivity.class);
                startActivity(ipo_activity_intent);

            }
        });

        news_tab = findViewById(R.id.btn_news);
        news_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent newsintent = new Intent(AGMActivity.this,
                        NewsActivity.class);
                startActivity(newsintent);

            }
        });


        new read_and_write_agm().execute("");


    }




    private void showInListView() {
        agm_list_view = (ListView) findViewById(R.id.dse_agm_list);
        adapter = new DSE_Data_adapter();
        agm_list_view.setAdapter(adapter);

    }


    public class DSE_Data_adapter extends ArrayAdapter<AGM> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.dse_agm_like,
                    agms);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.dse_agm_like, parent, false);

            }

            AGM current = getItem(position);
            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_agm_companyname);
            TextView yearEnd = (TextView) workingView
                    .findViewById(R.id.dse_agm_yearend);
            TextView dividend = (TextView) workingView
                    .findViewById(R.id.dse_agm_dividend);
            TextView dateOfAgm = (TextView) workingView
                    .findViewById(R.id.dse_agm_dateagm);
            TextView recordDate = (TextView) workingView
                    .findViewById(R.id.dse_agm_recorddate);
            TextView venue = (TextView) workingView
                    .findViewById(R.id.dse_agm_venue);
            TextView time = (TextView) workingView
                    .findViewById(R.id.dse_agm_time);


            tvcompany.setText(current.getCompanyName());
            yearEnd.setText("Year End : " + current.getYearEnd());
            dividend.setText("Dividend in (%) : " + current.getDividend());
            dateOfAgm.setText("Date of AGM : " + current.getDateOfAgm());
            recordDate.setText("Record Date : " + current.getRecordDate());
            venue.setText("Venue : " + current.getVenue());
            time.setText("Time : " + current.getTime());
            return workingView;
        }

    }




    class read_and_write_agm extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(AGMActivity.this,
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
                Toast.makeText(AGMActivity.this, "Can't connect to the server! Check your internet.",
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
                            .grabSource(Constants.AGM_URL);
                } else {
                    return null;
                }

            } catch (IOException | URISyntaxException e1) {

            }

            try {
                agm_array = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                return null;
            }

            int jsonarraysize = agm_array.length();

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    agm_object = agm_array.getJSONObject(i);
                    String company = agm_object
                            .getString("company_name");
                    String yearEnd = agm_object.getString("year_end");
                    String dividend = agm_object.getString("dividend");
                    String dateOfAgm = agm_object.getString("date_of_agm");
                    String recordDate = agm_object.getString("record_date");
                    String venue = agm_object.getString("venue");
                    String time = agm_object.getString("time");
                    AGM current_agm_info = new AGM(company, yearEnd, dividend, dateOfAgm, recordDate, venue, time);


                    agms.add(current_agm_info);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }

}
