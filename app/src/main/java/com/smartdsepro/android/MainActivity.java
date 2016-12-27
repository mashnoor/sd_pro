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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
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
    ArrayList<DSE_Company_data> dse_Company_datas,
            dse_Company_datas_for_search;
    String comapnyname, lastTrade, changeAmount, changePercent;
    StringBuilder builder;
    ListView dse_list_view;
    TextView search_text_box;
    ButtonController buttonController;

    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    public static final String file_name = "dse_data_files";
    public static final String file_name_all = "dse_data_files_all";
    public static boolean show = false;
    public static final String ITEM_VALUES = "http://104.131.22.246/dev/smartdsefiles/itemvalues.txt";
    public static final String ITEM_VALUES_ALL = "http://104.131.22.246/dev/smartdsefiles/itemvalues_all.txt";

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        buttonController = new ButtonController(MainActivity.this);
        dse_Company_datas = new ArrayList<>();
        dse_Company_datas_for_search = new ArrayList<>();

        search_text_box = (TextView) findViewById(R.id.dse_list_search_box);
        search_text_box.setVisibility(View.GONE);
        if (show) {
            search_text_box.setVisibility(View.VISIBLE);

        }


		/*
		 * search_text_box .setOnFocusChangeListener(new
		 * View.OnFocusChangeListener() {
		 *
		 * @Override public void onFocusChange(View arg0, boolean hasfocus) { if
		 * (hasfocus) { search_text_box .setBackgroundDrawable(getResources()
		 * .getDrawable( R.drawable.border_drawer_hex)); } else if (!hasfocus) {
		 * search_text_box .setBackgroundDrawable(getResources() .getDrawable(
		 * R.drawable.border_drawer_white));
		 *
		 * }
		 *
		 * } });
		 */

        search_text_box.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String search_text = search_text_box.getText().toString();
                if (adapter!=null) {
                    ((DSE_Data_adapter) adapter).search(search_text.toLowerCase());
                }


            }
        });
        Bundle extraBundle = getIntent().getExtras();
        if (extraBundle == null) {

            String[] link_file = { ITEM_VALUES_ALL, file_name_all };
            new read_and_write_data().execute(link_file);

        } else {
            String[] link_file = { ITEM_VALUES, file_name };

            new read_and_write_data().execute(link_file);
        }

    }



    private void showInListView() {
        dse_list_view = (ListView) findViewById(R.id.dse_latest_list_view);
        adapter = new DSE_Data_adapter();
        dse_list_view.setAdapter(adapter);
        dse_Company_datas_for_search.addAll(dse_Company_datas);
        registeronclicklistener();

    }

    private void registeronclicklistener() {
        dse_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long arg3) {
                        DSE_Company_data clicked_Company_data = dse_Company_datas_for_search
                                .get(position);
                        Intent detail_info_intent = new Intent(
                                MainActivity.this, ItemInfo.class);
                        detail_info_intent.putExtra("TradingCode",
                                clicked_Company_data.getCompany_Name());
                        detail_info_intent.putExtra("change_amount", clicked_Company_data.getChange_ammount());
                        detail_info_intent.putExtra("change_percentage",clicked_Company_data.getChange_percentage());
                        detail_info_intent.putExtra("ltp", clicked_Company_data.getLaste_trade());


                        startActivity(detail_info_intent);

                    }
                });
    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<DSE_Company_data> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.item_view_dse_data_like,
                    dse_Company_datas_for_search);
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
            TextView tvvolume = (TextView) workingView.findViewById(R.id.dse_volume);

            tvcompany.setText(current.getCompany_Name());
            percentage_backgroud.setBackgroundDrawable(getResources()
                    .getDrawable(current.getDse_item_color()));
            tvchangepercentage.setText(current.getChange_percentage());
            tvchangetrade.setText(current.getChange_ammount());
            tvlasttrade.setText(current.getLaste_trade());
            tvvolume.setText(current.getVolume());

            return workingView;
        }

        public void search(String search_string) {
            dse_Company_datas_for_search.clear();
            if (search_string.length() == 0) {
                dse_Company_datas_for_search.addAll(dse_Company_datas);
            } else {
                for (DSE_Company_data temp_comapny_data : dse_Company_datas) {
                    if (temp_comapny_data.getCompany_Name().toLowerCase()
                            .contains(search_string)) {
                        dse_Company_datas_for_search.add(temp_comapny_data);
                    }
                }

            }
            notifyDataSetChanged();

        }
    }

    class read_and_write_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
                "", "Retrieving Data...", true);

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
        protected String doInBackground(String... link_file) {

            String string = null;
            String currfile = link_file[1];
            String currlink = link_file[0];

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = SrcGrabber.grabSource(currlink);
                    //DevTools.write_file(MainActivity.this, currfile, string);
                    has_active_net = true;
                } else if (DevTools.fileExistance(MainActivity.this, currfile)) {
                    string = DevTools.read_file(MainActivity.this, currfile);
                } else {
                    return null;
                }

                // System.out.println(string);

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
                    String companyString = dse_json_object.getString("company");
                    String lastTrade = dse_json_object.getString("lastTrade");
                    String changeAmount = dse_json_object
                            .getString("changeAmount");
                    String changePercent = dse_json_object
                            .getString("changePercent") + "%";
                    String volume = dse_json_object.getString("volume");
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
                    if (!changePercent.equals("--%") && !changePercent.equals("%")) {
                        if (!changePercent.equals("Not Traded Today%")) {
                            DSE_Company_data currentCompany_data = new DSE_Company_data(
                                    companyString, lastTrade, changeAmount,
                                    changePercent,volume, item_color);
                            dse_Company_datas.add(currentCompany_data);
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
