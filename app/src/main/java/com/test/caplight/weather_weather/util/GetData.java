package com.test.caplight.weather_weather.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData {
    public static String getJson(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] data = StreamTool.read(in);
            return new String(data, "UTF-8");
        }
        return null;
    }

    public static JSONArray getImgJson(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            //将输入流转换成字符串
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte [] buffer=new byte[1024];
            int len=0;
            while((len=in.read(buffer))!=-1){
                baos.write(buffer, 0, len);
            }
            String jsonString=baos.toString();
            baos.close();
            in.close();
            return new JSONArray(jsonString);
        }
        return null;
    }


}
