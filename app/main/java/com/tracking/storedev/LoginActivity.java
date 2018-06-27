package com.tracking.storedev;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.ViewGroups;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebCore;
import com.tracking.storedev.web.WebURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    public HttpCaller httpCaller = HttpCaller.getInstance();
    public Util utilInstance = Util.getInstance();
    public WebCore webCore = WebCore.getInstance();
    public DBHandler dbHandler = DBHandler.getInstance();
    private ViewGroups viewGroup = ViewGroups.getInstance();

    private Button btnLogin;
    private EditText editTextEmail;
    private EditText editTextPass;
    private CheckBox checkBoxRememberdPass;

    int TerritoryId = 0;

    public PrefManager prefManager = PrefManager.getPrefInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean isLogin = prefManager.isLogin();

        if (isLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        editTextEmail = (EditText) findViewById(R.id.edittext_userid);
        editTextPass = (EditText) findViewById(R.id.edittext_pass);

        checkBoxRememberdPass = (CheckBox) findViewById(R.id.checkBoxRemembPass);

        btnLogin = (Button) findViewById(R.id.btn_login);

        if (!prefManager.getLoginData()[0].equals("") && !prefManager.getLoginData()[1].equals("")) {
            String loginData[] = prefManager.getLoginData();
            editTextEmail.setText("" + loginData[0]);
            editTextPass.setText("" + loginData[1]);
            checkBoxRememberdPass.setChecked(true);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final String userID = editTextEmail.getText().toString();
                final String userPass = editTextPass.getText().toString();

                if (!utilInstance.isNetworkConnected(LoginActivity.this)) {

                    String prefID = prefManager.getLoginData()[0];
                    String prefPass = prefManager.getLoginData()[1];

                    if(userID.equals(prefID) && userPass.equals(prefPass)){

                        prefManager.setLogin(true);

                        boolean isChecked = checkBoxRememberdPass.isChecked();
                        if (isChecked) {
                            prefManager.setRememberdPass(userID, userPass);
                        } else {
                            prefManager.removeRememberdData();
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Internet connection problem", Toast.LENGTH_SHORT).show();
                    }
                } else if (TextUtils.isEmpty(userID)) {
                    Toast.makeText(LoginActivity.this, "Please enter username ", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userPass)) {
                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject();
                        jsonObject.put("username", userID);
                        jsonObject.put("password", userPass);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final ProgressDialog progressDialog = viewGroup.progressDialog(LoginActivity.this, false);
                    progressDialog.show();
                    if (jsonObject != null)
                        httpCaller.requestToServerForLogin(jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("status")) {

                                        boolean isChecked = checkBoxRememberdPass.isChecked();
                                        if (isChecked) {
                                            prefManager.setRememberdPass(userID, userPass);
                                        } else {
                                            prefManager.removeRememberdData();
                                        }


                                        JSONObject responseJson = response.getJSONObject("user");
                                        if (responseJson != null) {

                                            final int userId = responseJson.getInt("userid");
                                            final String firstName = responseJson.getString("firstname");
                                            final String lastName = responseJson.getString("lastname");

                                            try{
                                                TerritoryId = responseJson.getInt("territoryid");
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                            final String email = responseJson.getString("email");

                                            String name = firstName + " " + lastName;

                                            prefManager.put("FullName", name);
                                            prefManager.put("Email", email);


                                            prefManager.setLogin(true);
                                            prefManager.setUserID(userId);

                                            String url = WebURL.getalluserstores + "" + userId;

                                            httpCaller.requestArrayGETToServer(false, LoginActivity.this, url, new Response.Listener<JSONArray>() {
                                                @Override
                                                public void onResponse(JSONArray jsonArrayResponse) {
                                                    try {

                                                        if(jsonArrayResponse.length() == 0){
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                        for (int i = 0; i < jsonArrayResponse.length(); i++) {
                                                            JSONObject jsonRegisteredShop = jsonArrayResponse.getJSONObject(i);

                                                            int storeID = jsonRegisteredShop.getInt("storeId");
                                                            String shopName = jsonRegisteredShop.getString("shopName");
                                                            String shopKeeper = jsonRegisteredShop.getString("shopKeeper");
                                                            String contactNo = jsonRegisteredShop.getString("contactNo");
                                                            String street = jsonRegisteredShop.getString("street");
                                                            String city = jsonRegisteredShop.getString("city");
                                                            String landLine = jsonRegisteredShop.getString("landline");
                                                            String address = jsonRegisteredShop.getString("address");
                                                            String cnic = jsonRegisteredShop.getString("cnic");
                                                            String landmark = jsonRegisteredShop.getString("landMark");
                                                            int dayRegistered = jsonRegisteredShop.getInt("dayRegistered");
                                                            String imageUrl = jsonRegisteredShop.getString("imageUrl");
                                                            String latitude = jsonRegisteredShop.getString("latitude");
                                                            String longitude = jsonRegisteredShop.getString("longitude");
                                                            String visitDays = jsonRegisteredShop.getString("visitDays");
                                                            int subsectionId = jsonRegisteredShop.getInt("subsectionId");
                                                            String storeVisitDayString  = utilInstance.jsonArrayToString(new JSONArray(visitDays));;

                                                            int day = utilInstance.getDayOfWeek();
                                                            final Store store = new Store();
                                                            store.StoreID = storeID;
                                                            store.ShopName = shopName;
                                                            store.ShopKeeper = shopKeeper;
                                                            store.ContactNumber = contactNo;
                                                            store.LandLine = landLine;
                                                            store.Address = address;
                                                            store.Street = street;
                                                            store.City = city;
                                                            store.Landmark = landmark;
                                                            store.DayRegistered = dayRegistered;
                                                            store.CNIC = cnic;
                                                            store.IsSync = true;
                                                            store.ImgUrl = imageUrl;
                                                            store.Latitude = latitude;
                                                            store.Longitude = longitude;
                                                            store.SubSectionID = subsectionId;
                                                            store.StoreVisitDays = visitDays;
                                                            store.VisitDays = storeVisitDayString;
                                                            store.IsSync = true;

                                                            if (storeVisitDayString.contains(""+day)){
                                                                String url = WebURL.getMostRecenetStoreVisit+storeID;
                                                                httpCaller.request1GETToServer(false, LoginActivity.this, url, new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        try {
                                                                            int storeId = response.getInt("storeId");
                                                                            if(store.StoreID == storeId){
                                                                                store.Status = "Achieved";
                                                                            }else{
                                                                                store.Status = "Target";
                                                                            }
                                                                            store.save();
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        error.getMessage();
                                                                    }
                                                                });
                                                            }
                                                            store.save();
                                                        }

                                                        getAllStoresForAccountRegistration();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        progressDialog.dismiss();
                                                    }

                                                    if (TerritoryId != 0) {
                                                        prefManager.put(Constants.IS_SALE_USER, true);
                                                        webCore.getSubSectionByUserID(LoginActivity.this, userId, new WebCore.WebEqecuteCallBack() {
                                                            @Override
                                                            public void finishResponse() {
                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                                finish();
                                                            }

                                                            @Override
                                                            public void updateToDateResponse() {

                                                            }
                                                        });
                                                    } else {
                                                        progressDialog.dismiss();
                                                        prefManager.put(Constants.IS_SALE_USER, false);
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }


                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Login failed, Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        String msg = response.getString("message");
                                        viewGroup.showSnackMessage(v, "" + msg, 5000);
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

    public void getAllStoresForAccountRegistration(){
        int userID = prefManager.getUserID();
        String url = WebURL.GetAllStoresForAccountRegistration+userID;
        httpCaller.requestArrayGETToServer(false, LoginActivity.this, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArrayResponse) {
                if(jsonArrayResponse.length() == 0){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                for (int i = 0; i < jsonArrayResponse.length(); i++) {
                    try {
                        JSONObject jsonRegisteredShop = jsonArrayResponse.getJSONObject(i);
                        int storeID = jsonRegisteredShop.getInt("storeId");
                        boolean hasShopKeeperAccount = jsonRegisteredShop.getBoolean("hasShopKeeperAccount");
                        Store storeDetail = dbHandler.getStoreDetail(storeID);
                        storeDetail.HasShopKeeperAccount = hasShopKeeperAccount;
                        storeDetail.save();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
