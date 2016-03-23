
package com.smartdsepro.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


public class About extends Activity {

    Button reset_cache, dse_site, facebook_like,
            smartdse_site, check_update;
    final static String db_name = "dsedatabase";
    final static String DSE_SITE = "http://www.dsebd.org";
    ButtonController buttonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_about);
        buttonController = new ButtonController(About.this);

        smartdse_site = (Button) findViewById(R.id.btn_smartdse_site);
        reset_cache = (Button) findViewById(R.id.btn_reset_cache);
        //Update Button
        check_update = (Button) findViewById(R.id.btn_update_smartdse);
        check_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=" + getApplicationContext().getPackageName())));


            }
        });
        //reset_portfolio_watchlist = (Button) findViewById(R.id.btn_reset_watchlist_portfolio);

        dse_site = (Button) findViewById(R.id.btn_dse_site);
        facebook_like = (Button) findViewById(R.id.btn_facebook);


        // Facebook Like
        facebook_like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                opensite("https://www.facebook.com/smartdseapp");

            }
        });

        // SmartDSE Site
        smartdse_site.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                opensite("http://www.smartdse.com");

            }
        });

        // DSE Official Site
        dse_site.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                opensite(DSE_SITE);

            }

        });


        reset_cache.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder reset_alert_builder = new AlertDialog.Builder(About.this);
                String message = "This will reset all your portfolio, watchlist and cached data.";
                reset_alert_builder.setMessage(message);
                reset_alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String[] files_list = getFilesDir().list();
                        int list_length = files_list.length;
                        for (int i = 0; i < list_length; i++) {
                            deleteFile(files_list[i]);
                        }
                        deleteDatabase(db_name);
                        Toast.makeText(About.this,
                                "All data has been reset successfully!",
                                Toast.LENGTH_LONG).show();

                    }
                });
                reset_alert_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                reset_alert_builder.setTitle("Are You Sure");

                AlertDialog reset_alert = reset_alert_builder.create();
                reset_alert.show();
            }
        });


    }



    private void opensite(String Site) {
        Uri uri = Uri.parse(Site);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

}