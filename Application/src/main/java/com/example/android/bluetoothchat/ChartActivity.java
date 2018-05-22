package com.example.android.bluetoothchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class ChartActivity extends Activity {

    String myJSON;

    JSONArray index = null;
    private static final String TAG_JSON = "cpr_helper";
    private static final String TAG_pressure = "pressure";
    private static final String TAG_sequence = "sequence";
    private static final String TAG_dept = "dept";

    public static float aa;
    public static float bb;
    public static float cc;
    public static TextView tv_cpr_during_time;


    Handler handler = new Handler();

    public CombinedChart mChart;
  //  protected BarChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTitle("CPR HELPER HISTORY");
        NetworkUtil.setNetworkPolicy();

        tv_cpr_during_time = (TextView) findViewById(R.id.tv_cpr_during_time);

        Button BTN1 = (Button)findViewById(R.id.BTN_DATA_CHECK);
        BTN1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                    getData("http://192.168.43.168/htdocs/getjson.php");
                    mChart.invalidate();

                    Intent intent1 = getIntent();
                    finish();
                    startActivity(intent1);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_cpr_during_time.setText(BluetoothChatFragment.cpr_during_time);
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();


                /*
                getData("http://192.168.0.14/htdocs/getjson.php");
                mChart.invalidate();


                Intent intent1 = getIntent();
                finish();

                startActivity(intent1);


                tv_cpr_during_time.setText(BluetoothChatFragment.cpr_during_time);
                Log.d("tttt", BluetoothChatFragment.cpr_during_time);
*/
                }
        });


        mChart = (CombinedChart) findViewById(R.id.chart1);

        mChart.getDescription().setText("This is testing Description");

        mChart.setDrawBarShadow(true);
        mChart.setHighlightFullBarEnabled(true);
        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });
        CombinedData data = new CombinedData();

        data.setData( generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        mChart.setData(data);
     //   mChart.invalidate();
    }

    int x1 = 1;
    int x2 = 2;
    int x3 = 3;

    float y1 = aa;
    float y2 = bb;
    float y3 = cc;
    private ArrayList<Entry> getLineEntriesData(ArrayList<Entry> entries){
    entries.add(new Entry(x1, 10));
    entries.add(new Entry(x2,10));
    entries.add(new Entry(x3, 10));


    return entries;
}

    private ArrayList<BarEntry> getBarEnteries(ArrayList<BarEntry> entries){
        entries.add(new BarEntry(x1, y1));
        entries.add(new BarEntry(x2, y2));
        entries.add(new BarEntry(x3, y3));

        return  entries;
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries = getLineEntriesData(entries);

        LineDataSet set = new LineDataSet(entries, "기준선");
        //set.setColor(Color.rgb(240, 238, 70));
        set.setColors(Color.RED);
        set.setLineWidth(2.5f);
        set.setLabel("기준선");
        set.setCircleColor(Color.RED);//(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.RED);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }
    private BarData generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        entries = getBarEnteries(entries);

        BarDataSet set1 = new BarDataSet(entries, "실제수치평균");
        //set1.setColor(Color.rgb(60, 220, 78));
        set1.setColors(ColorTemplate.MATERIAL_COLORS);
        set1.setValueTextColor(Color.BLACK);
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }
    protected String[] mMonths = new String[] {
            " ","압력", "횟수", "깊이"
    };

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON=result;

                Log.d("jsonResult",myJSON);
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            index = jsonObj.getJSONArray(TAG_JSON);

            for (int i = 0; i < index.length(); i++) {
                JSONObject c = index.getJSONObject(i);
                String pressure = c.getString(TAG_pressure);
                String sequence = c.getString(TAG_sequence);
                String dept = c.getString(TAG_dept);

                aa = Float.valueOf((String)pressure);
                bb = Float.valueOf((String)sequence);
                cc = Float.valueOf((String)dept);//Integer.valueOf((String)dept);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}