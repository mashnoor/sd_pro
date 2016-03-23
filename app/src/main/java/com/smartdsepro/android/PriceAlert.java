package com.smartdsepro.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PriceAlert extends Activity {


    ButtonController buttonController;
    ListView price_alert_list_view;
    ArrayAdapter<PriceAlertHelper> adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_alert);
        price_alert_list_view = (ListView) findViewById(R.id.dse_price_alert_list);
        adapter = new DSE_Price_alert_Data_adapter();
        buttonController = new ButtonController(this);
        if(PriceAlertHelper.getAllItems(PriceAlert.this) == null)
        {
            Toast.makeText(this, "No item is inserted for price alert!", Toast.LENGTH_LONG).show();
        }
        else {
            showInListView();
            registeronclicklistener();

        }



    }


    private void registeronclicklistener() {
        price_alert_list_view
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        PriceAlertHelper clicked_company = PriceAlertHelper.getAllItems(PriceAlert.this)
                                .get(position);
                        int clicked_item_position = position;

                        PriceAlertHelper.show_customize_price_alert_dialog(PriceAlert.this, clicked_company.getItem_name());



                    }

                });

    }


    public class DSE_Price_alert_Data_adapter extends
            ArrayAdapter<PriceAlertHelper> {
        public DSE_Price_alert_Data_adapter() {
            super(getApplicationContext(), R.layout.dse_price_alert_like,
                    PriceAlertHelper.getAllItems(PriceAlert.this));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.dse_price_alert_like, parent, false);

            }

            PriceAlertHelper current = getItem(position);

            TextView symbol = (TextView) workingView
                    .findViewById(R.id.price_alert_symbol);

            TextView ltp = (TextView) workingView
                    .findViewById(R.id.price_alert_ltp);
            TextView low = (TextView) workingView
                    .findViewById(R.id.price_alert_low);
            TextView high = (TextView) workingView
                    .findViewById(R.id.price_alert_high);
            ImageView status = (ImageView) workingView.findViewById(R.id.price_alert_status);

            if(current.getStatus()==PriceAlertHelper.HIGH)
            {
                status.setImageResource(R.drawable.pricealert_up);
            }
            else if(current.getStatus()==PriceAlertHelper.LOW)
            {
                status.setImageResource(R.drawable.pricealert_down);

            }
            else
            {
                status.setImageResource(R.drawable.pricealert_stable);
            }

            symbol.setText(current.getItem_name());
            ltp.setText(current.getLtp());

            low.setText(current.getLow_price());
            high.setText(current.getHigh_price());

            return workingView;
        }

    }


    private void showInListView() {



        price_alert_list_view.setAdapter(adapter);
        // dse_Company_datas_for_search.addAll(dse_Company_datas);
       // registeronclicklistener();

    }



}
