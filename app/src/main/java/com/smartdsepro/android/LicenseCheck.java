package com.smartdsepro.android;
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Welcome to the world of Android Market licensing. We're so glad to have you
 * onboard!
 * <p>
 * The first thing you need to do is get your hands on your public key.
 * Update the BASE64_PUBLIC_KEY constant below with your encoded public key,
 * which you can find on the
 * <a href="http://market.android.com/publish/editProfile">Edit Profile</a>
 * page of the Market publisher site.
 * <p>
 * Log in with the same account on your Cupcake (1.5) or higher phone or
 * your FroYo (2.2) emulator with the Google add-ons installed. Change the
 * test response on the Edit Profile page, press Save, and see how this
 * application responds when you check your license.
 * <p>
 * After you get this sample running, peruse the
 * <a href="http://developer.android.com/guide/publishing/licensing.html">
 * licensing documentation.</a>
 */
public class LicenseCheck extends Activity {
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmsFCFOsMsgO7guAs55VVNU2Xw6gkXPSOmD7246J5SrjH2LHOvX0K3wThJuJKmjqVL8tIaRIPfJRUlLbal5CrS/yCI2PxFqUTi+m2DpMYbvXMzmEq+IYYyL9JgdI2XfyFG/BiaBWXe6cNi44uJgXGaPG5P4Hg/B5+X95SmIrQb2/K2sJl1gGZR9MV3FA8JxH6SVAuMKNGYV5dyHggFStvp2FoRIiVONsc721FadLqUExDUyPzIdygvb5H7/Q6YEbSI+OISkcTFZ6L87VCMn2eO8kvoqy/214O0oBaUljYf21VMgucouuw7Nv16YHzT5mpdhhC53IWhrzvamsSqztvYwIDAQAB";

    // Generate your own 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[] {
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
            89
    };
    SharedPreferences settings;
    private boolean isLicenseActivate()
    {

        if (settings.getBoolean("license_active", false)) {
            //Licence Not Actice
            return false;
        }
        else
        {
            return true;
        }

    }


    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    // A handler on the UI thread.
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.splash_activity);
        settings =  getSharedPreferences(Constants.SHARED_PREF_NAME, 0);




        mHandler = new Handler();

        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // Library calls this when it's done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker(
                this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY);
        if(isLicenseActivate())
        {
            doCheck();

        }
        else
        {

            Intent i = new Intent(LicenseCheck.this, Splash_activity.class);
            startActivity(i);
            finish();
        }

    }

    protected Dialog onCreateDialog(int id) {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
                .setTitle("License Manager")
                .setMessage(bRetry ? "Can't connect to the internet." : "This copy of SmartDSE Pro is not licensed!")
                .setPositiveButton(bRetry ? "Retry" : "Buy", new DialogInterface.OnClickListener() {
                    boolean mRetry = bRetry;
                    public void onClick(DialogInterface dialog, int which) {
                        if ( mRetry ) {
                            doCheck();
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).
                        create();
    }

    private void doCheck() {

        setProgressBarIndeterminateVisibility(true);

        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    private void displayResult(final String result) {

        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(LicenseCheck.this, result, Toast.LENGTH_LONG).show();
                setProgressBarIndeterminateVisibility(false);

            }
        });
    }

    private void displayDialog(final boolean showRetry) {
        mHandler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                showDialog(showRetry ? 1 : 0);

            }
        });
    }

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int policyReason) {
            //Set that licence is active

            settings.edit().putBoolean("license_active", true).apply();
            finish();
            Intent i = new Intent(LicenseCheck.this, Splash_activity.class);
            startActivity(i);
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            displayResult("allow");
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            settings.edit().putBoolean("license_active", false).apply();
           // displayResult("Not valid license!");
            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            // If the reason for the lack of license is that the service is
            // unavailable or there is another problem, we display a
            // retry button on the dialog and a different message.
            displayDialog(policyReason == Policy.RETRY);
        }

        public void applicationError(int errorCode) {

            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.

            displayResult("Error Occured! Please restart app.");
            finish();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
    }

}