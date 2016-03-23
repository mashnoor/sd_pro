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


public class Top10Shares extends Activity {
	/*
	 * As we are parsing json array from the server for showing list view, so we
	 * have to use JSONArray class and JSONObject class for retriving
	 * information from file For that reason, we are Declaring jsonarray and
	 * jsonobject class instance
	 */
    // Declare Variables

    JSONObject dse_json_object = null;
    JSONArray dse_jsonArray = null;
    ArrayList<DSE_Company_data> dse_top_shares_datas;
    ButtonController buttonController;
    ListView dse_list_view;
    View btn_byvalue, btn_byvolume, btn_bytrade, byvalue_downpanel,
            byvolume_downpanel, bytrade_downpanel;
    TextView header_pvalue_text;
    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    public static final String file_name_byvalue = "dse_top20shares_byvalue";
    public static final String file_name_byvolume = "dse_top20shares_byvolume";
    public static final String file_name_bytrade = "dse_top20shares_bytrade";
    public static final String link_byvalue = "http://104.131.22.246/dev/smartdsefiles/top20sharesgrab_byvalue.txt";
    public static final String link_byvolume = "http://104.131.22.246/dev/smartdsefiles/top20sharesgrab_byvolume.txt";
    public static final String link_bytrade = "http://104.131.22.246/dev/smartdsefiles/top20sharesgrab_bytrade.txt";
    public static final String byvalue = "Value";
    public static final String byvolume = "Volume";
    public static final String bytrade = "Trade";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_top10_shares);
        initializevariable();
        dse_top_shares_datas = new ArrayList<DSE_Company_data>();

        buttonController = new ButtonController(Top10Shares.this);
        String[] test = { file_name_byvalue, link_byvalue };
        new read_and_write_data().execute(test);

    }

    private void initializevariable() {
        header_pvalue_text = (TextView) findViewById(R.id.dse_topshares_pvalue_header);
        btn_bytrade = findViewById(R.id.btn_topshares_bytrade);
        btn_byvalue = findViewById(R.id.btn_topshares_byvalue);
        btn_byvolume = findViewById(R.id.btn_topshares_byvolume);
        byvalue_downpanel = findViewById(R.id.topshares_byvalue_down_panel);
        bytrade_downpanel = findViewById(R.id.topshares_bytrade_down_panel);
        byvolume_downpanel = findViewById(R.id.topshares_byvolume_down_panel);

        String[] task_byvalue = { file_name_byvalue, link_byvalue };
        String[] task_byvolume = { file_name_byvolume, link_byvolume };
        String[] task_bytrade = { file_name_bytrade, link_bytrade };
        set_listener(btn_bytrade, bytrade_downpanel, task_bytrade,
                byvalue_downpanel, byvolume_downpanel, bytrade);
        set_listener(btn_byvalue, byvalue_downpanel, task_byvalue,
                byvolume_downpanel, bytrade_downpanel, byvalue);
        set_listener(btn_byvolume, byvolume_downpanel, task_byvolume,
                bytrade_downpanel, byvalue_downpanel, byvolume);
    }

    private void set_listener(View btn, final View downpanel,
                              final String[] filter, final View other1, final View other2,
                              final String headstring) {
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downpanel.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                other1.setBackgroundColor(Color.BLACK);
                other2.setBackgroundColor(Color.BLACK);
                header_pvalue_text.setText(headstring);
                new read_and_write_data().execute(filter);

            }
        });

    }


    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_top_20_shares_list);
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
                        DSE_Company_data clicked_Company_data = dse_top_shares_datas
                                .get(position);
                        Intent detail_info_intent = new Intent(
                                Top10Shares.this, ItemInfo.class);
                        detail_info_intent.putExtra("TradingCode",
                                clicked_Company_data.getCompany_Name());
                        startActivity(detail_info_intent);

                    }
                });
    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.top20shareslistlike,
                    dse_top_shares_datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.top20shareslistlike, parent, false);

            }

            DSE_Company_data current = getItem(position);
            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_topshares_itemname);

            TextView tvlasttrade = (TextView) workingView
                    .findViewById(R.id.dse_topshares_lasttradeprice);
            TextView tvpvalue = (TextView) workingView
                    .findViewById(R.id.dse_topshares_pvalue);

            tvcompany.setText(current.getCompany_Name());

            tvpvalue.setText(current.getConcernvalue());
            tvlasttrade.setText(current.getLaste_trade());

            return workingView;
        }

    }

    class read_and_write_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(Top10Shares.this,
                "", "Retrieving Data...", true);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dse_top_shares_datas.clear();
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            if (!has_active_net) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to server! Check your internet.",
                        Toast.LENGTH_LONG).show();
            }
            if (result != null) {
                showInListView();

            }

        }

        @Override
        protected String doInBackground(String... filter) {
            SrcGrabber grabber = new SrcGrabber();
            String string = null;
            String process_file = filter[0];
            String process_link = filter[1];

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = grabber.grabSource(process_link);
                    // System.out.println(string);
                    DevTools.write_file(Top10Shares.this, process_file, string);
                    has_active_net = true;
                } else if (DevTools.fileExistance(Top10Shares.this,
                        process_file)) {
                    string = DevTools.read_file(Top10Shares.this, process_file);
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
                    String lastTrade = dse_json_object.getString("ltp");
                    String pvalue = dse_json_object.getString("pvalue");

                    DSE_Company_data currentCompany_data = new DSE_Company_data(
                            companyString, lastTrade, pvalue);
                    dse_top_shares_datas.add(currentCompany_data);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }

}
