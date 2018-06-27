package com.tracking.storedev.util;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.activeandroid.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by irfan on 3/19/18.
 */

public class Util {

    public static Util util = new Util();
    private final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    public static Util getInstance() {
        return util;
    }

    public String getNumberToDays(JSONArray jsonArray){
        String days = "";
        for(int i = 0; i < jsonArray.length(); i++){
            try{
                int number = jsonArray.getJSONObject(i).getInt("day");
                if(number == 2){
                    days += "Monday";
                }
                if(number == 3){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Tuesday";
                }
                if(number == 4){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Wednesday";
                }

                if(number == 5){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Thursday";
                }

                if(number == 6){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Friday";
                }

                if(number == 7){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Saturday";
                }

                if(number == 1){
                    if(!days.equals("")) {
                        days += ", ";
                    }
                    days += "Sunday";
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return days;
    }

    public String getNumberToDay(int number){
        String days = "";
        try{
            if(number == 2){
                days = "Monday";
            }
            if(number == 3){
                days = "Tuesday";
            }
            if(number == 4){
                days = "Wednesday";
            }

            if(number == 5){
                days = "Thursday";
            }

            if(number == 6){
                days = "Friday";
            }

            if(number == 7){
                days = "Saturday";
            }

            if(number == 1){
                days = "Sunday";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return days;
    }

    public int getDayOfWeek(){
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); //Sunday is 1 and Monday is 2, Saturday is 7
        return dayOfWeek;
    }

    public File createImageFile(String fileName) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "eTrackert");
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(fileName, ".jpg", myDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getDayByDayID(){
        DateFormat format2=new SimpleDateFormat("EEEE");
        return  format2.format(new Date());
    }

    public double meterToMiles(double meters){
        double totalMiles = meters / 1609.34;
        return totalMiles;
    }

    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        return sdf.format(new Date());
    }

    public String jsonArrayToString(JSONArray jsonArray){
        String str = "";
       for(int i = 0; i < jsonArray.length(); i++){
           try{
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               String value = ""+jsonObject.getInt("day");
               str += value+",";
           }catch (Exception e){
               e.printStackTrace();
           }
       }
       return str;
    }

    public Date getCurrentDateIntoUTC() {
        Date date = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        try {
            return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }

    public String getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        return sdf.format(new Date());
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public boolean isGPSEnable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public int getUniqueID() {
        Random r = new Random();
        return r.nextInt(50000 - 500 + 1) + 500;
    }

    public String getRandomString() {
        final int sizeOfRandomString = 20;
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void hideKeybaord(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getData() {
        try {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), "eTrackert");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            File f = new File(myDirectory.getPath() + "/" + "valus.json");
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
