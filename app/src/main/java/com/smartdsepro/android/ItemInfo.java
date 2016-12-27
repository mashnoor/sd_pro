package com.smartdsepro.android;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




public class ItemInfo extends Activity {
    public static String tradingcode;

    // DSE Item Detail text views
    TextView closingprice, yesterdaycloseprice, openprice, daysrange, volume,
            adjustopenprice, totaltrade, marketcapital, authorizedcapital,
            paid_upcapital, facevalue, totalnumberofsecurities, weekrange,
            marketlot, marketcatagory, rightissue, yearend, reserveandsurplus,
            bonusissue, company, change, lasttrade, lastagmheld,
            tradingcode_sent, change_value, pebasic, pediluted, value,
            fp_2013_eps, fp_2013_nav, fp_2013_npat_co, fp_2013_npat_eo,
            fp_2014_eps, fp_2014_nav, fp_2014_npat_co, fp_2014_npat_eo,
            dividend_2009, dividend_2010, dividend_2011, dividend_2012,
            dividend_2013, dividend_2014, sp_director, sp_govt, sp_institute,
            sp_foreign, sp_public;

    Button portfolioButton, watchlistButton, show_moreButton, show_newsArchiveButton, show_marketDepthButton, priceAlertButton, graphButton;
    ButtonController buttonController;
    View change_percentage_layout;
    DSE_Company_data portfolio_clicked_data;
    boolean addedinwatchlist = false;
    boolean addedinportfolio = false;

    final static String item_detail_link = "http://104.131.22.246/dev/smartdsefiles/dsex_items/";
    final static String SHOW_MORE_LINK = "http://dsebd.org/displayCompany.php?name=";
    public static final String addtowatchlist = "Add to watchlist";
    public static final String removefromwatchlist = "Remove from Watchlist";
    public static final String databasename = "DSE_WATCHLIST_DATABASE";
    public static final String addtoportfolio = "Add to portfolio";
    public static final String customizeportfolio = "Customize Portfolio";
    public boolean hasactivenet = true;
    public boolean fileexists = false;
    String json_data;

    String change_amout, ltp, change_percentage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_item_info);



        Intent item_info = getIntent();
        tradingcode = item_info.getStringExtra("TradingCode");
        change_amout = item_info.getStringExtra("change_amount");
        ltp = item_info.getStringExtra("ltp");
        change_percentage = item_info.getStringExtra("change_percentage");


        buttonController = new ButtonController(ItemInfo.this);
        initializetextviews();

        new fetch_infos().execute("");
        // Onclick Listeners
        show_moreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                opensite(SHOW_MORE_LINK + tradingcode);

            }
        });
        show_newsArchiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsArchive_Intent = new Intent(ItemInfo.this, NewsDetail.class);
                newsArchive_Intent.putExtra("company", tradingcode);
                startActivity(newsArchive_Intent);


            }
        });
        if(PriceAlertHelper.ifItemExists(this, tradingcode))
        {
            priceAlertButton.setText(Constants.CUSTOMIZE_PRICE_ALERT);

        }
        else
        {
            priceAlertButton.setText(Constants.ADD_PRICE_ALERT);
        }

        priceAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(priceAlertButton.getText().toString().equals(Constants.ADD_PRICE_ALERT))
                {
                    PriceAlertHelper.addPriceAlert(ItemInfo.this, tradingcode, lasttrade.getText().toString());
                }
                else
                {
                    PriceAlertHelper.show_customize_price_alert_dialog(ItemInfo.this, tradingcode);
                }

            }
        });


        portfolioButton.setText(addtoportfolio);
        watchlistButton.setText(addtowatchlist);
        portfolioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (portfolioButton.getText().toString().equals(addtoportfolio)) {
					/*
					 * Intent intent = new Intent(ItemInfo.this,
					 * Portfolio_Add_Activity.class);
					 * intent.putExtra("TradingCode", tradingcode);
					 * intent.putExtra("LastTrade",
					 * lasttrade.getText().toString()); startActivity(intent);
					 */
                    Portfolio_menu_helper.new_portfolio_add(ItemInfo.this,
                            tradingcode, lasttrade.getText().toString(),
                            json_data);
                } else if (portfolioButton.getText().toString()
                        .equals(customizeportfolio)) {
                    String[] portfolio_strings = {"Add Stock", "Reduce Stock",
                            "Remove Stock"};
                    String[] portfolio_item_helper = Portfolio_menu_helper
                            .get_infos_of_portfolio_item(ItemInfo.this,
                                    tradingcode);

                    portfolio_clicked_data = new DSE_Company_data(tradingcode,
                            lasttrade.getText().toString(),
                            portfolio_item_helper[0], portfolio_item_helper[1],
                            0, 0);
                    Portfolio_menu_helper.generate_dialog(ItemInfo.this,
                            portfolio_clicked_data, portfolio_strings);

                }

            }
        });
        show_marketDepthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent depth_intent = new Intent(ItemInfo.this, MarketDepth.class);
                depth_intent.putExtra("company", tradingcode);
                startActivity(depth_intent);


            }
        });

        watchlistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (watchlistButton.getText().toString().equals(addtowatchlist)) {
                    new add_to_watch_list().execute("");
                } else if (watchlistButton.getText().toString()
                        .equals(removefromwatchlist)) {
                    SQLiteDatabase dsebd = openOrCreateDatabase("dsedatabase",
                            MODE_PRIVATE, null);
                    dsebd.execSQL("CREATE TABLE IF NOT EXISTS watchlist(itemnames VARCHAR);");
                    dsebd.execSQL("DELETE FROM watchlist WHERE itemnames='"
                            + tradingcode + "'");
                    dsebd.close();
                    addedinwatchlist = false;
                    if (!addedinportfolio) {
                        deleteFile(tradingcode);
                    }

                    Toast.makeText(getApplicationContext(),
                            "Item removed from watchlist successfully.",
                            Toast.LENGTH_LONG).show();
                    change_button_text(watchlistButton, addtowatchlist);

                }

            }
        });
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ItemInfo.this, "Graph Will be available soon!", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void opensite(String Site) {
        Uri uri = Uri.parse(Site);
        Intent intent = new Intent(ItemInfo.this,  ItemDetailWeb.class);
        intent.putExtra("item", tradingcode);
        startActivity(intent);

    }
    private void initializetextviews() {
        tradingcode_sent = (TextView) findViewById(R.id.dse_info_tradingcode_and_businesssegment);
        closingprice = (TextView) findViewById(R.id.dse_info_closing_price);
        openprice = (TextView) findViewById(R.id.dse_info_today_open_price);
        yesterdaycloseprice = (TextView) findViewById(R.id.dse_info_yesterday_closing_price);
        daysrange = (TextView) findViewById(R.id.dse_info_days_range);
        volume = (TextView) findViewById(R.id.dse_info_volume);
        adjustopenprice = (TextView) findViewById(R.id.dse_info_adjust_open_price);
        totaltrade = (TextView) findViewById(R.id.dse_info_total_trade);
        marketcapital = (TextView) findViewById(R.id.dse_info_market_capital_in_bdt);
        authorizedcapital = (TextView) findViewById(R.id.dse_info_authorized_capital_in_bdt);
        paid_upcapital = (TextView) findViewById(R.id.dse_info_paid_up_value);
        facevalue = (TextView) findViewById(R.id.dse_info_face_value);
        totalnumberofsecurities = (TextView) findViewById(R.id.dse_info_total_number_of_securities);
        weekrange = (TextView) findViewById(R.id.dse_info_52_week_range);
        marketlot = (TextView) findViewById(R.id.dse_info_market_lot);
        marketcatagory = (TextView) findViewById(R.id.dse_info_market_catagory);
        rightissue = (TextView) findViewById(R.id.dse_info_right_issue);
        yearend = (TextView) findViewById(R.id.dse_info_year_end);
        reserveandsurplus = (TextView) findViewById(R.id.dse_info_reserveandsurplus);
        bonusissue = (TextView) findViewById(R.id.dse_info_bonus_issue);
        company = (TextView) findViewById(R.id.dse_info_company);
        lasttrade = (TextView) findViewById(R.id.dse_info_last_trade);
        change = (TextView) findViewById(R.id.dse_info_change);
        lastagmheld = (TextView) findViewById(R.id.dse_info_last_agm_held);
        change_value = (TextView) findViewById(R.id.dse_info_change_value);
        pebasic = (TextView) findViewById(R.id.dse_info_p_e_basic);
        pediluted = (TextView) findViewById(R.id.dse_info_p_e_diluted);
        value = (TextView) findViewById(R.id.dse_info_value);
        fp_2013_eps = (TextView) findViewById(R.id.dse_info_fp_2013_eps);
        fp_2013_nav = (TextView) findViewById(R.id.dse_info_fp_2013_nav);
        fp_2013_npat_co = (TextView) findViewById(R.id.dse_info_fp_2013_npat_continueoperation);
        fp_2013_npat_eo = (TextView) findViewById(R.id.dse_info_fp_2013_npat_extraordinaryincome);
        fp_2014_eps = (TextView) findViewById(R.id.dse_info_fp_2014_eps);
        fp_2014_nav = (TextView) findViewById(R.id.dse_info_fp_2014_nav);
        fp_2014_npat_co = (TextView) findViewById(R.id.dse_info_fp_2014_npat_continueoperation);
        fp_2014_npat_eo = (TextView) findViewById(R.id.dse_info_fp_2014_npat_extraordinaryincome);
        dividend_2009 = (TextView) findViewById(R.id.dse_info_dividend_2009);
        dividend_2010 = (TextView) findViewById(R.id.dse_info_dividend_2010);
        dividend_2011 = (TextView) findViewById(R.id.dse_info_dividend_2011);
        dividend_2012 = (TextView) findViewById(R.id.dse_info_dividend_2012);
        dividend_2013 = (TextView) findViewById(R.id.dse_info_dividend_2013);
        dividend_2014 = (TextView) findViewById(R.id.dse_info_dividend_2014);
        sp_director = (TextView) findViewById(R.id.dse_info_sp_director);
        sp_institute = (TextView) findViewById(R.id.dse_info_sp_institute);
        sp_govt = (TextView) findViewById(R.id.dse_info_sp_government);
        sp_foreign = (TextView) findViewById(R.id.dse_info_sp_foreign);
        sp_public = (TextView) findViewById(R.id.dse_info_sp_public);

        // Buttons
        portfolioButton = (Button) findViewById(R.id.dse_item_portfolio_button);
        watchlistButton = (Button) findViewById(R.id.dse_item_watchlist_button);
        show_moreButton = (Button) findViewById(R.id.dse_item_detail_showmore);
        show_newsArchiveButton = (Button) findViewById(R.id.dse_item_detail_newsarchive);
        show_marketDepthButton = (Button) findViewById(R.id.dse_item_detail_see_market_depth);
        priceAlertButton = (Button) findViewById(R.id.dse_item_detail_add_price_alert);
        graphButton = (Button) findViewById(R.id.dse_item_detail_showgraph);

        // Layouts
        change_percentage_layout = findViewById(R.id.dse_info_change_layout);

    }

    private void change_button_text(Button have_to_change_Button,
                                    String change_string) {
        have_to_change_Button.setText(change_string);

    }

    private String getLtp() throws Exception
    {
        String ltp_json = DevTools.read_file(ItemInfo.this, Constants.LTP_FILE);
        JSONArray ltp_array = new JSONArray(ltp_json);
        int ltp_array_size = ltp_array.length();
        JSONObject ltp_object;
        for (int i = 0; i<ltp_array_size; i++)
        {
            ltp_object = ltp_array.getJSONObject(i);
            String curr_item = ltp_object.getString("company");
            if(curr_item.equals(tradingcode))
            {
                String ltp = ltp_object.getString("lastTrade");
                return ltp;
            }
        }
        return "--";
    }

    class add_to_watch_list extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            change_button_text(watchlistButton, removefromwatchlist);
            Toast.makeText(getApplicationContext(),
                    "Item added to watchlist successfully.", Toast.LENGTH_LONG)
                    .show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                SQLiteDatabase dsebd = openOrCreateDatabase("dsedatabase",
                        MODE_PRIVATE, null);
                dsebd.execSQL("CREATE TABLE IF NOT EXISTS watchlist(itemnames VARCHAR);");
                dsebd.execSQL("INSERT INTO watchlist VALUES('" + tradingcode
                        + "');");
                dsebd.close();

                DevTools.write_file(ItemInfo.this, tradingcode, json_data);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

    }

    class fetch_infos extends AsyncTask<String, String, String[]> {
        ProgressDialog progressDialog = ProgressDialog.show(ItemInfo.this, "",
                "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            // Change the text if exists in watchlist
            if (Portfolio_menu_helper.watchlist_existance(ItemInfo.this,
                    tradingcode)) {
                change_button_text(watchlistButton, removefromwatchlist);
                addedinwatchlist = true;
            }

            // Check if exists in portfolio
            if (Portfolio_menu_helper.portfolio_exixtance(ItemInfo.this,
                    tradingcode)) {
                change_button_text(portfolioButton, customizeportfolio);
                addedinportfolio = true;
            }

            if (result == null) {
                portfolioButton.setEnabled(false);
                watchlistButton.setEnabled(false);
            }

            if (!hasactivenet && !fileexists) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to server! Check your internet.",
                        Toast.LENGTH_LONG).show();
            } else {
                if (!(result == null)) {

                    // Set text views with respective datas

                    closingprice.setText(result[0]);
                    yesterdaycloseprice.setText(result[1]);
                    openprice.setText(result[2]);
                    adjustopenprice.setText(result[3]);
                    daysrange.setText(result[4]);
                    volume.setText(result[5]);
                    totaltrade.setText(result[6]);
                    marketcapital.setText(result[7]);
                    authorizedcapital.setText(result[8]);
                    paid_upcapital.setText(result[9]);
                    facevalue.setText(result[10]);
                    totalnumberofsecurities.setText(result[11]);
                    weekrange.setText(result[12]);
                    marketlot.setText(result[13]);
                    tradingcode_sent.setText("Item: " + tradingcode
                            + ", Sector: " + result[14]);
                    rightissue.setText(result[15]);
                    yearend.setText(result[16]);
                    reserveandsurplus.setText(result[17]);
                    bonusissue.setText(result[18]);
                    company.setText(result[19]);
                    lasttrade.setText(result[20]);
                    //lasttrade.setText(ltp);


                    lastagmheld.setText(result[23]);
                    pebasic.setText(result[24]);
                    pediluted.setText(result[25]);
                    value.setText(result[26]);
                    marketcatagory.setText(result[27]);
                    fp_2013_eps.setText(result[28]);
                    fp_2013_nav.setText(result[29]);
                    fp_2013_npat_co.setText(result[30].replaceAll("\\s+", ""));
                    fp_2013_npat_eo.setText(result[31].replaceAll("\\s+", ""));

                    fp_2014_eps.setText(result[32]);
                    fp_2014_nav.setText(result[33]);
                    fp_2014_npat_co.setText(result[34].replaceAll("\\s+", ""));
                    fp_2014_npat_eo.setText(result[35].replaceAll("\\s+", ""));

                    dividend_2009.setText(result[36]);
                    dividend_2010.setText(result[37]);
                    dividend_2011.setText(result[38]);
                    dividend_2012.setText(result[39]);
                    dividend_2013.setText(result[40]);
                    dividend_2014.setText(result[41]);
                    sp_director.setText(result[42]);
                    sp_govt.setText(result[43]);
                    sp_institute.setText(result[44]);
                    sp_foreign.setText(result[45]);
                    sp_public.setText(result[46]);
                    change.setText(result[22]);
                    //change.setText(change_percentage);
                    // change_value.setText(change_amout);
                    ItemInfo.this.change_value.setText(result[21]);
                    boolean not_traded = false;
                    float change_value;
                    try {
                        change_value = Float.parseFloat(result[21]);
                        //change_value = Float.parseFloat(change_amout);
                    } catch (Exception e) {
                        change_value = -1;
                        not_traded = true;
                    }
                    if (not_traded) {
                        change.setText("--");
                        ItemInfo.this.change_value.setText("--");
                        lasttrade.setText("--");
                        if (portfolioButton.getText().toString()
                                .equals(addtoportfolio)) {
                            portfolioButton.setEnabled(false);
                        }
                        if (watchlistButton.getText().toString()
                                .equals(addedinwatchlist)) {
                            watchlistButton.setEnabled(false);
                        }

                    }

                    if (change_value < 0) {
                        change_percentage_layout
                                .setBackgroundDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.border_drawer_red_percentage));
                    } else {
                        change_percentage_layout
                                .setBackgroundDrawable(getResources()
                                        .getDrawable(
                                                R.drawable.border_drawer_green_percentage));
                    }

                }

            }

        }

        @Override
        protected String[] doInBackground(String... params) {

            SrcGrabber grabber = new SrcGrabber();

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    json_data = grabber.grabSource(item_detail_link
                            + tradingcode + ".txt");

                    hasactivenet = true;
                    if (DevTools.fileExistance(ItemInfo.this, tradingcode)) {
                        DevTools.write_file(ItemInfo.this, tradingcode,
                                json_data);
                        fileexists = true;
                    }

                }

                else {
                    if (DevTools.fileExistance(ItemInfo.this, tradingcode)) {
                        json_data = DevTools.read_file(ItemInfo.this,
                                tradingcode);
                        hasactivenet = false;
                        fileexists = true;

                    } else {
                        hasactivenet = false;
                        return null;

                    }

                }

                JSONArray item_info_array = new JSONArray(json_data);
                JSONObject item_info_object = item_info_array.getJSONObject(0);

                // Parse json and assign to string variable
                String[] parsed_data = {
                        item_info_object.getString("data1"),
                        item_info_object.getString("data2"),
                        item_info_object.getString("data3"),
                        item_info_object.getString("data4"),
                        item_info_object.getString("data5"),
                        item_info_object.getString("data6"),
                        item_info_object.getString("data7"),
                        item_info_object.getString("data8"),
                        item_info_object.getString("data9"),
                        item_info_object.getString("data10"),
                        item_info_object.getString("data11"),
                        item_info_object.getString("data12"),
                        item_info_object.getString("data13"),
                        item_info_object.getString("data14"),
                        item_info_object.getString("data15"),
                        item_info_object.getString("data16"),
                        item_info_object.getString("data17"),
                        item_info_object.getString("data18"),
                        item_info_object.getString("data19"),
                        item_info_object.getString("data20"),
                        item_info_object.getString("data21"),
                        item_info_object.getString("data22"),
                        item_info_object.getString("data23"),
                        item_info_object.getString("data24"),
                        item_info_object.getString("p_e_ratio_basic"),
                        item_info_object.getString("p_e_ratio_diluted"),
                        item_info_object.getString("value(mn)"),
                        item_info_object.getString("marketcatagory"),
                        item_info_object
                                .getString("fp2013_epscontinueoperation"),
                        item_info_object.getString("fp2013_NAV"),
                        item_info_object
                                .getString("fp2013_NPATcontinueoperation"),
                        item_info_object
                                .getString("fp2013_NPATextraordinaryincome"),

                        item_info_object
                                .getString("fp2014_epscontinueoperation"),
                        item_info_object.getString("fp2014_NAV"),
                        item_info_object
                                .getString("fp2014_NPATcontinueoperation"),
                        item_info_object
                                .getString("fp2014_NPATextraordinaryincome"),
                        item_info_object.getString("fpcontinue_dividend_2009"),
                        item_info_object.getString("fpcontinue_dividend_2010"),
                        item_info_object.getString("fpcontinue_dividend_2011"),
                        item_info_object.getString("fpcontinue_dividend_2012"),
                        item_info_object.getString("fpcontinue_dividend_2013"),
                        item_info_object.getString("fpcontinue_dividend_2014"),
                        item_info_object.getString("sp_sponsor_director"),
                        item_info_object.getString("sp_govt"),
                        item_info_object.getString("sp_institute"),
                        item_info_object.getString("sp_foreign"),
                        item_info_object.getString("sp_public")

                };

                return parsed_data;

            } catch (JSONException | IOException | URISyntaxException e) {

            }

            return null;
        }

    }

}
