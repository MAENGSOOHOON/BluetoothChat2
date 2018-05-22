package com.example.android.bluetoothchat;

/**
 * Created by MSH on 2018-03-16.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PHPRequest {
    private URL url;

    public PHPRequest(String url) throws MalformedURLException { this.url = new URL(url); }

    private String readStream(InputStream in) throws IOException {
        StringBuilder jsonHtml = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;

        while((line = reader.readLine()) != null)
            jsonHtml.append(line);

        reader.close();
        return jsonHtml.toString();
    }

    public String PHPtest(final int data2, final String data3,final String data4, final String data5, final String data6, final String data7,final String data8, final String data9, final String data10, final String data11) {
        try {
            String postData = "device_number=" + data2 + "&" + "patient_num=" + data3 + "&" + "patient_name=" + data4 + "&" + "cpr_date=" + data5 + "&" + "cpr_start_time=" + data6 + "&" +"cpr_end_time=" + data7 + "&" + "pressure=" + data8 + "&" + "sequence=" + data9 + "&" + "dept=" + data10 + "&" + "location=" + data11;
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
            return null;
        }
    }
}