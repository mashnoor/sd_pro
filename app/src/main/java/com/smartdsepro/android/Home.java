package com.smartdsepro.android;

/*This Class is used for showing the main screen data. That means dsex, dses, dse30 datas.
 * We have to store last retrived datas in file. So we created two methods for reading and writin.
 * 
 * 
 * 
 * */

import java.io.IOException;
import java.net.URISyntaxException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;

import com.mikepenz.materialdrawer.DrawerBuilder;

public class Home extends Activity {

    // All Text Views

    TextView tvdsex_index_data1, tvdsex_index_data2, tvdsex_index_data3,
            tvdses_index_data1, tvdses_index_data2, tvdses_index_data3,
            tvdse30_index_data1, tvdse30_index_data2, tvdse30_index_data3,
            tvdse_total_trade, tvdse_total_volume, tvdse_total_value,
            tvdse_market_status, tvdse_lastupdate, tvdse_issue_advanced,
            tvdse_issue_declined, tvdse_issue_unchanged, header_server_data;
    LinearLayout dsex_index_Layout, dse30_index_Layout, dsex_whole, ds30_whole, dsex_graph_layout, volume_graph_layout;

    ButtonController buttonController;
    GraphDrawer home_graph;
    Button show_graph;


    Handler handler;
    Thread thread;
    int i = 0;

    boolean thread_flag = false;

    // This variable is representing the AsyncTask.. This task will load our
    // data
    private set_dse_home_datas data_fetching_task;

    // Name of our file where we store our offline data
    final static String filename = "DSEX_DSES_DS30_INFOS";

    // Defining a boolean where we can store weather there is an active internet
    // connection or not
    boolean has_active_netconnection = true;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home);


        //download();

        //Test


        initialize_variable();

//        dsex_graph_layout.setVisibility(View.INVISIBLE);
        //      volume_graph_layout.setVisibility(View.INVISIBLE);

        new DrawerBuilder().withActivity(this).build();


        buttonController = new ButtonController(Home.this);

        //Show Graph


        home_graph = new GraphDrawer(this);
        // home_graph.drawHomeGraph();

        // Defining our asynctask manager
        data_fetching_task = (set_dse_home_datas) new set_dse_home_datas();
        data_fetching_task.execute("");
        final Animation animation, animation2;
        animation = AnimationUtils.loadAnimation(Home.this, R.anim.fade_out);
        animation2 = AnimationUtils.loadAnimation(Home.this, R.anim.fade_in);
        thread = new Thread(new header_text_thread());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                if (msg.arg1 >= ButtonController.header_texts.size()) {
                    return;
                }
                Spannable s = (Spannable) Html
                        .fromHtml(ButtonController.header_texts.get(msg.arg1));
                for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
                    s.setSpan(new UnderlineSpan() {
                        public void updateDrawState(TextPaint tp) {
                            tp.setUnderlineText(false);
                        }
                    }, s.getSpanStart(u), s.getSpanEnd(u), 0);
                }
                header_server_data.startAnimation(animation);

                header_server_data.setText(s);

                header_server_data.setMovementMethod(LinkMovementMethod
                        .getInstance());
                header_server_data.startAnimation(animation2);
            }
        };
        layoutclicklistener();

    }






    class header_text_thread implements Runnable {

        @Override
        public void run() {
            if (ButtonController.header_texts != null) {

                int k = ButtonController.header_texts.size();

                thread_flag = true;
                while (thread_flag) {
                    if (i == k) {
                        i = 0;
                    }
                    Message message = Message.obtain();
                    message.arg1 = i;
                    handler.sendMessage(message);
                    i++;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    // We are moving to item list where whenever a user click dsex or dses
    // layout. So we are
    // using a on click listener
    private void layoutclicklistener() {
        final Intent itemlistIntent = new Intent(Home.this, MainActivity.class);
        dsex_index_Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                itemlistIntent.putExtra("DSEX", "DSEX");

                startActivity(itemlistIntent);

            }
        });

        dse30_index_Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent dse30listintent = new Intent(Home.this, DSE30List.class);
                startActivity(dse30listintent);

            }
        });
        dsex_whole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                itemlistIntent.putExtra("DSEX", "DSEX");
                startActivity(itemlistIntent);

            }
        });

        ds30_whole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Intent dse30listintent = new Intent(Home.this, DSE30List.class);
                //startActivity(dse30listintent);

            }
        });

        show_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent fullScreenGraph = new Intent(Home.this, FullScreenGraph.class);
                startActivity(fullScreenGraph);
                /***
                 dsex_graph_layout.setVisibility(View.VISIBLE);
                 volume_graph_layout.setVisibility(View.VISIBLE);
                 home_graph.drawHomeGraph();
                 ***/


            }
        });

    }

    // Describing a function to read data from offline file

    // Describing a function to storing our data in offline file

    // Initialize all variables
    private void initialize_variable() {
        // DSEX Text Views
        tvdsex_index_data1 = (TextView) findViewById(R.id.dsex_data_1);
         tvdsex_index_data2 = (TextView) findViewById(R.id.dsex_data_2);
        tvdsex_index_data3 = (TextView) findViewById(R.id.dsex_data_3);

        // DSE30 Text Views
        tvdse30_index_data1 = (TextView) findViewById(R.id.dse30_data_1);
        tvdse30_index_data2 = (TextView) findViewById(R.id.dse30_data_2);
        tvdse30_index_data3 = (TextView) findViewById(R.id.dse30_data_3);

        // Market Status, Last Update
        tvdse_market_status = (TextView) findViewById(R.id.dse_marketstatus);
        // tvdse_lastupdate = (TextView) findViewById(R.id.dse_lastupdate);

        // Total trade, volume, value textviews
        tvdse_total_trade = (TextView) findViewById(R.id.dse_total_trade);
        tvdse_total_volume = (TextView) findViewById(R.id.dse_total_volume);
        tvdse_total_value = (TextView) findViewById(R.id.dse_total_value);

        // Issues
        tvdse_issue_advanced = (TextView) findViewById(R.id.dse_issue_advanced);
        tvdse_issue_declined = (TextView) findViewById(R.id.dse_issue_declined);
        tvdse_issue_unchanged = (TextView) findViewById(R.id.dse_issue_unchanged);

        // Layouts
        dsex_index_Layout = (LinearLayout) findViewById(R.id.dse_x_layout);
        // dses_index_layLayout = (LinearLayout) findViewById(R.id.dse_s);
        dse30_index_Layout = (LinearLayout) findViewById(R.id.ds_30_layout);
        dsex_whole = (LinearLayout) findViewById(R.id.dse_x);
        ds30_whole = (LinearLayout) findViewById(R.id.dse_30);
        //dsex_graph_layout = (LinearLayout) findViewById(R.id.dsex_graph_layout);
        //volume_graph_layout = (LinearLayout) findViewById(R.id.volume_graph_layout);

        //Buttons
        show_graph = (Button) findViewById(R.id.dse_show_graph);

        header_server_data = (TextView) findViewById(R.id.header_server_text);

    }

    public class set_dse_home_datas extends AsyncTask<String, String, String[]> {




        ProgressDialog progressDialog = ProgressDialog.show(Home.this, "",
                "Retrieving Data...", true);






        @Override
        protected void onPostExecute(String[] result) {

            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            // A thread for showing header text....

            // For testing purpose

            thread.start();



            if (!has_active_netconnection) {
                Toast.makeText(getApplicationContext(),
                        "Can't connect to server! Check your internet.",
                        Toast.LENGTH_LONG).show();
            }
            if (!(result == null)) {

                if (result[1].equals("null")) {
                    result[1] = "0";
                }
                if (result[4].equals("null")) {
                    result[4] = "0";
                }
                // DSEX
                tvdsex_index_data1.setText(result[0]);
                tvdsex_index_data2.setText(result[1]);
                tvdsex_index_data3.setText(result[2]);

                // DSE30
                tvdse30_index_data1.setText(result[3]);
                tvdse30_index_data2.setText(result[4]);
                tvdse30_index_data3.setText(result[5]);

                // Total Datas
                tvdse_total_trade.setText(result[6]);
                tvdse_total_volume.setText(result[7]);
                tvdse_total_value.setText(result[8]);

                // Lastupdate, Marketstatus
                tvdse_market_status.setText(result[9]);
                // tvdse_lastupdate.setText(result[10]);

                // Issues
                tvdse_issue_advanced.setText(result[11]);
                tvdse_issue_declined.setText(result[12]);
                tvdse_issue_unchanged.setText(result[13]);

                double neg_pos_definer_dsex = Double.parseDouble(result[1]);

                double neg_pos_definer_dse30 = Double.parseDouble(result[4]);

                // Set Color for border DSEX

                if (neg_pos_definer_dsex < 0) {
                    // dsex_index_Layout.setBackground(getResources().getDrawable(
                    // R.drawable.layout_bg_2));
                    dsex_index_Layout.setBackgroundDrawable(getResources()
                            .getDrawable(
                                    R.drawable.border_drawer_red_percentage));

                } else if (neg_pos_definer_dsex >= 0) {
                    // dsex_index_Layout.setBackground(getResources().getDrawable(
                    // R.drawable.layout_bg_2));
                    dsex_index_Layout.setBackgroundDrawable(getResources()
                            .getDrawable(
                                    R.drawable.border_drawer_green_percentage));

                }
                // Set Color for border DS30
                if (neg_pos_definer_dse30 < 0) {
                    // dse30_index_Layout.setBackground(getResources()
                    // .getDrawable(R.drawable.layout_bg_2));
                    dse30_index_Layout.setBackgroundDrawable(getResources()
                            .getDrawable(
                                    R.drawable.border_drawer_red_percentage));

                } else if (neg_pos_definer_dse30 >= 0) {
                    // dse30_index_Layout.setBackground(getResources()
                    // .getDrawable(R.drawable.layout_bg_2));
                    dse30_index_Layout.setBackgroundDrawable(getResources()
                            .getDrawable(
                                    R.drawable.border_drawer_green_percentage));

                }

            }
        }

        @Override
        protected String[] doInBackground(String... arg0) {




            String home_data = "";
            SrcGrabber grabber = new SrcGrabber();



            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    home_data = grabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/homedata.txt");

                    System.out.println(home_data);
                    DevTools.write_file(Home.this, filename, home_data);
                    //DevTools.write_file(Home.this, );

                    has_active_netconnection = true;
                } else if (DevTools.fileExistance(Home.this, filename)) {
                    home_data = DevTools.read_file(Home.this, filename);
                    has_active_netconnection = false;

                } else {
                    has_active_netconnection = false;
                    return null;
                }

            } catch (IOException | URISyntaxException e) {

            }

            try {

                JSONArray dse_home_datas = new JSONArray(home_data);


                // Json Objects
                JSONObject dsex_index_object = dse_home_datas.getJSONObject(0);
                JSONObject dse30_index_Object = dse_home_datas.getJSONObject(2);
                JSONObject totaldetails = dse_home_datas.getJSONObject(3);
                JSONObject marketstatus = dse_home_datas.getJSONObject(5);
                JSONObject lastupdate = dse_home_datas.getJSONObject(6);
                JSONObject dseissues = dse_home_datas.getJSONObject(4);

                // Set Datas
                String dsex_data1, dsex_data2, dsex_data3, dse30_data1, dse30_data2, dse30_data3, dsetotaltrade, dsetotalvolume, dsetotalvalue, dsemarketstatus, dselastupdate, dseissueadvanced, dseissuedeclined, dseissueunchanged;

                // DSEX Datas
                dsex_data1 = dsex_index_object.getString("data1");
                dsex_data2 = dsex_index_object.getString("data2");
                dsex_data3 = dsex_index_object.getString("data3");

                // DSE30 Data
                dse30_data1 = dse30_index_Object.getString("data1");
                dse30_data2 = dse30_index_Object.getString("data2");
                dse30_data3 = dse30_index_Object.getString("data3");

                // Total Details
                dsetotaltrade = totaldetails.getString("ttrade");
                dsetotalvolume = totaldetails.getString("tvolume");
                dsetotalvalue = totaldetails.getString("tvalue");

                // Market Satus and last update
                dsemarketstatus = marketstatus.getString("marketstatus")
                        + " | " + lastupdate.getString("lastupdate");
                ButtonController.update_status = dsemarketstatus;
                dselastupdate = lastupdate.getString("lastupdate");

                // Issues
                dseissueadvanced = dseissues.getString("iadvanced");
                dseissuedeclined = dseissues.getString("ideclined");
                dseissueunchanged = dseissues.getString("iunchanged");

                String[] alldatas = { dsex_data1, dsex_data2, dsex_data3,
                        dse30_data1, dse30_data2, dse30_data3, dsetotaltrade,
                        dsetotalvolume, dsetotalvalue, dsemarketstatus,
                        dselastupdate, dseissueadvanced, dseissuedeclined,
                        dseissueunchanged };
                return alldatas;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

    }

}
