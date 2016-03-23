package com.smartdsepro.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Portfolio_menu_helper {
    static int success_status = -3;



    public static void generate_dialog(final Activity activity,
                                       final DSE_Company_data portfolio_clicked_company,
                                       String[] portfolio_strings) {

        AlertDialog.Builder portfolio_cust_dialog = new AlertDialog.Builder(
                activity);

        portfolio_cust_dialog.setTitle("Customize Portfolio").setItems(
                portfolio_strings, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        if (which == 0 | which == 1) {
                            Portfolio_menu_helper.show_add_reduce_stock_dialog(
                                    which, activity, portfolio_clicked_company);
                        } else if (which == 2) {
                            remove_portfolio_item(activity,
                                    portfolio_clicked_company);
                        } else if (which == 3) {
                            Intent detail_info_intent = new Intent(activity,
                                    ItemInfo.class);
                            detail_info_intent
                                    .putExtra("TradingCode",
                                            portfolio_clicked_company
                                                    .getCompany_Name());
                            activity.startActivity(detail_info_intent);
                        }

                    }

                });
        AlertDialog portfo_cus_Dialog = portfolio_cust_dialog.create();
        portfo_cus_Dialog.show();

    }

    public static void remove_portfolio_item(final Activity activity,
                                             final DSE_Company_data portfolio_clicked_company) {
        AlertDialog.Builder delete_notif = new AlertDialog.Builder(activity);
        delete_notif.setTitle("Delete from portfolio");
        delete_notif.setMessage(portfolio_clicked_company.getCompany_Name()
                + " is going to be removed from portfolio.\n\nAre you sure?");
        delete_notif.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase dsebd;
                        dsebd = activity.openOrCreateDatabase("dsedatabase",
                                Activity.MODE_PRIVATE, null);
                        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");

                        dsebd.execSQL("DELETE FROM portfolioitems WHERE stockname='"
                                + portfolio_clicked_company.getCompany_Name()
                                + "'");
                        dsebd.close();
                        if (!watchlist_existance(activity,
                                portfolio_clicked_company.getCompany_Name())) {
                            activity.deleteFile(portfolio_clicked_company
                                    .getCompany_Name());
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                activity)
                                .setTitle("Removed!")
                                .setMessage(
                                        "You have successfully removed stock from your portfolio.")
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

    public static void show_add_reduce_stock_dialog(final int command,
                                                    final Activity activity,
                                                    final DSE_Company_data portfolio_clicked_company) {

        AlertDialog.Builder portfolio_add_dialog = new AlertDialog.Builder(
                activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View workingview = inflater.inflate(
                R.layout.portfolio_cust_dialog, null);
        portfolio_add_dialog.setView(workingview);
        final SQLiteDatabase dsebd;
        dsebd = activity.openOrCreateDatabase("dsedatabase",
                Activity.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
        String button_string = "None";
        final EditText no_of_new_stocks = (EditText) workingview
                .findViewById(R.id.portfolio_cust_noofstocks);
        final EditText new_unitprice = (EditText) workingview
                .findViewById(R.id.portfolio_cust_unitprice);
        final TextView title = (TextView) workingview
                .findViewById(R.id.portfolio_cust_title);
        TextView price_tag = (TextView) workingview
                .findViewById(R.id.portfolio_cust_unit_tag);

        if (command == 0) {
            // Set the title Add Stock
            title.setText("Add Stock");
            button_string = "Add";
        } else if (command == 1) {
            title.setText("Reduce Stock");
            button_string = "Reduce";
            price_tag.setVisibility(View.GONE);
            new_unitprice.setVisibility(View.GONE);
        }

        portfolio_add_dialog.setPositiveButton(button_string,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int arg1) {
                        if (command==0) {


                            if (!check_field()) {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        activity)
                                        .setTitle("Error!")
                                        .setMessage(
                                                "Some fields are blank! Please check.")
                                        .setPositiveButton("OK", null).show();
                                return;
                            }
                        }
                        else if (command==1) {
                            if (no_of_new_stocks.getText().toString().equals("")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        activity)
                                        .setTitle("Error!")
                                        .setMessage(
                                                "Some fields are blank! Please check.")
                                        .setPositiveButton("OK", null).show();
                                return;
                            }
                        }

                        int prev_no_of_stocks = Integer
                                .parseInt(portfolio_clicked_company
                                        .getNumberofstocks());

                        int new_no_of_stocks = Integer
                                .parseInt(no_of_new_stocks.getText().toString());

                        int fin_total_no_of_stocks = 1;
                        if (command == 0) {
                            double prev_dunitprice = Double
                                    .parseDouble(portfolio_clicked_company
                                            .getPurchaceunit());
                            double prev_total_ammount = prev_no_of_stocks
                                    * prev_dunitprice;
                            double new_dunitprice = Double
                                    .parseDouble(new_unitprice.getText()
                                            .toString());
                            double new_total_ammount = new_no_of_stocks
                                    * new_dunitprice;
                            double fin_summation_totalammount = 0;

                            fin_summation_totalammount = prev_total_ammount
                                    + new_total_ammount;
                            fin_total_no_of_stocks = prev_no_of_stocks
                                    + new_no_of_stocks;
                            if (fin_total_no_of_stocks>999999) {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        activity)
                                        .setTitle("Error!")
                                        .setMessage(
                                                "You are exceeding the maximum number of stocks!")
                                        .setPositiveButton("OK", null)
                                        .show();
                                ;
                                return;
                            }
                            if (new_dunitprice<1) {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        activity)
                                        .setTitle("Error!")
                                        .setMessage(
                                                "Price is too low!")
                                        .setPositiveButton("OK", null)
                                        .show();
                                ;
                                return;
                            }

                            double fin_avg_unitprice = fin_summation_totalammount
                                    / (double) fin_total_no_of_stocks;
                            String finalunitprice = Double
                                    .toString(fin_avg_unitprice);
                            String finalnumberofstocks = Integer
                                    .toString(fin_total_no_of_stocks);
                            dsebd.execSQL("UPDATE portfolioitems SET stockname='"
                                    + portfolio_clicked_company
                                    .getCompany_Name()
                                    + "', purchaseunit='"
                                    + finalunitprice
                                    + "', currentprice='"
                                    + portfolio_clicked_company
                                    .getLaste_trade()
                                    + "', numberofstocks='"
                                    + finalnumberofstocks
                                    + "'"
                                    + "WHERE stockname='"
                                    + portfolio_clicked_company
                                    .getCompany_Name() + "';");
                            dsebd.close();

                        }
                        if (command == 1) {

                            fin_total_no_of_stocks = prev_no_of_stocks
                                    - new_no_of_stocks;
                            if (fin_total_no_of_stocks < 0) {
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        activity)
                                        .setTitle("Error!")
                                        .setMessage(
                                                "You are trying to reduce more stocks than you have!")
                                        .setPositiveButton("Cancel", null)
                                        .show();
                                ;
                                return;

                            } else if (fin_total_no_of_stocks == 0) {
                                remove_portfolio_item(activity,
                                        portfolio_clicked_company);
                                return;
                            } else {
                                String finalnumberofstocks = Integer
                                        .toString(fin_total_no_of_stocks);
                                dsebd.execSQL("UPDATE portfolioitems SET numberofstocks='"
                                        + finalnumberofstocks
                                        + "'"
                                        + "WHERE stockname='"
                                        + portfolio_clicked_company
                                        .getCompany_Name() + "';");
                            }

                        }

                        if (command == 1) {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    activity)
                                    .setTitle("Success!")
                                    .setMessage(
                                            "You have successfully reduced stock from your portfolio.")
                                    .setPositiveButton(
                                            "OK",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    restart_activity(activity);

                                                }
                                            }).setCancelable(false).show();

                        } else if (command == 0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    activity)
                                    .setTitle("Success!")
                                    .setMessage(
                                            "You have successfully added stock to your portfolio.")
                                    .setPositiveButton(
                                            "OK",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    restart_activity(activity);

                                                }
                                            }).setCancelable(false).show();
                            ;
                        }

                    }

                    private boolean check_field() {
                        if (no_of_new_stocks.getText().toString().equals("")
                                | new_unitprice.getText().toString().equals("")) {
                            return false;
                        } else {
                            return true;
                        }

                    }
                });

        AlertDialog portfo_add_Dialog_fin = portfolio_add_dialog.create();
        portfo_add_Dialog_fin.show();

    }






    public static void new_portfolio_add(final Activity activity,
                                         final String itemname, final String currentprice,
                                         final String file_data) {

        AlertDialog.Builder portfolio_add_dialog = new AlertDialog.Builder(
                activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final View workingview = inflater.inflate(
                R.layout.portfolio_cust_dialog, null);
        portfolio_add_dialog.setView(workingview);
        final SQLiteDatabase dsebd;
        dsebd = activity.openOrCreateDatabase("dsedatabase",
                Activity.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
        String button_string = "None";
        final EditText no_of_new_stocks = (EditText) workingview
                .findViewById(R.id.portfolio_cust_noofstocks);
        final EditText new_unitprice = (EditText) workingview
                .findViewById(R.id.portfolio_cust_unitprice);
        final TextView title = (TextView) workingview
                .findViewById(R.id.portfolio_cust_title);

        title.setText("Add Stock");
        button_string = "Add";

        portfolio_add_dialog.setPositiveButton(button_string,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int arg1) {
                        if (new_unitprice.getText().toString().equals("")
                                | no_of_new_stocks.getText().toString()
                                .equals("")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    activity)
                                    .setTitle("Error")
                                    .setMessage(
                                            "Some fields are blank! Please check")
                                    .setPositiveButton("OK", null).show();
                            return;
                        }
                        if (Double.parseDouble(new_unitprice.getText().toString())<1) {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    activity)
                                    .setTitle("Error!")
                                    .setMessage(
                                            "Price is too low!")
                                    .setPositiveButton("OK", null)
                                    .show();
                            ;
                            return;
                        }


                        dsebd.execSQL("INSERT INTO portfolioitems VALUES('"
                                + itemname + "', '"
                                + new_unitprice.getText().toString() + "', '"
                                + currentprice + "', '"
                                + no_of_new_stocks.getText().toString() + "');");

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    DevTools.write_file(activity, itemname, file_data);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }

                            }
                        });
                        thread.start();

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                activity)
                                .setTitle("Success!")
                                .setMessage(
                                        "You have successfully added stock to your portfolio.")
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
                });
        AlertDialog portfo_add_Dialog_fin = portfolio_add_dialog.create();
        portfo_add_Dialog_fin.show();

    }

    public static String[] get_infos_of_portfolio_item(Activity sent_activity,
                                                       String itemname) {
        SQLiteDatabase dsebd;
        dsebd = sent_activity.openOrCreateDatabase("dsedatabase",
                Context.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
        Cursor portfolio_cursor = dsebd.rawQuery(
                "SELECT * FROM portfolioitems WHERE stockname='" + itemname
                        + "';", null);
        portfolio_cursor.moveToFirst();
        String number_of_stocks = portfolio_cursor.getString(3);
        String purchase_unit = portfolio_cursor.getString(1);
        String[] final_arrayString = { number_of_stocks, purchase_unit };
        dsebd.close();
        portfolio_cursor.close();
        return final_arrayString;

    }

    // This function returns a MAP object.. The key pair values will
    // Used for uploading the datas in Spalsh
    public static Map<String, String> need_to_upload(Activity activity) {

        SQLiteDatabase dsebd;
        dsebd = activity.openOrCreateDatabase("dsedatabase",
                Context.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
        Cursor portfolio_cursor = dsebd.rawQuery(
                "SELECT * FROM portfolioitems", null);
        if (portfolio_cursor.getCount() == 0) {
            dsebd.close();
            portfolio_cursor.close();
            return null;
        }
        portfolio_cursor.moveToFirst();

        Map<String, String> portfolio_items = new HashMap<>();
        do {
            portfolio_items.put(portfolio_cursor.getString(0),
                    portfolio_cursor.getString(3));
        } while (portfolio_cursor.moveToNext());
        dsebd.close();
        portfolio_cursor.close();
        return portfolio_items;

    }

    private static void restart_activity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.startActivity(intent);
        activity.finish();
    }

    public static boolean watchlist_existance(Activity activity, String itemname) {
        boolean status = false;
        SQLiteDatabase dsebd = activity.openOrCreateDatabase("dsedatabase",
                Context.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS watchlist(itemnames VARCHAR);");
        Cursor watchlistcursor = dsebd.rawQuery(
                "SELECT * FROM watchlist WHERE itemnames='" + itemname + "'",
                null);
        // Check if file exists
        if (watchlistcursor.getCount() > 0) {
            status = true;
        }
        dsebd.close();
        watchlistcursor.close();
        return status;
    }

    public static boolean portfolio_exixtance(Activity activity, String itemname) {
        boolean status = false;
        SQLiteDatabase dsebd = activity.openOrCreateDatabase("dsedatabase",
                Context.MODE_PRIVATE, null);
        dsebd.execSQL("CREATE TABLE IF NOT EXISTS portfolioitems(stockname VARCHAR, purchaseunit VARCHAR, currentprice VARCHAR, numberofstocks VARCHAR);");
        Cursor dseportfoliocursor = dsebd.rawQuery(
                "SELECT * FROM portfolioitems WHERE stockname='" + itemname
                        + "';", null);
        if (dseportfoliocursor.getCount() > 0) {

            status = true;

        }
        dseportfoliocursor.close();
        dsebd.close();

        return status;

    }

}
