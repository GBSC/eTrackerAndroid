package com.tracking.storedev.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.tracking.storedev.service.CoreService;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.web.WebCore;

/**
 * Created by ZASS on 4/25/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private Util utilInstance = Util.getInstance();
    private WebCore webCore = WebCore.getInstance();
    private CoreService coreService = CoreService.getCoreService();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Handler handler = new Handler();
        Runnable myRunnable = new Runnable() {
            public void run() {
                int userID = prefManager.getUserID();
                if(utilInstance.isNetworkConnected(context) && userID != 0){
                }else{
                    coreService.stopService();
                }
            }
        };
        handler.postDelayed(myRunnable,20 * 1000);

    }
}
