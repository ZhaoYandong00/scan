package com.zyd.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 信息传递类
 * Created by ZYD on 2017/12/28.
 */
public class HandlerUtil {
    public static void handlerMessage (Handler handler, String key, String value) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
