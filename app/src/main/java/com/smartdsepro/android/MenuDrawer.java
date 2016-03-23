package com.smartdsepro.android;

import android.app.Activity;

import com.mikepenz.materialdrawer.DrawerBuilder;

/**
 * Created by mashnoor on 1/1/16.
 */
public class MenuDrawer {

    public MenuDrawer(Activity sentActivity)
    {
        new DrawerBuilder().withActivity(sentActivity).build();
    }




}
