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

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.SQLException;
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



public class Portfolio_activity extends Activity {
    /*
     * As we are parsing json array from the server for showing list view, so we
     * have to use JSONArray class and JSONObject class for retriving
     * information from file For that reason, we are Declaring jsonarray and
     * jsonobject class instance
     */
    // Declare Variables
    ArrayList<DSE_Company_data> dse_Company_datas;

    String comapnyname, lastTrade, changeAmount, changePercent;
    DSE_Company_data portfolio_clicked_company;
    ListView dse_portfolio_list_view;
    ButtonController buttonController;
    int clicked_item_position = -1;
    double portfo_totalpurchasevalue = 0, portfo_totalcurrentvalue = 0,
            portfo_gain_loss = 0;
    String cString;
    ArrayAdapter<DSE_Company_data> adapter; // This array adapter will describe
    // how the listview will be shown
    boolean has_active_net = false; // This is for determining if we have a
    // active net connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_portfolio_activity);
        dse_Company_datas = new ArrayList<DSE_Company_data>();

        buttonController = new ButtonController(Portfolio_activity.this);
        buttonController.setSentfromportfolioactivity(true);

        // dse_Company_datas_for_search = new ArrayList<DSE_Company_data>();

        new read_and_write_data().execute("");

    }

    private void showInListView() {

        dse_portfolio_list_view = (ListView) findViewById(R.id.dse_portfolio_listview);
        adapter = new DSE_Portfolio_Data_adapter();
        dse_portfolio_list_view.setAdapter(adapter);
        // dse_Company_datas_for_search.addAll(dse_Company_datas);
        registeronclicklistener();

    }

    private void registeronclicklistener() {
        dse_portfolio_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        portfolio_clicked_company = dse_Company_datas
                                .get(position);
                        clicked_item_position = position;
                        String[] portfolio_items = { "Add Stock",
                                "Reduce Stock", "Remove Stock",
                                "Show Item Detail" };
                        Portfolio_menu_helper.generate_dialog(
                                Portfolio_activity.this,
                                portfolio_clicked_company, portfolio_items);

                        // Dse_item_arraylist
                        // .setCompany_data_object_for_passing_through_portfolio(portfolio_clicked_company);
                        // Intent customize_portfolio_intent = new Intent(
                        // Portfolio_activity.this,
                        // PortfolioCustomize.class);
                        // startActivity(customize_portfolio_intent);

                    }

                });

    }

    public class DSE_Portfolio_Data_adapter extends
            ArrayAdapter<DSE_Company_data> {
        public DSE_Portfolio_Data_adapter() {
            super(getApplicationContext(), R.layout.portfolio_list_like,
                    dse_Company_datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.portfolio_list_like, parent, false);

            }

            DSE_Company_data current = getItem(position);

            TextView tvcompany = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_list_itemname);

            TextView tvnoofstocks = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_list_noofstocks);
            TextView tvpurchasevalue = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_list_purchasevalue);
            TextView tvcurrentvalue = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_list_currentvalue);
            TextView tvgainlossvalue = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_list_gain_loss_value);
            TextView tvgainlosspercentage = (TextView) workingView
                    .findViewById(R.id.dse_portfolio_gain_loss_percentage);

            tvnoofstocks.setText(current.getNumberofstocks());
            tvcompany.setText(current.getCompany_Name());

            tvcurrentvalue.setText(current.getLaste_trade());
            tvgainlosspercentage.setTextColor(current.getDse_item_color());
            tvpurchasevalue.setText(current.getPurchaceunit());
            tvgainlossvalue.setText(current.getGain_loss_ammount());
            tvgainlosspercentage.setText(current.getGain_loss_percentage());
            tvgainlosspercentage.setTextColor(current.getDse_item_color());
            // System.out.println(current.getChange_percentage());

            return workingView;
        }

    }


    class read_and_write_data extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(
                Portfolio_activity.this, "", "Processing Portfolio...", true);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dse_Company_datas.clear();
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            calculate_prices();

            showInListView();

        }


        private void calculate_prices() {
            TextView totalpurchase, totalcurrent, totalgainloss;
            totalpurchase = (TextView) findViewById(R.id.portfolio_totalpurchase);
            totalcurrent = (TextView) findViewById(R.id.portfolio_totalcurrent);
            totalgainloss = (TextView) findViewById(R.id.portfolio_totalgain_loss);
            double purchase = 0, current = 0, gain_loss = 0;
            for (DSE_Company_data dse_Company_data_current : dse_Company_datas) {
                purchase += Double.parseDouble(dse_Company_data_current
                        .getTotal_purchase_unitprice());
                current += Double.parseDouble(dse_Company_data_current
                        .getTotal_parchase_lasttrade());

            }
            gain_loss = current - purchase;
            totalpurchase.setText(String.format("%.2f", purchase));
            totalcurrent.setText(String.format("%.2f", current));
            totalgainloss.setText(String.format("%.2f", gain_loss));
            if (gain_loss >= 0) {
                totalgainloss.setTextColor(Color.GREEN);
            } else {
                totalgainloss.setTextColor(Color.RED);
            }

        }

        SQLiteDatabase dsebd;
        JSONArray itemarray;

        @Override
        protected String doInBackground(String... params) {
            // if there is a net connection, update current price... :)
            String item_json_txt;
            boolean hasnet = false;

            SrcGrabber grabber = new SrcGrabber();
            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    hasnet = true;

                    item_json_txt = grabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/itemvalues_portfolio.txt");
                    itemarray = new JSONArray(item_json_txt);
                }
            } catch (IOException | URISyntaxException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dsebd = openOrCreateDatabase("dsedatabase", MODE_PRIVATE, null);
            dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
            String currentprice = "0";
            Cursor portfolio_cursor = dsebd.rawQuery(
                    "SELECT * FROM portfolioitems", null);


            if (portfolio_cursor.getCount() == 0) {
                return null;
            } else {
                portfolio_cursor.moveToFirst();
                do {
                    String itemname = portfolio_cursor.getString(0);


                    if (hasnet) {
                        try {

                            try {
                                currentprice = update_item_currentprice(itemname);
                                if (currentprice.equals("Not Traded Today")) {

                                    currentprice = portfolio_cursor
                                            .getString(2);
                                }
                                else if (currentprice.equals("--")) {
                                    currentprice = portfolio_cursor
                                            .getString(2);
                                }
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block

                        }
                    }
                    String purchaseunit = portfolio_cursor.getString(1);
                    if (!hasnet) {
                        currentprice = portfolio_cursor.getString(2);
                    }

                    String numberofstocks = portfolio_cursor.getString(3);
                    DSE_Company_data currentcompany = new DSE_Company_data(
                            itemname, currentprice, numberofstocks,
                            purchaseunit, Color.GREEN, 9.81f);
                    if (Float.parseFloat(currentcompany.getGain_loss_ammount()) > 0) {
                        currentcompany.setDse_item_color(Color.GREEN);
                    } else {
                        currentcompany.setDse_item_color(Color.RED);
                    }
                    dse_Company_datas.add(currentcompany);

                } while (portfolio_cursor.moveToNext());
                portfolio_cursor.close();
                dsebd.close();

                return null;
            }

        }

        private String update_item_currentprice(final String itemname)
                throws JSONException, InterruptedException {

            Thread addthread = new Thread(new Runnable() {

                @Override
                public void run() {
                    SQLiteDatabase tempdb = openOrCreateDatabase("dsedatabase",
                            MODE_PRIVATE, null);
                    for (int i = 0; i < itemarray.length(); i++) {

                        try {
                            if (itemarray.getJSONObject(i).getString("company")
                                    .equals(itemname)) {
                                String currentprice = itemarray
                                        .getJSONObject(i)
                                        .getString("lastTrade").toString();
                                if (!currentprice.equals("Not Traded Today")) {
                                    tempdb.execSQL("UPDATE portfolioitems SET currentprice='"
                                            + currentprice
                                            + "' WHERE stockname='"
                                            + itemname
                                            + "';");
                                }

                                cString = currentprice;
                                tempdb.close();
                                break;
                            }
                        } catch (SQLException | JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    tempdb.close();

                }
            });

            addthread.start();
            addthread.join();
            return cString;

        }

    }

}