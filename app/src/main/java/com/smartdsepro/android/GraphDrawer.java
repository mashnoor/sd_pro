package com.smartdsepro.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mashnoor on 2/16/16.
 */
public class GraphDrawer {
    Activity activity;
    JSONArray home_graph_data;
    JSONArray home_volume_graph_data;
    ArrayList<Entry> dsex_values;
    ArrayList<BarEntry> dsex_volume_values;
    ArrayList<String> dsex_times;
    ArrayList<String> dsex_volume_times;

    public GraphDrawer(Activity activity)

    {

        this.activity = activity;
    }

    public class grab_and_draw_graph extends AsyncTask<String, String, String>
    {

        ProgressDialog progressDialog = ProgressDialog.show(activity, "",
                "Processing Graph", true);

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);




            // creating labels


            /********************* START OF DRAWING THE BAR CHART ****************************/

            /***
            final BarChart volumeChart = (BarChart) activity.findViewById(R.id.home_volume_chart);
            final BarDataSet volume_dataset = new BarDataSet(dsex_volume_values, "");
            volumeChart.setScaleEnabled(false);
            final BarData volume_data = new BarData(dsex_volume_times, volume_dataset);
            volumeChart.setData(volume_data); // set the data and list of lables into chart
            volumeChart.setDescription("Description");  // set the description
            volumeChart.setTouchEnabled(true);
            volumeChart.setBackgroundColor(Color.BLACK);
            volumeChart.setGridBackgroundColor(Color.BLACK);
            volumeChart.setDrawBorders(true);
            volumeChart.setBorderWidth(1f);
            volumeChart.getAxisLeft().setDrawGridLines(false);
            volumeChart.getAxisRight().setDrawGridLines(false);
            volumeChart.getXAxis().setDrawGridLines(false);
            volumeChart.getAxisLeft().setTextColor(Color.WHITE);
            volumeChart.getXAxis().setTextColor(Color.WHITE);
            volumeChart.getAxisRight().setStartAtZero(false);
            volumeChart.getAxisLeft().setStartAtZero(false);
            volume_dataset.setValueTextColor(Color.WHITE);
            volumeChart.setBorderColor(Color.parseColor(Constants.FAVOURITE_COLOR));
            volumeChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    Toast.makeText(activity, volume_dataset.getYValForXIndex(e.getXIndex()) + ", " + volumeChart.getXValue(e.getXIndex()), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected() {

                }
            });
            ***/

            /*************************** END OF DRAWING THE BAR CHART ************************/






            /******************* START of Drawing The Line Chart ********************/
            final LineChart lineChart = (LineChart) activity.findViewById(R.id.home_chart);
            final LineDataSet dataset = new LineDataSet(dsex_values, "# of Calls");

            lineChart.setScaleEnabled(false);
            dataset.setDrawCircles(false);

            dataset.setDrawFilled(false);
            dataset.setFillColor(Color.parseColor(Constants.FAVOURITE_COLOR));
            final LineData data = new LineData(dsex_times, dataset);

            lineChart.setDescription("Description");  // set the description
            lineChart.setTouchEnabled(true);
            lineChart.getAxisRight().setStartAtZero(false);
            lineChart.getAxisLeft().setStartAtZero(false);
           /***
            lineChart.getAxisLeft().setAxisMinValue(4560f);
            lineChart.getAxisLeft().setAxisMaxValue(4599f);
            lineChart.getAxisRight().setAxisMinValue(4560f);
            lineChart.getAxisRight().setAxisMaxValue(4599f);


            ***/
            lineChart.setBackgroundColor(Color.BLACK);
            lineChart.setGridBackgroundColor(Color.BLACK);
            lineChart.setDrawBorders(true);
            lineChart.setBorderWidth(1f);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setGridColor(Color.BLACK);
            lineChart.getAxisRight().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);

            lineChart.setData(data); // set the data and list of lables into chart

            lineChart.getAxisLeft().setTextColor(Color.WHITE);
            lineChart.getXAxis().setTextColor(Color.WHITE);
            dataset.setValueTextColor(Color.WHITE);
            lineChart.setBorderColor(Color.parseColor(Constants.FAVOURITE_COLOR));
            lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    Toast.makeText(activity, dataset.getYValForXIndex(e.getXIndex()) + ", " + lineChart.getXValue(e.getXIndex()), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected() {

                }
            });
            /***************** End Of Drawing Line Chart *********************/

            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }



        }

        @Override
        protected String doInBackground(String... params) {


            dsex_times = new ArrayList<>();
            dsex_values = new ArrayList<>();
            dsex_volume_values = new ArrayList<>();
            dsex_volume_times = new ArrayList<>();
            String home_graph = "";
            String home_volume_graph = "";
            try {
                SrcGrabber grabber = new SrcGrabber();
                home_graph = grabber.grabSource(Constants.HOME_GRAPH_LINK);
                home_volume_graph = grabber.grabSource(Constants.VOLUME_GRAPH_LINK);
                home_graph_data = new JSONArray(home_graph);
                home_volume_graph_data = new JSONArray(home_volume_graph);
                //Get The DSEX Graph Data
                for (int i = 0; i<home_graph_data.length(); i++)
                {
                    JSONObject curr_obj = home_graph_data.getJSONObject(i);
                    Iterator keys = curr_obj.keys();
                    String time = (String) keys.next();
                    float value = (float)curr_obj.getDouble(time);
                    dsex_times.add(time);

                    dsex_values.add(new Entry(value, i));
                }

                //Get The Volume Graph Data
                for (int i = 0; i<home_volume_graph_data.length(); i++)
                {
                    JSONObject curr_obj = home_volume_graph_data.getJSONObject(i);
                    Iterator keys = curr_obj.keys();
                    String time = (String) keys.next();
                    float value = (float)curr_obj.getDouble(time);
                    dsex_volume_times.add(time);
                    dsex_volume_values.add(new BarEntry(value, i));
                }

            }
            catch (Exception e)
            {

            }

            return null;
        }
    }

    public void drawHomeGraph()
    {

       new grab_and_draw_graph().execute("");

    }



}
