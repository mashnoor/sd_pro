package com.smartdsepro.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class MarketDepth extends AppCompatActivity {

    //md = market depth
    JSONObject md_object = null;
    JSONArray  md_array = null;

    ArrayList<MarketDepthValue> md_buy_arraylist;
    ArrayList<MarketDepthValue> md_sell_arraylist;

    ListView md_buy_list;
    ListView md_sell_list;
    ButtonController buttonController;

    ArrayAdapter<MarketDepthValue> md_buy_adapter; // This array adapter will describe
    ArrayAdapter<MarketDepthValue> md_sell_adapter;
    String company;


    // how the listview will be shown
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_depth);
        buttonController = new ButtonController(this);
        md_buy_arraylist = new ArrayList<MarketDepthValue>();
        md_sell_arraylist = new ArrayList<MarketDepthValue>();
        Intent grab_intet = getIntent();
        company = grab_intet.getStringExtra("company");
        Toast.makeText(this, company, Toast.LENGTH_LONG).show();
        new grab_market_depth().execute("");

    }



    private void showInListView() {
        md_buy_list = (ListView) findViewById(R.id.buy_list);
        md_sell_list = (ListView) findViewById(R.id.sell_list);
        md_buy_adapter = new DSE_Data_adapter(md_buy_arraylist);
        md_buy_list.setAdapter(md_buy_adapter);
        md_sell_adapter = new DSE_Data_adapter(md_sell_arraylist);
        md_sell_list.setAdapter(md_sell_adapter);

    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<MarketDepthValue> {
        public DSE_Data_adapter(ArrayList<MarketDepthValue> sent_catagory) {
            super(getApplicationContext(), R.layout.dse_market_depth_like,
                    sent_catagory);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.dse_market_depth_like, parent, false);

            }

            MarketDepthValue current = getItem(position);
            TextView tvprice = (TextView) workingView
                    .findViewById(R.id.dse_market_depth_price);
            TextView tvvolume = (TextView) workingView
                    .findViewById(R.id.dse_market_depth_volume);

            tvprice.setText(current.getPrice());
            tvvolume.setText(current.getVolume());

            return workingView;
        }

    }




    class grab_market_depth extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(MarketDepth.this,
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
                Toast.makeText(MarketDepth.this, "Can't connect to the server! Check your internet.",
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
                    String url = Constants.MARKET_DEPTH + company + ".txt";
                    string = grabber
                            .grabSource(url);
                    Log.i(Constants.DEBUG_TAG, string);
                } else {
                    return null;
                }

            } catch (IOException | URISyntaxException e1) {

            }

            try {
                md_array = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                return null;
            }



            try {
                //Buy Values
                JSONArray buy_array = md_array.getJSONArray(0);
                int buy_array_size = buy_array.length();

                for (int i = 0; i < buy_array_size; i++) {
                    md_object = buy_array.getJSONObject(i);

                    String price = md_object.getString("buy_price");
                    String volume = md_object.getString("buy_volume");

                    MarketDepthValue current_value = new MarketDepthValue(price,volume);
                    md_buy_arraylist.add(current_value);

                }

                //Sell Values
                JSONArray sell_array = md_array.getJSONArray(1);
                int sell_array_size = sell_array.length();

                for (int i = 0; i < sell_array_size; i++) {
                    md_object = sell_array.getJSONObject(i);

                    String price = md_object.getString("sell_price");
                    String volume = md_object.getString("sell_volume");

                    MarketDepthValue current_value = new MarketDepthValue(price,volume);
                    md_sell_arraylist.add(current_value);

                }






            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }




}
