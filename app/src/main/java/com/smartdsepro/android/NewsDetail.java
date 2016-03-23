package com.smartdsepro.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class NewsDetail extends Activity {


    String company_name;
    JSONObject news_json_object = null;
    JSONArray news_jsonArray = null;
    ArrayList<News> news_detail_arraylist;

    ListView news_detail_list;
    ButtonController buttonController;

    ArrayAdapter<News> adapter; // This array adapter will describe
    // how the listview will be shown
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Intent newsintent = getIntent();
        company_name = newsintent.getStringExtra("company");
        news_detail_arraylist = new ArrayList<News>();
        buttonController = new ButtonController(this);
       new grab_news_detail().execute("");
    }



    private void showInListView() {
        news_detail_list = (ListView) findViewById(R.id.dse_news_detail_list);
        adapter = new DSE_Data_adapter();
        news_detail_list.setAdapter(adapter);

    }

    @SuppressLint("DefaultLocale")
    public class DSE_Data_adapter extends ArrayAdapter<News> {
        public DSE_Data_adapter() {
            super(getApplicationContext(), R.layout.news_detail_like,
                    news_detail_arraylist);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View workingView = convertView;
            if (workingView == null) {
                workingView = getLayoutInflater().inflate(
                        R.layout.news_detail_like, parent, false);

            }

            News current = getItem(position);
            TextView tv_itemname = (TextView) workingView
                    .findViewById(R.id.news_detail_itemname);
            TextView tv_news = (TextView) workingView
                    .findViewById(R.id.news_detail_news);
            TextView tv_date = (TextView) workingView
                    .findViewById(R.id.news_detail_date);

            tv_itemname.setText(current.getItem_name());
            tv_news.setText(Html.fromHtml(current.getNews()).toString());
            tv_date.setText(current.getDate());

            return workingView;
        }

    }



    //AsyncTask for News Grabbing
    class grab_news_detail extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = ProgressDialog.show(NewsDetail.this,
                "", "Retrieving Data...", true);

        @Override
        protected void onPostExecute(String result) {

            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }

            // if (!has_active_net) {
            // Toast.makeText(getApplicationContext(),
            // "No internet connection. Showing Last retrived data.",
            // Toast.LENGTH_LONG).show();
            // }
            if (result == null) {
                Toast.makeText(NewsDetail.this, "Can't connect to the server! Check your internet.",
                        Toast.LENGTH_LONG).show();

            } else {
                showInListView();

            }

        }

        @Override
        protected String doInBackground(String... params) {
            SrcGrabber grabber = new SrcGrabber();
            String string = null;

            try {
                if (Active_net_checking.testInte("104.131.22.246")) {
                    string = grabber
                            .grabSource("http://104.131.22.246/dev/smartdsefiles/news/" + company_name + ".txt");
                } else {
                    return null;
                }

            } catch (IOException | URISyntaxException e1) {

            }

            try {
                news_jsonArray = new JSONArray(string);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                return null;
            }

            int jsonarraysize = news_jsonArray.length();

            try {
                for (int i = 0; i < jsonarraysize; i++) {
                    news_json_object = news_jsonArray.getJSONObject(i);


                    String news = news_json_object.getString("news");
                    System.out.print(news);

                    String date = news_json_object.getString("date");
                    News current_News = new News(
                            company_name, news, date);
                    //Log.d("Error:", news);
                    news_detail_arraylist.add(current_News);

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return "";

        }

    }


}
