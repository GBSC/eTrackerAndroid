package com.tracking.storedev;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.db.SubSection;
import com.tracking.storedev.db.TrackerLogs;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.service.CoreService;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.DBToObject;
import com.tracking.storedev.util.GPSValue;
import com.tracking.storedev.util.Logger;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.StoreVisitsDialog;
import com.tracking.storedev.view.SubsectionSelectionDialog;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class StoreRegistrationActivity extends AppCompatActivity {

    private GPSValue gpsValue = GPSValue.getInstance();
    private HttpCaller httpCaller = HttpCaller.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    private Util utilInstance = Util.getInstance();
    private DBHandler dbHandler = DBHandler.getInstance();
    private App appInstance = App.getAppInstance();
    private DBToObject dbToObject = DBToObject.getInstance();
    private Logger logger = Logger.getInstance();

    private JSONArray visitJsonArray = null;

    private EditText txtShopName;
    private EditText edittext_shopkeeper;
    private EditText edittext_contactno;
    private EditText edittext_landline;
    private EditText edittext_address;
    private EditText edittext_street;
    private EditText edittext_city;
    private EditText edittext_cnic;
    private EditText edittext_landmark;
    private TextView txtFileName, txtDaysSelection;

    MaterialSpinner catgpinner, classificatioin_spinner;

    private RadioGroup statusRadioGroup;

    private TextView idSubsection;
    private RelativeLayout attachLayout, dropdown_Subsection, dropdownDays;
    private Button btn_add, checkInOut;

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    private File mFileTemp;
    public String filePath = "";
    public String lati = "0.0", longi = "0.0";
    public String fileName = "";

    public String selectedCategory = "Select Category";
    public String selectedClassification = "Select Classification";
    public String storeVisitDays = "";

    public boolean selectedStatus = false;

    private String startTime, endTime;
    public int selectedSubSectionID = 0;

    public boolean disableView = false;

    private ImageView imgBack;

    Store store = null;
    public TrackerLogs trackerLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_registration);

        checkInOut = (Button) findViewById(R.id.toolbar_checkin);

        txtShopName = (EditText) findViewById(R.id.txtShopName);
        edittext_shopkeeper = (EditText) findViewById(R.id.edittext_shopkeeper);
        edittext_contactno = (EditText) findViewById(R.id.edittext_contactno);
        edittext_landline = (EditText) findViewById(R.id.edittext_landline);
        edittext_address = (EditText) findViewById(R.id.edittext_address);
        edittext_street = (EditText) findViewById(R.id.edittext_street);
        edittext_city = (EditText) findViewById(R.id.edittext_city);
        edittext_cnic = (EditText) findViewById(R.id.edittext_cnic);
        edittext_landmark = (EditText) findViewById(R.id.edittext_landmark);

        txtFileName = (TextView) findViewById(R.id.txtFileName);
        idSubsection = (TextView) findViewById(R.id.idSubsection);
        txtDaysSelection = (TextView) findViewById(R.id.txtDaysSelection);

        imgBack = (ImageView) findViewById(R.id.btnBack);

        statusRadioGroup = (RadioGroup) findViewById(R.id.classficationRadioGroup);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                5000);

        btn_add = (Button) findViewById(R.id.btn_add);

        attachLayout = (RelativeLayout) findViewById(R.id.attach_layout);
        dropdown_Subsection = (RelativeLayout) findViewById(R.id.dropdown_Subsection);
        dropdownDays = (RelativeLayout) findViewById(R.id.dropdown_Days);

        attachLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileTemp = utilInstance.createImageFile(TEMP_PHOTO_FILE_NAME);
                if (mFileTemp != null) {
                    takePictureFromCamera();
                } else {
                    Toast.makeText(StoreRegistrationActivity.this, "Image store failed", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dropdown_Subsection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idSubsection.setTextColor(getResources().getColor(R.color.black));
                getSubSection();
            }
        });


        dropdownDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StoreVisitsDialog(StoreRegistrationActivity.this, new StoreVisitsDialog.OrderCallback() {
                    @Override
                    public void OrderGetter(JSONArray jsonArray) {
                        if (jsonArray.length() > 0) {
                            String selectedDays = utilInstance.getNumberToDays(jsonArray);
                            txtDaysSelection.setVisibility(View.VISIBLE);
                            txtDaysSelection.setText("" + selectedDays);

                            visitJsonArray = jsonArray;
                            storeVisitDays = utilInstance.jsonArrayToString(jsonArray);
                        }
                    }
                }).show();
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ShopName = txtShopName.getText().toString();
                final String shopKeeper = edittext_shopkeeper.getText().toString();
                final String contactNumber = edittext_contactno.getText().toString();
                final String landLine = edittext_landline.getText().toString();
                final String address = edittext_address.getText().toString();
                final String street = edittext_street.getText().toString();
                final String city = edittext_city.getText().toString();
                final String cnic = edittext_cnic.getText().toString();
                final String landmark = edittext_landmark.getText().toString();
                final int userID = prefManager.getUserID();


                if (ShopName.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Shop Name", Toast.LENGTH_SHORT).show();
                } else if (shopKeeper.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Shopkeeper", Toast.LENGTH_SHORT).show();
                } else if (contactNumber.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Contact Number", Toast.LENGTH_SHORT).show();
                } else if (selectedSubSectionID == 0) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Select SubSection", Toast.LENGTH_SHORT).show();
                } else if (address.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                } else if (street.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Street", Toast.LENGTH_SHORT).show();
                } else if (city.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                } else if (landmark.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Enter Landmark", Toast.LENGTH_SHORT).show();
                } else if (cnic.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please CNIC Number", Toast.LENGTH_SHORT).show();
                } else if (storeVisitDays.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Select Visits Days", Toast.LENGTH_SHORT).show();
                } else if (selectedCategory.trim().equals("Select Category")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Select Category", Toast.LENGTH_SHORT).show();
                } else if (selectedClassification.trim().equals("Select Classification")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Select Classification", Toast.LENGTH_SHORT).show();
                } else if (filePath.trim().equals("")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                } else if (lati.equals("0.0") && longi.equals("0.0")) {
                    Toast.makeText(StoreRegistrationActivity.this, "Getting GPS Coordinate Failed", Toast.LENGTH_SHORT).show();
                } else {
                    store = saveStoreInDB();
                    if (store != null)
                        trackerLog = new TrackerLogs();

                    logger.setLog(trackerLog, store.ShopName + "Offline Store Registered", "Success");

                    if (utilInstance.isNetworkConnected(StoreRegistrationActivity.this)) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = dbToObject.storeToJSONObject(store, userID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonObject != null) {
                            String URL = WebURL.storeRegistration;
                            httpCaller.jsonObjectPOSTRequest(true, StoreRegistrationActivity.this, URL, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int storeID = response.getInt("id");
                                        store.StoreID = storeID;
                                        store.IsSync = true;
                                        store.save();

                                        logger.setLog(trackerLog, store.ShopName + " Store Registered with web", "Success");

                                        syncImageFile("" + storeID, store);

                                        resetFields();

                                        Intent intent = new Intent(StoreRegistrationActivity.this, AssignUserActivity.class);
                                        intent.putExtra("ContactNumber", store.ContactNumber);
                                        intent.putExtra("Address", store.Address);
                                        intent.putExtra("ShopKeeperName", store.ShopKeeper);
                                        intent.putExtra("CNIC", store.CNIC);
                                        intent.putExtra("storeID", storeID);
                                        startActivity(intent);
                                        finish();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(StoreRegistrationActivity.this, "Store registered failed", Toast.LENGTH_SHORT).show();
                                    logger.setLog(trackerLog, store.ShopName + " Store registered failed, network failed", "Failed");
                                }
                            });
                        }
                    } else {
                        Toast.makeText(StoreRegistrationActivity.this, "Store registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });


        checkInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SubSection> subSectionList = dbHandler.getSubsection();
                boolean isSaleUserType = (boolean) prefManager.get(Constants.IS_SALE_USER, new Boolean(false));

                if (!utilInstance.isNetworkConnected(StoreRegistrationActivity.this)) {
                    getOfflineGPS();
                }

                if (isSaleUserType) {
                    if (!utilInstance.isGPSEnable(StoreRegistrationActivity.this)) {
                        Toast.makeText(StoreRegistrationActivity.this, "Please enable GPS Location", Toast.LENGTH_SHORT).show();
                    } else if (lati.equals("0.0") && longi.equals("0.0")) {
                        Toast.makeText(StoreRegistrationActivity.this, "GPS Coordinates getting failed, Please restart mobile", Toast.LENGTH_SHORT).show();
                    } else if (subSectionList.size() == 0) {
                        Toast.makeText(StoreRegistrationActivity.this, "Please contact with admin to assign Subsection", Toast.LENGTH_SHORT).show();
                    } else {
                        startTime = utilInstance.getCurrentTime();
                        checkInOut.setVisibility(View.GONE);
                        enableOrDisableView(true);
                    }
                    enableOrDisableView(true);
                } else {
                    Toast.makeText(StoreRegistrationActivity.this, "Only Sales person can register stores OR the Distributer is not assigned", Toast.LENGTH_SHORT).show();
                }


            }
        });
        if (isGPSPermissionGranted()) {
            if (utilInstance.isGPSEnable(StoreRegistrationActivity.this)) {
                getLocation();
            } else {
                gpsLocationDialog();
            }

        } else {
            Toast.makeText(StoreRegistrationActivity.this, "GPS permission granted", Toast.LENGTH_SHORT).show();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ShopName = txtShopName.getText().toString();
                final String shopKeeper = edittext_shopkeeper.getText().toString();
                final String contactNumber = edittext_contactno.getText().toString();
                final String landLine = edittext_landline.getText().toString();
                final String address = edittext_address.getText().toString();
                final String street = edittext_street.getText().toString();
                final String city = edittext_city.getText().toString();
                final String cnic = edittext_cnic.getText().toString();
                final String landmark = edittext_landmark.getText().toString();


                if (!ShopName.equals("") || !shopKeeper.equals("") || !contactNumber.equals("") || !landLine.equals("") || !address.equals("") || !street.equals("") || !city.equals("") || !cnic.equals("") || !landmark.equals("")) {
                    new MaterialDialog.Builder(StoreRegistrationActivity.this).content("Do you want to discard?").backgroundColor(getResources().getColor(R.color.white)).contentColor(getResources().getColor(R.color.black))
                            .positiveText("Yes").positiveColor(Color.parseColor("#01a301"))
                            .cancelable(false)
                            .negativeText("No").negativeColor(Color.parseColor("#ff0000")).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            StoreRegistrationActivity.super.onBackPressed();
                        }
                    }).show();
                } else {
                    finish();
                }
            }
        });
        initView();
        enableOrDisableView(disableView);
        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 5000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                Toast.makeText(StoreRegistrationActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public Store saveStoreInDB() {

        long id = dbHandler.getUniqueID("Store");

        final String ShopName = txtShopName.getText().toString();
        final String shopKeeper = edittext_shopkeeper.getText().toString();
        final String contactNumber = edittext_contactno.getText().toString();
        final String landLine = edittext_landline.getText().toString();
        final String address = edittext_address.getText().toString();
        final String street = edittext_street.getText().toString();
        final String city = edittext_city.getText().toString();
        final String cnic = edittext_cnic.getText().toString();
        final String landmark = edittext_landmark.getText().toString();

        final boolean isInternetConnection = utilInstance.isNetworkConnected(StoreRegistrationActivity.this);

        Store store = new Store();
        store.StoreID = id;
        store.ShopName = ShopName;
        store.ShopKeeper = shopKeeper;
        store.ContactNumber = contactNumber;
        store.LandLine = landLine;
        store.Address = address;
        store.Street = street;
        store.City = city;
        store.Landmark = landmark;
        store.DayRegistered = utilInstance.getDayOfWeek();
        store.Classification = selectedClassification;
        store.Latitude = lati;
        store.Longitude = longi;
        store.CNIC = cnic;
        store.StartTime = startTime;
        store.EndTime = utilInstance.getCurrentTime();
        store.registerDAte = utilInstance.getCurrentDate();
        store.VisitDays = storeVisitDays;
        store.StoreVisitDays = visitJsonArray.toString();
        store.Status = "Target";
        store.SubSectionID = selectedSubSectionID;
        store.Category = selectedCategory;
        store.ImagePath = filePath;
        if (isInternetConnection) {
            store.IsSync = true;
        }
        store.save();

        if (isInternetConnection) {
            appInstance.mapFragment.refreshMap();
        }
        appInstance.storeVisitsFragment.refreshStoreVisit(store);
        return store;
    }

    public void resetFields() {
        txtShopName.setText("");
        edittext_shopkeeper.setText("");
        edittext_contactno.setText("");
        edittext_landline.setText("");
        edittext_address.setText("");
        edittext_street.setText("");
        edittext_city.setText("");
        edittext_cnic.setText("");
        edittext_landmark.setText("");
        txtFileName.setText("");

        selectedSubSectionID = 0;
    }

    public void initView() {
        final List<String> catgDataSet = new LinkedList<>(Arrays.asList("Select Category", "LMT", "W/S", "Retail"));
        final List<String> classificationDataSet = new LinkedList<>(Arrays.asList("Select Classification", "LMTs", "500 above", "250 to 499", "100 to 249", "Less then 100"));


        catgpinner = (MaterialSpinner) findViewById(R.id.catg_spinner);
        classificatioin_spinner = (MaterialSpinner) findViewById(R.id.classificatioin_spinner);

        catgpinner.setPadding(0, 10, 0, 10);
        classificatioin_spinner.setPadding(0, 10, 0, 10);


        catgpinner.setItems(catgDataSet);
        classificatioin_spinner.setItems(classificationDataSet);

        catgpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedCategory = catgDataSet.get(position);
            }
        });

        classificatioin_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedClassification = classificationDataSet.get(position);
            }
        });

        statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonActive) {
                    selectedStatus = true;
                } else {
                    selectedStatus = false;
                }
            }
        });
    }

    public void getSubSection() {
        List<SubSection> townList = dbHandler.getSubsection();
        if (townList == null)
            return;
        SubsectionSelectionDialog townAreadDialog = new SubsectionSelectionDialog(StoreRegistrationActivity.this, "Town", townList, new SubsectionSelectionDialog.SelectionCallBack() {
            @Override
            public void selection(SubSection selection) {
                idSubsection.setText(selection.name);
                selectedSubSectionID = selection.subsectionId;
            }
        });
        townAreadDialog.show();
    }

    public void getLocation() {
        SimpleLocation location = gpsValue.getLocation();
        if (location != null) {
            lati = location.getLatitude() + "";
            longi = location.getLongitude() + "";
        } else {
            location = MainActivity.locationInstance;
            if (location == null)
                return;

            lati = location.getLatitude() + "";
            longi = location.getLongitude() + "";
        }
    }

    public void enableOrDisableView(boolean value) { // false mean disable
        txtShopName.setEnabled(value);
        edittext_shopkeeper.setEnabled(value);
        edittext_contactno.setEnabled(value);
        edittext_landline.setEnabled(value);
        edittext_address.setEnabled(value);
        edittext_street.setEnabled(value);
        edittext_city.setEnabled(value);
        edittext_cnic.setEnabled(value);
        edittext_landmark.setEnabled(value);
        txtFileName.setEnabled(value);
        attachLayout.setEnabled(value);
        btn_add.setEnabled(value);
        dropdown_Subsection.setEnabled(value);
        statusRadioGroup.setEnabled(value);
        catgpinner.setEnabled(value);
        classificatioin_spinner.setEnabled(value);
        dropdownDays.setEnabled(value);
    }

    public boolean isGPSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(StoreRegistrationActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void syncImageFile(final String storeID, final Store store) {
        String url = WebURL.fileUpload + "/" + storeID;
        httpCaller.uploadImage(url, filePath, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String imgUrl = response.getString("filepath");
                        store.ImgUrl = imgUrl;
                        store.save();
                        txtFileName.setText("");

                        logger.setLog(null, store.ShopName + " image uploaded to server", "Image Uploaded");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        logger.setLog(null, store.ShopName + " image upload failed", "Image Upload failed");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logger.setLog(null, store.ShopName + " image upload failed" + error.getMessage(), "Image Upload failed");
            }
        });
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(StoreRegistrationActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    5000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //GPS On response
        if (requestCode == 100) {
            getLocation();
        } else if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {

            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                filePath = finalFile.getAbsolutePath();
                fileName = finalFile.getName();
                txtFileName.setText(fileName);

            } catch (Exception e) {
                Toast.makeText(StoreRegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            //runCropImage(2001);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void takePictureFromCamera() {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = Uri.parse("content://eu.janmuller.android.simplecropimage.example/");

            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 2000);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }*/

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void gpsLocationDialog() {
        new MaterialDialog.Builder(StoreRegistrationActivity.this).content("Turn on locationInstance services to determine your locationInstance").contentColor(getResources().getColor(R.color.black)).backgroundColor(getResources().getColor(R.color.white))
                .positiveText("Setting")
                .negativeText("Cancel").negativeColor(Color.parseColor("#ff0000")).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
            }
        }).show();
    }

    public void getOfflineGPS() {
        startService(new Intent(StoreRegistrationActivity.this, CoreService.class));
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
