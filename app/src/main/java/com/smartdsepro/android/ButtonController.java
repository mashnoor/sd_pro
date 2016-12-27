package com.smartdsepro.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class ButtonController {

    private boolean sentfromportfolioactivity = false;
    public static String update_status = "Not Updated Yet";
    //Header Text for Version 2.0.0 (Beta Release)
    final static String header_text_link = "http://104.131.22.246/dev/smartdsefiles/header_text_2.5.1.txt";
    final static String ADV_FILE_NAME = "advertise";
    public static ArrayList<String> header_texts;
    Handler handler;
    Thread thread;
    int i = 0;
    public static Activity mainActivity;



    private volatile boolean thread_flag = false;


    class header_text_thread implements Runnable {

        @Override
        public void run() {
            if (header_texts != null) {

                int k = header_texts.size();
                thread_flag = true;
                while (thread_flag) {
                    if (i == k) {
                        i = 0;
                    }
                    Message message = Message.obtain();
                    message.arg1 = i;
                    handler.sendMessage(message);
                    ++i;
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




    public ButtonController(final Activity sentactivity) {


        final View logo, home, items, search, watchlist, portfolio, news, top20, gainerloser, close_app, rateus, refresh_button, menuicon;
        final TextView update_status_tv;
        refresh_button = sentactivity.findViewById(R.id.dse_home_refresh);
        //For Advertisement

        mainActivity = sentactivity;


        //Adding the Drawer
        new DrawerBuilder().withActivity(sentactivity).build();
        //Adding the items... :D
        PrimaryDrawerItem home_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.home)).withName("Home");
        PrimaryDrawerItem a_z_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.itemlist)).withName("All Items");
        PrimaryDrawerItem search_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.search)).withName("Search");
        PrimaryDrawerItem watchlist_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.watchlist)).withName("Watchlist");
        PrimaryDrawerItem portfolio_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.portfolio)).withName("Portfolio");
        PrimaryDrawerItem news_and_ipo_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.news)).withName("News IPO AGM");
        PrimaryDrawerItem top20_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.top20)).withName("Top 20");
        PrimaryDrawerItem gainerloser_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.gainerloser)).withName("Top Gainer Loser");
        PrimaryDrawerItem currency_convert_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.currency)).withName("Currency Conversion");
        PrimaryDrawerItem price_alert = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.pricealert)).withName("Price Alert");

        PrimaryDrawerItem rateus_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.rateus)).withName("Rate Us");
        PrimaryDrawerItem info_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.info)).withName("Info");
        PrimaryDrawerItem quit_item = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.quit)).withName("Quit");
        PrimaryDrawerItem expert_analysis = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.expert_analysis_logo)).withName("Expert Analysis");

        PrimaryDrawerItem group_discussion = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.group_chat_logo)).withName("Group Discussion");
        PrimaryDrawerItem weekly_report = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.weekly_report_logo)).withName("Weekly Report");
        PrimaryDrawerItem like_us = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.fb_logo)).withName("Like Us");
        PrimaryDrawerItem login_logout = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.login_logout)).withName("Login/Logout");
        PrimaryDrawerItem stock_on_news = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.stockmarketonnewspaper)).withName("Financial News");
        PrimaryDrawerItem sd_pro = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.sd_pro)).withName("Faster & Ads Free!");
        PrimaryDrawerItem sync_data_server = new PrimaryDrawerItem().withIcon(sentactivity.getResources().getDrawable(R.drawable.restore)).withName("Backup and Restore Data");



//create the drawer and remember the `Drawer` result object
         int clickedPosition;
        final Drawer result = new DrawerBuilder()

                .withActivity(sentactivity)

                .addDrawerItems(
                        home_item, //0
                        a_z_item,  //1
                        search_item, //2
                        watchlist_item,//3
                        portfolio_item,//4
                        price_alert,//5
                        expert_analysis,//6
                        group_discussion,//7
                        weekly_report,//8
                        news_and_ipo_item,//9
                        currency_convert_item,//10
                        gainerloser_item,//11
                        top20_item,//12
                        stock_on_news,//13
                        like_us,//14
                        info_item,//15
                        login_logout,//16

                        sync_data_server,//17
                        rateus_item,//18
                        quit_item//19


                        //new SecondaryDrawerItem().withName(R.string.drawer_item_settings)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        //Handle the clicks
                        if (position == 0)
                        {
                            //Home
                            MainActivity.show = false;
                            Intent homeiIntent = new Intent(sentactivity, Home.class);
                            sentactivity.startActivity(homeiIntent);



                        }
                      else if(position == 1)
                        {
                            //A2Z
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    MainActivity.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if(position == 2)

                        {

                            //Search
                            if (sentactivity instanceof MainActivity) {
                                View search_box = sentactivity
                                        .findViewById(R.id.dse_list_search_box);


                                int search_box_visibility = search_box.getVisibility();
                                if (search_box_visibility == View.GONE) {
                                    InputMethodManager imm = (InputMethodManager) sentactivity
                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                                    Animation animation;
                                    animation = AnimationUtils.loadAnimation(sentactivity,
                                            R.anim.fade_in);
                                    sentactivity.findViewById(R.id.dse_list_search_box)
                                            .setAnimation(animation);
                                    sentactivity.findViewById(R.id.dse_list_search_box)
                                            .setVisibility(View.VISIBLE);
                                    search_box.requestFocus();
                                    imm.showSoftInput(search_box, 0);


                                } else if (search_box_visibility == View.VISIBLE) {
                                    InputMethodManager imm = (InputMethodManager) sentactivity
                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                                    Animation animation;
                                    animation = AnimationUtils.loadAnimation(sentactivity,
                                            R.anim.fade_out);
                                    sentactivity.findViewById(R.id.dse_list_search_box)
                                            .setAnimation(animation);
                                    sentactivity.findViewById(R.id.dse_list_search_box)
                                            .setVisibility(View.GONE);

                                    imm.hideSoftInputFromWindow(
                                            search_box.getWindowToken(), 0);
                                }

                            } else {
                                Intent itemintent = new Intent(sentactivity,
                                        MainActivity.class);
                                sentactivity.startActivity(itemintent);
                                MainActivity.show = true;
                            }

                        }
                        else if(position == 3)
                        {
                            //Watchlist
                            MainActivity.show = false;
                            Intent watchlist = new Intent(sentactivity,
                                    Watch_List_list_view.class);
                            sentactivity.startActivity(watchlist);

                        }
                        else if (position == 4)
                        {
                            //Portfolio
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    Portfolio_activity.class);
                            sentactivity.startActivity(itemintent);

                        }
                        else if (position == 5)
                        {
                            //Price Alert
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    PriceAlert.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if (position == 6)
                        {
                            //Expert Analysis
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    ExpertAnalysis.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if (position == 7)
                        {
                            //Group Discussion

                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    Chat.class);
                            sentactivity.startActivity(itemintent);

                        }
                        else if(position == 8)
                        {
                            //Weekly Report
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    WeeklyReport.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if (position == 9)
                        {
                            //News IPO AGM
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    NewsActivity.class);
                            sentactivity.startActivity(itemintent);
                        }

                        else if (position == 10)
                        {
                            //Currency Converter
                            MainActivity.show = false;
                            Intent expert_analysis_intent = new Intent(sentactivity,
                                    Currency.class);
                            sentactivity.startActivity(expert_analysis_intent);
                        }
                        else if (position == 11)
                        {
                          //Top Gainer Loser
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    Top10Gainers.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if (position == 12)
                        {
                           //TOP 20
                            MainActivity.show = false;
                            Intent itemintent = new Intent(sentactivity,
                                    Top10Shares.class);
                            sentactivity.startActivity(itemintent);
                        }
                        else if (position == 13)
                        {
                            //Stock on Newspaper
                            MainActivity.show = false;
                            Intent intent = new Intent(sentactivity, StockOnNewsPaper.class);
                            sentactivity.startActivity(intent);


                        }


                        else if (position == 14)
                        {
                            //Like Us
                            try {
                                sentactivity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                                Intent fb_app = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/790664287641127"));
                                sentactivity.startActivity(fb_app);
                            } catch (Exception e) {
                               Intent i =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/790664287641127"));
                                sentactivity.startActivity(i);
                            }
                        }
                        else if(position == 15)
                        {
                            //Info
                            MainActivity.show = false;
                            Intent intent = new Intent(sentactivity, About.class);
                            sentactivity.startActivity(intent);




                        }
                        else if (position == 16)
                        {
                            //Login Logout
                            MainActivity.show = false;
                            Intent intent = new Intent(sentactivity, Login_logout.class);
                            sentactivity.startActivity(intent);

                        }


                        else if(position == 18)
                        {
                            //Rate Us
                            sentactivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("market://details?id=" + sentactivity.getApplicationContext().getPackageName())));


                        }
                        else if(position == 19)
                        {

                            //Exit


                            sentactivity.finish();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            sentactivity.startActivity(intent);

                        }
                        else if(position == 17) {

                            //Sync
                            if (LoginHelper.getUserName(sentactivity).equals(Constants.LOGIN_NAME_NOT_SET)) {
                                Toast.makeText(sentactivity, "You need to Login to perform this operation!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(sentactivity, Login_logout.class);
                                sentactivity.startActivity(intent);
                            } else {
                                LayoutInflater inflater = sentactivity.getLayoutInflater();
                                View workingview = inflater.inflate(R.layout.backup_restore, null);
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(sentactivity);
                                alertBuilder.setView(workingview);
                                alertBuilder.show();
                                final Button backup_button = (Button) workingview.findViewById(R.id.btn_backup);
                                Button restore_button = (Button) workingview.findViewById(R.id.btn_restore);
                                restore_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BackupRestore backupRestore = new BackupRestore(sentactivity);
                                        backupRestore.restore();
                                    }
                                });
                                backup_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BackupRestore backupRestore = new BackupRestore(sentactivity);
                                        backupRestore.backup();
                                       // Toast.makeText(sentactivity, "Lol", Toast.LENGTH_LONG).show();

                                    }
                                });

                            }
                        }





                      return  true;
                    }
                })
                .build();



        logo = sentactivity.findViewById(R.id.sdse_logo);
        menuicon = sentactivity.findViewById(R.id.menu_icon);
        update_status_tv = (TextView) sentactivity
                .findViewById(R.id.dse_marketstatus);


        update_status_tv.setText(update_status);



        // Handler for showing advertisement
        if (!(sentactivity instanceof Home)) {
            final Animation animation, animation2;
            animation = AnimationUtils.loadAnimation(sentactivity,
                    R.anim.fade_out);
            animation2 = AnimationUtils.loadAnimation(sentactivity,
                    R.anim.fade_in);
            thread = new Thread(new header_text_thread());
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    if (msg.arg1 == header_texts.size()) {
                        return;
                    } else if (header_texts.isEmpty()) {
                        return;
                    } else if (msg.arg1 > (header_texts.size() - 1)) {
                        return;
                    }
                    Spannable s = null;
                    try {
                        s = (Spannable) Html
                                .fromHtml(ButtonController.header_texts
                                        .get(msg.arg1));
                    } catch (Exception e) {
                        return;
                    }

                    for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
                        s.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, s.getSpanStart(u), s.getSpanEnd(u), 0);
                    }
                    update_status_tv.startAnimation(animation);

                    update_status_tv.setText(s);

                    update_status_tv.setMovementMethod(LinkMovementMethod
                            .getInstance());
                    update_status_tv.startAnimation(animation2);
                }
            };
            thread.start();

        }

        // Logo

        if (!(sentactivity instanceof Home)) {
            logo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    MainActivity.show = false;
                    Intent homeiIntent = new Intent(sentactivity, Home.class);
                    sentactivity.startActivity(homeiIntent);
                    ;

                }
            });

        }
        //Menu Click Listener
        menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                result.openDrawer();

            }
        });


	        // Refresh Button
        refresh_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                thread_flag = false;
                Intent refreshIntent = sentactivity.getIntent();
                sentactivity.startActivity(refreshIntent);
                sentactivity.finish();

            }
        });

    }

    public boolean isSentfromportfolioactivity() {
        return sentfromportfolioactivity;
    }

    public void setSentfromportfolioactivity(boolean sentfromportfolioactivity) {
        this.sentfromportfolioactivity = sentfromportfolioactivity;
    }

}