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


public class DSE30List extends Activity {
    /*
     * As we are parsing json array from the server for showing list view, so we
     * have to use JSONArray class and JSONObject class for retriving
     * information from file For that reason, we are Declaring jsonarray and
     * jsonobject class instance
     */
    // Declare Variables
    TextView tView;
    JSONObject dse_json_object = null;
    JSONArray dse_jsonArray = null;
    ArrayList<DSE_Company_data> dse_Company_datas;

    String comapnyname, lastTrade, changeAmount, changePercent;

    ListView dse_list_view;
    TextView search_text_box;
    ButtonController buttonController;
    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    public static final String file_name = "dse30_data_files";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_dse30_list);
        buttonController = new ButtonController(DSE30List.this);
        dse_Company_datas = new ArrayList<DSE_Company_data>();

        search_text_box = (TextView) findViewById(R.id.dse_list_search_box);
        search_text_box.setVisibility(View.GONE);

        new read_and_write_data().execute("");

    }

    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_latest_list_view);
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
                        DSE_Company_data clicked_Company_data = dse_Company_datas
                                .get(position);
                        Intent detail_info_intent = new Intent(DSE30List.this,
                                ItemInfo.class);
                        detail_info_intent.putExtra("TradingCode",
                                clicked_Company_data.getCompany_Name());
                        startActivity(detail_info_intent);

                    }
                });
    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
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
            View percentage_backgroud = workingView
                    .findViewById(R.id.dse_item_like_percent6ageback);

            tvcompany.setText(current.getCompany_Name());
            percentage_backgroud.setBackgroundDrawable(getResources()
                    .getDrawable(current.getDse_item_color()));

            tvchangepercentage.setText(current.getChange_percentage());
            tvchangetrade.setText(current.getChange_ammount());
            tvlasttrade.setText(current.getLaste_trade());

            return workingView;
        }

    }

    class read_and_write_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(DSE30List.this, "",
                "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            if (!has_active_net) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to internet! Check your internet.",
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
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/dse30itemgrab.txt");
                    // System.out.println(string);

                    DevTools.write_file(DSE30List.this, file_name, string);
                    has_active_net = true;
                }
                else if (DevTools.fileExistance(DSE30List.this, file_name)) {
                    string = DevTools.read_file(DSE30List.this, file_name);
                }
                else {
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
                    String lastTrade = dse_json_object.getString("ltp");
                    String change_ammount = dse_json_object
                            .getString("chnageammount");
                    String changePercent = dse_json_object
                            .getString("changepercentage") + "%";

                    double change_Ammount = Float.parseFloat(change_ammount);

                    int item_color = Color.GREEN;
                    if (change_Ammount < 0) {
                        item_color = R.drawable.border_drawer_red_percentage;
                    } else {
                        item_color = R.drawable.border_drawer_green_percentage;
                    }
                    if (!changePercent.contains("--%")) {
                        /***
                        DSE_Company_data currentCompany_data = new DSE_Company_data(
                                companyString, lastTrade, change_ammount,
                                changePercent, item_color);
                         ***/
                       // dse_Company_datas.add(currentCompany_data);
                    }

                    // System.out.println(changePercent);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return "";

        }

    }

}

