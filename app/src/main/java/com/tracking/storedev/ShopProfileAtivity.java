package com.tracking.storedev;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.web.WebURL;

import org.json.JSONArray;

import java.io.File;

public class ShopProfileAtivity extends AppCompatActivity {

    public Util utilInstance = Util.getInstance();
    public App appInstance = App.getAppInstance();

    public static Store storeInfo;
    public static boolean isCheckout = false;
    public static String Status;
    public static LatLng latlng;
    private ImageView imageView, imgBack;
    private Button btnCheckin, btnDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_profile_activity);

        isCheckout = false;

        TextView txtShopName = (TextView)findViewById(R.id.txtShopName);
        TextView txtShopkeeperName = (TextView)findViewById(R.id.txtShopkeeperName);
        TextView txtShopAddress = (TextView)findViewById(R.id.txtShopAddress);
        TextView txtNearestLandmark = (TextView)findViewById(R.id.txtNearestLandmark);
        TextView txtNextScheduleVisit = (TextView)findViewById(R.id.txtNextScheduleVisit);
        imageView = (ImageView)findViewById(R.id.shopImg);
        imgBack = (ImageView)findViewById(R.id.btnBack);
        btnCheckin = (Button)findViewById(R.id.btncheckin) ;
        btnDirection = (Button)findViewById(R.id.btnDirection) ;

        txtShopName.setText(storeInfo.ShopKeeper);
        txtShopkeeperName.setText(storeInfo.ShopName);
        txtShopAddress.setText(storeInfo.Address);
        txtNearestLandmark.setText(storeInfo.Landmark);

        try{
            String selectedDays = utilInstance.getNumberToDays(new JSONArray(storeInfo.StoreVisitDays));
            txtNextScheduleVisit.setText(selectedDays);
        }catch (Exception e){

        }

        if(Status.equals("Achieved")){
            btnCheckin.setVisibility(View.GONE);
            btnDirection.setVisibility(View.GONE);
        }
        try{
            if(!storeInfo.IsSync){
                File file = new File(storeInfo.ImagePath);
                if(file.exists()){
                    Picasso.get().load(file).placeholder(R.mipmap.ic_loading).into(imageView);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{

            if(!storeInfo.ImgUrl.equals("") && !storeInfo.ImgUrl.equals("null")){
                String url = WebURL.host+""+storeInfo.ImgUrl;
                Picasso.get().load(url).placeholder(R.mipmap.ic_loading).into(imageView);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheckout){
                    appInstance.storeVisitsFragment.updateStoreVisit(storeInfo);
                }
                finish();
            }
        });

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopProfileAtivity.this, StoreDetailActivity.class));
                finish();
                }

        });

        if(latlng == null){
            btnDirection.setVisibility(View.INVISIBLE);
        }
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String desLocation = "&daddr=" + storeInfo.Latitude + ","
                        + storeInfo.Longitude;
                String currLocation = "saddr=" + Double.toString(latlng.latitude) + ","
                        + Double.toString(latlng.longitude);
                // "d" means driving car, "w" means walking "r" means by bus
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" + currLocation
                                + desLocation + "&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        latlng = null;
        if(isCheckout){
            appInstance.storeVisitsFragment.updateStoreVisit(storeInfo);
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        latlng = null;
        if(isCheckout){
            appInstance.storeVisitsFragment.updateStoreVisit(storeInfo);
        }
        super.onBackPressed();
    }
}
