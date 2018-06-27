package com.tracking.storedev;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tracking.storedev.service.CoreService;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;

public class SettingsActivity extends AppCompatActivity {

    private Util utilInstance = Util.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    private CoreService coreService = CoreService.getCoreService();

    private Switch aSwitchService;
    private TextView txtLogout;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtLogout = (TextView)findViewById(R.id.txtLogout);
        aSwitchService = (Switch)findViewById(R.id.swtichService);
        btnBack = (ImageView)findViewById(R.id.btnBack);

        boolean isGPSServiceONOFF = (boolean)prefManager.get(Constants.SERVICE_ON_OFF, false);
        aSwitchService.setChecked(isGPSServiceONOFF);

        aSwitchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (!utilInstance.isMyServiceRunning(CoreService.class, SettingsActivity.this))
                        startService(new Intent(SettingsActivity.this, CoreService.class));

                    prefManager.put(Constants.SERVICE_ON_OFF, true);
                }else{
                    if (utilInstance.isMyServiceRunning(CoreService.class, SettingsActivity.this))
                        stopService(new Intent(SettingsActivity.this, CoreService.class));
                    prefManager.put(Constants.SERVICE_ON_OFF, false);
                }
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void logoutDialog() {
        new MaterialDialog.Builder(SettingsActivity.this).content("Do you want to logout?").contentColor(getResources().getColor(R.color.black)).backgroundColor(getResources().getColor(R.color.white))
                .positiveText("Yes")
                .negativeText("No").negativeColor(Color.parseColor("#ff0000")).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                prefManager.logout();
                logOut();
                if (!utilInstance.isMyServiceRunning(CoreService.class, SettingsActivity.this))
                    coreService.stopService(SettingsActivity.this);

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).show();
    }

    public void logOut() {
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "Store"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "OrderItem"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "InventoryItem"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "StoreVisit"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "SubSection"));
        ActiveAndroid.execSQL(String.format("DELETE FROM %s;", "TrackerLogs"));
    }

}
