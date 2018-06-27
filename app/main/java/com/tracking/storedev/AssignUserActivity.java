package com.tracking.storedev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;

public class AssignUserActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private HttpCaller httpCaller = HttpCaller.getInstance();
    private Util util = Util.getInstance();
    private DBHandler dbHandler = DBHandler.getInstance();

    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextName;
    private EditText editTextContactNumber;
    private EditText editTextDOB;
    private EditText editTextAddress;
    private EditText editTextCNIC;
    private EditText editTextRole;

    private RelativeLayout layoutImage;
    private TextView textViewSkip;

    private Button btnAddContact;
    private ImageView shopImg,btnBack;

    private String ContactNumber, Address, CNIC, ShopKeeperName;
    private long storeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_user_activity);

        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
        editTextName = (EditText)findViewById(R.id.editTextFirstName);
        editTextContactNumber = (EditText)findViewById(R.id.editTextContactNumber);
        editTextDOB = (EditText)findViewById(R.id.editTextDOB);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editTextCNIC = (EditText)findViewById(R.id.editTextCNIC);
        editTextRole = (EditText)findViewById(R.id.editTextRole);

        layoutImage = (RelativeLayout) findViewById(R.id.layout_image);

        textViewSkip = (TextView)findViewById(R.id.textViewSkip);

        btnAddContact = (Button)findViewById(R.id.btnAddContact);
        shopImg = (ImageView)findViewById(R.id.shopImg);
        btnBack = (ImageView)findViewById(R.id.btnBack);

        util.hideKeybaord(AssignUserActivity.this, editTextUserName);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adBtnClick();
            }
        });

        textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("ContactNumber"))
            ContactNumber = intent.getStringExtra("ContactNumber");

        if(intent.hasExtra("Address"))
            Address = intent.getStringExtra("Address");

        if(intent.hasExtra("storeID"))
            storeID = intent.getIntExtra("storeID", 0);

        if(intent.hasExtra("CNIC"))
            CNIC = intent.getStringExtra("CNIC");

        if(intent.hasExtra("ShopKeeperName"))
            ShopKeeperName = intent.getStringExtra("ShopKeeperName");

        if(!intent.hasExtra("StoreRegister")){
            textViewSkip.setVisibility(View.GONE);
        }

        editTextContactNumber.setText(ContactNumber);
        editTextAddress.setText(Address);
        editTextCNIC.setText(CNIC);
        editTextName.setText(ShopKeeperName);

        try{
            Store store = dbHandler.getStoreDetail(storeID);
            if(!store.ImgUrl.equals("") && !store.ImgUrl.equals("null")){
                String url = WebURL.host+""+store.ImgUrl;
                Picasso.get().load(url).placeholder(R.mipmap.ic_loading).into(shopImg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AssignUserActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void adBtnClick(){
        String teditTextUserName = editTextUserName.getText().toString();
        String teditTextEmail = editTextEmail.getText().toString();
        String teditTextPassword = editTextPassword.getText().toString();
        String teditTextConfirmPassword = editTextConfirmPassword.getText().toString();
        String teditTextFirstName = editTextName.getText().toString();
        String teditTextContactNumber = editTextContactNumber.getText().toString();
        String teditTextDOB = editTextDOB.getText().toString();
        String teditTextAddress = editTextAddress.getText().toString();
        String teditTextCNIC = editTextCNIC.getText().toString();
        String teditTextRole = editTextRole.getText().toString();

        if(!util.isNetworkConnected(AssignUserActivity.this)){
            Toast.makeText(AssignUserActivity.this, "Internet connection problem", Toast.LENGTH_SHORT).show();
        }
        else if(teditTextUserName.equals("")){
            Toast.makeText(AssignUserActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
        } else if(teditTextPassword.equals("")){
            Toast.makeText(AssignUserActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
        }else if(teditTextConfirmPassword.equals("")){
            Toast.makeText(AssignUserActivity.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
        }else if(teditTextPassword.length() < 6){
            Toast.makeText(AssignUserActivity.this, "Passwords must be at least 6 characters.", Toast.LENGTH_SHORT).show();
        }else if(teditTextConfirmPassword.length() < 6){
            Toast.makeText(AssignUserActivity.this, "Confirm Passwords must be at least 6 characters.", Toast.LENGTH_SHORT).show();
        }else if(!teditTextPassword.equals(teditTextConfirmPassword)){
            Toast.makeText(AssignUserActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
        }else{
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("storeid", storeID);
                jsonObject.put("username", teditTextUserName);
                jsonObject.put("password", teditTextPassword);
                jsonObject.put("email", ""+teditTextEmail);
                jsonObject.put("name", ""+teditTextFirstName);
                jsonObject.put("contactnumber", ""+teditTextContactNumber);
                jsonObject.put("dob", ""+teditTextDOB);
                jsonObject.put("address", ""+teditTextAddress);
                jsonObject.put("cnic", ""+teditTextCNIC);
                jsonObject.put("role", ""+teditTextRole);
            }catch (Exception e){
                e.printStackTrace();
            }

            String url = WebURL.accounts;
            httpCaller.jsonObjectPOSTRequest(true, AssignUserActivity.this, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        //syncImageFile("" + storeID);
                        Toast.makeText(AssignUserActivity.this, "Account create succesfully", Toast.LENGTH_LONG).show();
                        Store storeDetail = dbHandler.getStoreDetail(storeID);
                        storeDetail.HasShopKeeperAccount = true;
                        storeDetail.save();

                        Intent data = new Intent();
                        setResult(RESULT_OK, data);
                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AssignUserActivity.this, "Account create failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        editTextDOB.setText(monthOfYear+"-"+dayOfMonth+"-"+year);
    }
}
