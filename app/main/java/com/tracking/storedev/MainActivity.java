package com.tracking.storedev;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.FirebaseApp;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.fragment.MapFragment;
import com.tracking.storedev.fragment.StoreVisitsFragment;
import com.tracking.storedev.service.CoreService;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.DBToObject;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.DisableSwipViewPager;
import com.tracking.storedev.view.ViewGroups;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;

import org.json.JSONObject;

import im.delight.android.location.SimpleLocation;


public class MainActivity extends AppCompatActivity {

    private CoreService coreService = CoreService.getCoreService();
    private Util utilInstance = Util.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    private DBHandler dbHandler = DBHandler.getInstance();
    private ViewGroups viewGroups = ViewGroups.getInstance();
    private DBToObject dbToObject = DBToObject.getInstance();

    public static SimpleLocation locationInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseApp.initializeApp(MainActivity.this);
        locationInstance =new SimpleLocation(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navOptionClickListerner(drawer);
        initJourneyCycle();

        getSupportActionBar().setTitle(null);

        dbHandler.updateStatus();


        boolean isFockLocationEnabled = false;//utilInstance.isMockLocationEnabled(MainActivity.this);
        if(isFockLocationEnabled){
            viewGroups.showSnackMessage(drawer, "You are using fake GPS location app, please uninstall that app", 20000);
        }else if (GPSStoragePermissionGranted()) {
            if (utilInstance.isGPSEnable(MainActivity.this)) {
                boolean isGPSServiceONOFF = (boolean)prefManager.get(Constants.SERVICE_ON_OFF, false);
                if(isGPSServiceONOFF)
                    if (!utilInstance.isMyServiceRunning(CoreService.class, MainActivity.this))
                       startService(new Intent(MainActivity.this, CoreService.class));
            } else {
                gpsLocationDialog();
            }

        } else {
            Toast.makeText(MainActivity.this, "GPS permission granted", Toast.LENGTH_SHORT).show();
        }

    }

    public void navOptionClickListerner(DrawerLayout drawerLayout) {
        RelativeLayout layoutStoreRegistration = (RelativeLayout) drawerLayout.findViewById(R.id.layout_storeregistration);
        RelativeLayout layout_createAccount = (RelativeLayout) drawerLayout.findViewById(R.id.layout_createAccount);
        RelativeLayout layout_settings = (RelativeLayout) drawerLayout.findViewById(R.id.layout_settings);
        RelativeLayout layout_checking = (RelativeLayout) drawerLayout.findViewById(R.id.layout_checking);

        TextView textViewSalesEmail = (TextView) drawerLayout.findViewById(R.id.textViewSalesEmail);
        TextView textViewSalesname = (TextView) drawerLayout.findViewById(R.id.textViewSalesname);

        String name = "" + prefManager.get(Constants.FullName, new String());
        String email = "" + prefManager.get(Constants.Email, new String());

        if(email.equals("null")) {
            textViewSalesEmail.setText("");
        }else{
            textViewSalesEmail.setText(email);
        }

        if(name.equals("null")) {
            textViewSalesname.setText("");
        }else{
            textViewSalesname.setText(name );
        }


        layoutStoreRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StoreRegistrationActivity.class));
            }
        });

        layout_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });


        layout_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
            }
        });

        layout_checking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = dbToObject.getMileageHashMapObject(prefManager);
                if(jsonObject != null){
                    String url = WebURL.addMileage;
                    HttpCaller.getInstance().jsonObjectPOSTRequest(true, MainActivity.this, url, jsonObject, new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            System.out.println("Sync");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Sync");
                        }
                    });
                }
            }
        });


    }

    public void initJourneyCycle() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Map", MapFragment.class)
                .add("Store visits", StoreVisitsFragment.class)
                .create());

        DisableSwipViewPager viewPager = (DisableSwipViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);

        final SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //GPS On response
        if (requestCode == 100) {
            if (!utilInstance.isMyServiceRunning(CoreService.class, MainActivity.this))
                startService(new Intent(MainActivity.this, CoreService.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            if (!utilInstance.isMyServiceRunning(CoreService.class, MainActivity.this))
                startService(new Intent(MainActivity.this, CoreService.class));
        }
    }

    public boolean GPSStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    public void logOut() {
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "Store"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "OrderItem"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "InventoryItem"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "StoreVisit"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "SubSection"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "TrackerLogs"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            if (locationInstance != null)
                locationInstance.beginUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            if (locationInstance != null)
                locationInstance.endUpdates();
        }
    }

    public void gpsLocationDialog() {
        new MaterialDialog.Builder(MainActivity.this).content("Turn on locationInstance services to determine your locationInstance").contentColor(getResources().getColor(R.color.black)).backgroundColor(getResources().getColor(R.color.white))
                .positiveText("Setting")
                .negativeText("Cancel").negativeColor(Color.parseColor("#ff0000")).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
            }
        }).show();
    }
}

