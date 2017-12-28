package com.zyd.http;

import android.os.Handler;
import android.util.Log;

import com.zyd.utils.HandlerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 网络连接
 * Created by Administrator on 2017/12/25.
 */
public class MyHttpConnectionThread extends Thread {
    /**
     * 网址
     */
    private String url;
    /**
     * RequestMethod:POST GET
     */
    private String mod;
    /**
     * 传的参数
     */
    private String param;
    /**
     * 线程通信
     */
    private Handler handler;
    /**
     * 通信key
     */
    private String key;

    /**
     * 连接设置
     *
     * @param url     网址
     * @param mod     模式
     * @param param   参数
     * @param handler 通信
     */
    public MyHttpConnectionThread (String url, String mod, String param, Handler handler,
                                   String key) {
        this.url = url;
        this.mod = mod;
        this.param = param;
        this.handler = handler;
        this.key = key;
    }

    @Override
    public void run () {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(mod);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (mod.equals("POST")) {
                urlConnection.setDoOutput(true);
                OutputStream out = urlConnection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
            }
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    result.append(line);
                br.close();
                isr.close();
                in.close();
                Log.i("HTTP", result.toString());
                //把消息发送给主程序
                HandlerUtil.handlerMessage(handler, key, result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
