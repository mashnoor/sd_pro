package com.smartdse.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;

/**
 * Created by mashnoor on 1/18/16.
 */
public class PriceAlertHelper {


    final static String BUTTON_TEXT = "Add";



    public static void addPriceAlert(final Activity activity, final String item_name, final String ltp)
    {
        //Create The Builder
        AlertDialog.Builder addPriceAlertDialogueBuilder  = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View workingView = inflater.inflate(R.layout.price_alert_dialog, null);
        addPriceAlertDialogueBuilder.setView(workingView);
        final TextView tvhigh = (TextView) workingView.findViewById(R.id.price_alert_high);
        final TextView tvlow = (TextView) workingView.findViewById(R.id.price_alert_low);

        addPriceAlertDialogueBuilder.setPositiveButton(BUTTON_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check if the fields are blank
                if (fieldsBlank(tvhigh, tvlow)) {
                    //If Blank, Show a error msg
                    showDialog(activity, "Error", "Some fields are blank!", "Ok");
                    return;
                }
                //Check if the input numbers are correct
                else if (inputNotValid(tvhigh, tvlow)) {
                    showDialog(activity, "Error", "Something wrong with your input data. Please check!", "Ok");
                    return;
                }
                //All Corrcet
                else {
                    //Add to database
                    addToDatabase(activity, item_name, tvhigh.getText().toString(), tvlow.getText().toString(), ltp);


                    //Show Success Message
                    showSuccessfulAddDialog(activity, "You have successfully added " + item_name + " to your price alert.");

                }


            }
        });




        AlertDialog final_dialog = addPriceAlertDialogueBuilder.create();
        final_dialog.show();

    }

    private static void addToDatabase(Activity activity, String item_name, String high, String low, String ltp) {
        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Context.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String db_insert_query = "INSERT INTO " + Constants.PRICE_ALERT_TABLE + " VALUES ('" +
                item_name + "', '"+
                high + "', '" +
                low + "', '" +
                ltp + "'" +
                ");";
        dsebd.execSQL(db_insert_query);



    }

    private static void showSuccessfulAddDialog(final Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                activity)
                .setTitle("Success!")
                .setMessage(
                        message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface arg0,
                                    int arg1) {
                                restart_activity(activity);

                            }
                        }).setCancelable(false).show();
    }
    private static void restart_activity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.startActivity(intent);
        activity.finish();
    }

    private static boolean inputNotValid(TextView tvhigh, TextView tvlow) {
        String high = tvhigh.getText().toString();
        String low = tvlow.getText().toString();
        if (Double.parseDouble(high) <= 0 | Double.parseDouble(low) <= 0)
        {
            return true;
        }

        return false;
    }

    private static void showDialog(Activity activity, String title, String msg, String positiveBtnTxt) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveBtnTxt, null).show();
    }

    private static boolean fieldsBlank(TextView tvhigh, TextView tvlow) {

        if (tvhigh.getText().toString().equals("") | tvlow.getText().toString().equals(""))
        {
            return true;
        }
        return false;

    }

    public static boolean ifItemExists(Context activity, String item)
    {


        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String search_string = "SELECT * FROM " + Constants.PRICE_ALERT_TABLE + " WHERE item='" + item + "';";
        Cursor item_cursor = dsebd.rawQuery(search_string, null);

        if (item_cursor.getCount()>0)
        {
            item_cursor.close();
            dsebd.close();
            return true;
        }
        item_cursor.close();
        dsebd.close();
        return false;

    }



    public static void show_customize_price_alert_dialog(final Activity activity, final String item)
    {

        String[] dialog_items = {"Set new range", "Remove from price alert"};
        AlertDialog.Builder price_alert_cust_dialog = new AlertDialog.Builder(
                activity);

        price_alert_cust_dialog.setTitle("Customize Price Alert").setItems(
                dialog_items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        if (which == 0) {
                            //Set New Range
                            showSetNewRangeDialog(activity, item);


                        } else if (which == 1) {
                            //Remove from price alert
                            removeItemFromPriceAlert(activity, item);

                        }

                    }

                });
        AlertDialog portfo_cus_Dialog = price_alert_cust_dialog.create();
        portfo_cus_Dialog.show();

    }




    public static void removeItemFromPriceAlert(final Activity activity, final String item
                                             ) {
        AlertDialog.Builder delete_notif = new AlertDialog.Builder(activity);
        delete_notif.setTitle("Delete from price alert");
        delete_notif.setMessage(item
                + " is going to be removed from price alert.\n\nAre you sure?");
        delete_notif.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                                Activity.MODE_PRIVATE, null);
                        String db_creation_query = "CREATE TABLE IF NOT EXISTS " +
                                Constants.PRICE_ALERT_TABLE + "(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
                        dsebd.execSQL(db_creation_query);

                        String delete_query = "DELETE FROM " + Constants.PRICE_ALERT_TABLE + " WHERE item='" + item + "';";
                        dsebd.execSQL(delete_query);

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                activity)
                                .setTitle("Removed!")
                                .setMessage(
                                        "You have successfully removed itme from price alert.")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                restart_activity(activity);

                                            }
                                        }).setCancelable(false).show();

                    }
                });
        AlertDialog deletedialog = delete_notif.create();
        deletedialog.show();
    }




    private static void showSetNewRangeDialog(final Activity activity, final String item) {
        AlertDialog.Builder addPriceAlertDialogueBuilder  = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View workingView = inflater.inflate(R.layout.price_alert_dialog, null);
        addPriceAlertDialogueBuilder.setView(workingView);
        final TextView tvhigh = (TextView) workingView.findViewById(R.id.price_alert_high);
        final TextView tvlow = (TextView) workingView.findViewById(R.id.price_alert_low);
        String[] high_low_value = getHighLowValue(activity, item);
        tvhigh.setText("" + high_low_value[0]);
        tvlow.setText("" + high_low_value[1]);

        addPriceAlertDialogueBuilder.setPositiveButton(BUTTON_TEXT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Check if the fields are blank
                if (fieldsBlank(tvhigh, tvlow)) {
                    //If Blank, Show a error msg
                    showDialog(activity, "Error", "Some fields are blank!", "Ok");
                    return;
                }
                //Check if the input numbers are correct
                else if (inputNotValid(tvhigh, tvlow)) {
                    showDialog(activity, "Error", "Something wrong with your input data. Please check!", "Ok");
                    return;
                }
                //All Corrcet
                else {
                    //Update Item High Low
                    updateRange(activity, item, tvhigh.getText().toString(), tvlow.getText().toString());


                    //Show Success Message

                    showSuccessfulAddDialog(activity, "You have successfully updated range.");

                }


            }
        });

        AlertDialog final_dialog = addPriceAlertDialogueBuilder.create();
        final_dialog.show();

    }

    private static void updateRange(Activity activity,String item, String high, String low) {

        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String update_query = "UPDATE " + Constants.PRICE_ALERT_TABLE +
                " SET high='" + high + "', " +
                "low='" + low +
                "' WHERE item='"  + item + "';";
        dsebd.execSQL(update_query);
        dsebd.close();



    }



    public static void updateLtp(Context activity, String item, String ltp)
    {
        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String update_query = "UPDATE " + Constants.PRICE_ALERT_TABLE
                + " SET ltp='" + ltp + "' WHERE item='" + item + "';";
        dsebd.execSQL(update_query);
        dsebd.close();
    }

    public static ArrayList<String> getItems(Context activity)
    {
        ArrayList<String> items = new ArrayList<String>();

        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String search_string = "SELECT * FROM " + Constants.PRICE_ALERT_TABLE + ";";
        Cursor item_cursor = dsebd.rawQuery(search_string, null);
        int total_item = item_cursor.getCount();
        if (total_item == 0)
        {
            return items;
        }
        else
        {
            item_cursor.moveToFirst();
            do {

                String item_name = item_cursor.getString(0);
                items.add(item_name);
            }
            while (item_cursor.moveToNext());
        }

        item_cursor.close();
        dsebd.close();
        return items;
    }

    public static String[] getHighLowValue(Context activity, String item) {

        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String search_string = "SELECT * FROM " + Constants.PRICE_ALERT_TABLE + " WHERE item='" + item + "';";
        Cursor item_cursor = dsebd.rawQuery(search_string, null);
        item_cursor.moveToFirst();
        String[] value = {item_cursor.getString(1), item_cursor.getString(2)};
        item_cursor.close();
        dsebd.close();
        return value;

    }

    public static String getLtp(Context activity, String item)
    {
        SQLiteDatabase dsebd = activity.openOrCreateDatabase(Constants.DATABASE_NAME,
                Activity.MODE_PRIVATE, null);
        String db_creation_query = "CREATE TABLE IF NOT EXISTS "+
                Constants.PRICE_ALERT_TABLE+"(item VARCHAR, high VARCHAR, low VARCHAR, ltp VARCHAR);";
        dsebd.execSQL(db_creation_query);

        String search_string = "SELECT * FROM " + Constants.PRICE_ALERT_TABLE + " WHERE item='" + item + "';";
        Cursor item_cursor = dsebd.rawQuery(search_string, null);
        item_cursor.moveToFirst();
        String ltp = item_cursor.getString(3);
        item_cursor.close();
        dsebd.close();
        return ltp;

    }


}