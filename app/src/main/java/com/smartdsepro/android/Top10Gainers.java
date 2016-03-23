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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Top10Gainers extends Activity {
	/*
	 * As we are parsing json array from the server for showing list view, so we
	 * have to use JSONArray class and JSONObject class for retriving
	 * information from file For that reason, we are Declaring jsonarray and
	 * jsonobject class instance
	 */
    // Declare Variables

    JSONObject dse_json_object = null;
    JSONArray dse_jsonArray = null;
    ArrayList<DSE_Company_data> dse_top_gainers_losers_datas;

    ListView dse_list_view;
    View showtoptengainers, showtoptenlosers, downpaneltoptengainer,
            downpaneltoptenlosers;
    ButtonController buttonController;
    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    public static final String file_name_gainers = "dse_top10gainers";
    public static final String file_name_losers = "dse_top10losers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_top10_gainers);

        new read_and_write_gainers_data().execute("");
        buttonController = new ButtonController(Top10Gainers.this);
        dse_top_gainers_losers_datas = new ArrayList<DSE_Company_data>();
        downpaneltoptengainer = findViewById(R.id.topgainers_down_panel);
        downpaneltoptenlosers = findViewById(R.id.toplosers_down_panel);

        showtoptengainers = (View) findViewById(R.id.btn_toptengainers);
        showtoptenlosers = (View) findViewById(R.id.btn_toptenlosers);
        showtoptenlosers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dse_top_gainers_losers_datas.clear();
                new read_and_write_losers_data().execute("");
                downpaneltoptenlosers.setBackgroundColor(Color
                        .parseColor("#ff33b5e5"));
                downpaneltoptengainer.setBackgroundColor(Color.BLACK);

            }
        });

        showtoptengainers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                downpaneltoptenlosers.setBackgroundColor(Color.BLACK);
                downpaneltoptengainer.setBackgroundColor(Color
                        .parseColor("#ff33b5e5"));
                dse_top_gainers_losers_datas.clear();
                new read_and_write_gainers_data().execute("");
            }
        });

    }

    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_toptengainerslosers_listview);
        adapter = new DSE_Data_adapter();
        dse_list_view.setAdapter(adapter);
        registeronclicklistener();

    }

    private void registeronclicklistener() {
        dse_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long arg3) {
                        DSE_Company_data clicked_Company_data = dse_top_gainers_losers_datas
                                .get(position);
                        Intent detail_info_intent = new Intent(
                                Top10Gainers.this, ItemInfo.class);
                        detail_info_intent.putExtra("TradingCode",
                                clicked_Company_data.getCompany_Name());
                        startActivity(detail_info_intent);

                    }
                });
    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.topgainerloser_listlike,
                    dse_top_gainers_losers_datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.topgainerloser_listlike, parent, false);

            }

            DSE_Company_data current = getItem(position);
            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_topgl_itemname);
            TextView tvcloseprice = (TextView) workingView
                    .findViewById(R.id.dse_topgl_closeprice);
            TextView tvhigh = (TextView) workingView
                    .findViewById(R.id.dse_topgl_high);
            TextView tvlow = (TextView) workingView
                    .findViewById(R.id.dse_topgl_low);
            TextView tvyesterdaycloseprice = (TextView) workingView
                    .findViewById(R.id.dse_topgl_yesterdaycloseprice);
            TextView tvchange = (TextView) workingView
                    .findViewById(R.id.dse_topgl_change);

            tvcompany.setText(current.getCompany_Name());
            tvcloseprice.setText(current.getCloseprice());
            tvhigh.setText(current.getHigh());
            tvlow.setText(current.getLow());
            tvyesterdaycloseprice.setText(current.getYesterdaycloseprice());
            tvchange.setText(current.getChange_percentage());
            return workingView;
        }

    }


    class read_and_write_gainers_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(Top10Gainers.this,
                "", "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (!has_active_net) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to server. Check your internet.",
                        Toast.LENGTH_LONG).show();
            }
            if (result != null) {
                showInListView();

            }
        }

        @Override
        protected String doInBackground(String... params) {
            SrcGrabber grabber = new SrcGrabber();
            String string = null;

            try {

                string = grabber
                        .grabSource("http://104.131.22.246/dev/smartdsefiles/top10gainersgrab.txt");
                // System.out.println(string);
                DevTools.write_file(Top10Gainers.this, file_name_gainers,
                        string);
                has_active_net = true;

            } catch (IOException | URISyntaxException e1) {
                if (DevTools
                        .fileExistance(Top10Gainers.this, file_name_gainers)) {
                    string = DevTools.read_file(Top10Gainers.this,
                            file_name_gainers);
                    has_active_net = false;

                } else {
                    return null;
                }

            }

            try {
                dse_jsonArray = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int jsonarraysize = dse_jsonArray.length();

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    dse_json_object = dse_jsonArray.getJSONObject(i);
                    String companyString = dse_json_object
                            .getString("itemname");
                    String closeprice = dse_json_object.getString("CLOSEP");
                    String high = dse_json_object.getString("high");
                    String low = dse_json_object.getString("low");
                    String yesterdaycloseprice = dse_json_object
                            .getString("ycp");
                    String change = dse_json_object.getString("change");
                    DSE_Company_data currentCompany_data = new DSE_Company_data(
                            companyString, closeprice, high, low,
                            yesterdaycloseprice, change);
                    dse_top_gainers_losers_datas.add(currentCompany_data);

                }
                System.out.println(dse_top_gainers_losers_datas.size());

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }
    }

    class read_and_write_losers_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(Top10Gainers.this,
                "", "Retriving Data...", true);

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            if (!has_active_net) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to server. Check your internet.",
                        Toast.LENGTH_LONG).show();
            }
            if (result != null) {
                showInListView();
            } else {


            }

        }



        @Override
        protected String doInBackground(String... params) {
            SrcGrabber grabber = new SrcGrabber();
            String string = null;

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = grabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/top10losersgrab.txt");
                    // System.out.println(string);
                    DevTools.write_file(Top10Gainers.this, file_name_losers, string);
                    has_active_net = true;
                }
                else if (DevTools.fileExistance(Top10Gainers.this, file_name_losers)) {
                    string = DevTools.read_file(Top10Gainers.this,
                            file_name_losers);
                    has_active_net = false;

                } else {
                    return null;
                }



            } catch (IOException | URISyntaxException e1) {


            }

            try {
                dse_jsonArray = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int jsonarraysize = dse_jsonArray.length();

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    dse_json_object = dse_jsonArray.getJSONObject(i);
                    String companyString = dse_json_object
                            .getString("itemname");
                    String closeprice = dse_json_object.getString("CLOSEP");
                    String high = dse_json_object.getString("high");
                    String low = dse_json_object.getString("low");
                    String yesterdaycloseprice = dse_json_object
                            .getString("ycp");
                    String change = dse_json_object.getString("change");
                    DSE_Company_data currentCompany_data = new DSE_Company_data(
                            companyString, closeprice, high, low,
                            yesterdaycloseprice, change);
                    dse_top_gainers_losers_datas.add(currentCompany_data);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }
    }

}
