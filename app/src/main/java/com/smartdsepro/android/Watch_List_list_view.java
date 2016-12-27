package com.smartdsepro.android;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


public class Watch_List_list_view extends Activity {
    // Declare Variables

    TextView tView;

    JSONObject dse_json_object = null;
    JSONArray dse_jsonArray = null;
    ArrayList<DSE_Company_data> dse_Company_datas;

    String comapnyname, lastTrade, changeAmount, changePercent;
    StringBuilder builder;
    ListView dse_list_view;
    TextView search_text_box;
    ArrayAdapter<DSE_Company_data> adapter;
    boolean has_active_net;
    ButtonController buttonController;

    public static final String file_name = "dse_data_files_all";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        dse_Company_datas = new ArrayList<DSE_Company_data>();
        buttonController = new ButtonController(Watch_List_list_view.this);


        search_text_box = (TextView) findViewById(R.id.dse_list_search_box);
        search_text_box.setVisibility(View.GONE);

        new read_and_write_data().execute("");

    }

    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_latest_list_view);
        adapter = new DSE_Data_adapter();
        dse_list_view.setAdapter(adapter);

        if (dse_Company_datas.size() == 0) {
            Toast.makeText(Watch_List_list_view.this, "No item in watchlist!",
                    Toast.LENGTH_LONG).show();

        }
        registeronclicklistener();

    }

    private void registeronclicklistener() {
        dse_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        DSE_Company_data clicked_Company_data = dse_Company_datas
                                .get(position);
                        Intent detail_info_intent = new Intent(
                                Watch_List_list_view.this, ItemInfo.class);
                        detail_info_intent.putExtra("TradingCode",
                                clicked_Company_data.getCompany_Name());
                        startActivity(detail_info_intent);

                    }
                });
    }

    private class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.item_view_dse_data_like,
                    dse_Company_datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.item_view_dse_data_like, parent, false);

            }

            DSE_Company_data current = getItem(position);
            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_compane_name);
            TextView tvchangetrade = (TextView) workingView
                    .findViewById(R.id.dse_change_ammount);
            TextView tvlasttrade = (TextView) workingView
                    .findViewById(R.id.dse_last_trade);
            TextView tvchangepercentage = (TextView) workingView
                    .findViewById(R.id.dse_change_percentage);
            TextView tvvolume = (TextView) workingView.findViewById(R.id.dse_volume);
            View percentage_backgroud = workingView
                    .findViewById(R.id.dse_item_like_percent6ageback);
            tvcompany.setText(current.getCompany_Name());
            percentage_backgroud.setBackgroundDrawable(getResources()
                    .getDrawable(current.getDse_item_color()));
            tvchangepercentage.setText(current.getChange_percentage() + "%");
            tvchangetrade.setText(current.getChange_ammount());
            tvlasttrade.setText(current.getLaste_trade());
            tvvolume.setText(current.getVolume());

            return workingView;
        }

    }

    class read_and_write_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(
                Watch_List_list_view.this, "", "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            showInListView();

        }

        @Override
        protected String doInBackground(String... params) {

            String string = "No Data";

            ArrayList<String> dse_item_watchlist = new ArrayList<String>();

            SQLiteDatabase dsebd = openOrCreateDatabase("dsedatabase",
                    MODE_PRIVATE, null);
            dsebd.execSQL("CREATE TABLE IF NOT EXISTS watchlist(itemnames VARCHAR);");
            Cursor cursor = dsebd.rawQuery("SELECT * FROM watchlist;", null);
            cursor.moveToFirst();
            int watchlist_size = cursor.getCount();
            for (int i = 0; i < watchlist_size; i++) {
                dse_item_watchlist.add(cursor.getString(0));
                cursor.moveToNext();
            }

            dsebd.close();
            cursor.close();

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = SrcGrabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/itemvalues_all.txt");

                    DevTools.write_file(Watch_List_list_view.this, file_name,
                            string);
                    has_active_net = true;
                } else if (DevTools.fileExistance(Watch_List_list_view.this,
                        file_name)) {

                    string = DevTools.read_file(Watch_List_list_view.this,
                            file_name);
                    has_active_net = false;

                } else {
                    return null;
                }

            } catch (IOException | URISyntaxException e1) {

            }
            try {
                dse_jsonArray = new JSONArray(string);

            } catch (JSONException e) {

            }

            int jsonarraysize = dse_jsonArray.length();
            System.out.println("Watchlist Size: " + jsonarraysize);

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    dse_json_object = dse_jsonArray.getJSONObject(i);
                    String companyString = dse_json_object.getString("company");
                    String lastTrade = dse_json_object.getString("lastTrade");
                    String changeAmount = dse_json_object
                            .getString("changeAmount");
                    String changePercent = dse_json_object
                            .getString("changePercent");
                    String volume = dse_json_object.getString("volume");
                    int cursour_length = cursor.getCount();
                    for (int j = 0; j < cursour_length; j++) {
                        if (dse_item_watchlist.get(j).equals(companyString)) {
                            float change_ammount = -1;
                            try {
                                change_ammount = Float.parseFloat(changeAmount);

                            } catch (Exception e) {
                                // TODO: handle exception
                            }

                            int item_color = Color.GREEN;
                            if (change_ammount < 0) {
                                item_color = R.drawable.border_drawer_red_percentage;
                            } else {
                                item_color = R.drawable.border_drawer_green_percentage;
                            }
                            DSE_Company_data currentCompany_data = new DSE_Company_data(
                                    companyString, lastTrade, changeAmount,
                                    changePercent,volume, item_color);
                            dse_Company_datas.add(currentCompany_data);
                            break;
                        }

                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }

}
