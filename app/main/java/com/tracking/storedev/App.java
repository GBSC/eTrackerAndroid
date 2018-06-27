package com.tracking.storedev;

import android.app.Application;
import android.os.StrictMode;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.tracking.storedev.fragment.MapFragment;
import com.tracking.storedev.fragment.StoreVisitsFragment;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.web.HttpCaller;

import io.fabric.sdk.android.Fabric;

/**
 * Created by irfan on 3/19/18.
 */

public class App extends Application {

    public static App appInstance = new App();

    HttpCaller httpCaller = HttpCaller.getInstance();
    public static RequestQueue requestQueue;
    public StoreVisitsFragment storeVisitsFragment;
    public MapFragment mapFragment;

    public static App getAppInstance(){
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PrefManager.getInstance(this);
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("Evia.db").create();
        ActiveAndroid.initialize(dbConfiguration);
        this.requestQueue = Volley.newRequestQueue(getApplicationContext());
        AndroidNetworking.initialize(getApplicationContext());
        httpCaller.init(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Fabric.with(this, new Crashlytics());

    }


}
