package com.tracking.storedev.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {


    public static SharedPreferences sharedPreferences;
    public static PrefManager prefManager;

    public static void getInstance(Context context){
        sharedPreferences = context.getSharedPreferences("eTracking", Context.MODE_PRIVATE);
    }

    public static PrefManager getPrefInstance() {
        if(sharedPreferences != null){
            prefManager = new PrefManager();
            return prefManager;
        }else{
            return null;
        }
    }

    public void logout() {
        if(sharedPreferences != null){
            sharedPreferences.edit().remove("Filter").commit();
            sharedPreferences.edit().remove("IsLogin").commit();
            sharedPreferences.edit().remove("UserTockin").commit();
            sharedPreferences.edit().remove("UserID").commit();
            sharedPreferences.edit().remove("UserTockin").commit();
        }
    }

    public void removeRememberdData(){
        sharedPreferences.edit().remove("RemEmail").commit();
        sharedPreferences.edit().remove("RemPass").commit();
    }
    public void setLogin(boolean isLogin) {
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("IsLogin", isLogin);
            editor.commit();
        }
    }

    public void setRememberdPass(String email, String pass) {
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RemEmail", email);
            editor.putString("RemPass", pass);
            editor.commit();
        }
    }



    public String[] getLoginData(){
        String emailPass[] = new String[2];
        if(sharedPreferences != null){
            emailPass[0] =  sharedPreferences.getString("RemEmail", "");
            emailPass[1] =  sharedPreferences.getString("RemPass", "");
            return emailPass;
        }else{
            return null;
        }
    }

    public void setUserTockin(String tockin) {
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserTockin", tockin);
            editor.commit();
        }
    }

    public void setUserID(int userID) {
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("UserID", userID);
            editor.commit();
        }
    }

    public int getUserID(){
        if(sharedPreferences != null){
            return sharedPreferences.getInt("UserID", 0);
        }else{
            return 0;
        }
    }


    public boolean isLogin() {
        if(sharedPreferences != null){
            return sharedPreferences.getBoolean("IsLogin", false);
        }else{
            return false;
        }

    }

    public Object get(String key, Object type) {
        if (sharedPreferences != null) {
            if (type instanceof String) {
                return sharedPreferences.getString(key, "");
            } else if (type instanceof Integer) {
                return sharedPreferences.getInt(key, 0);
            } else if (type instanceof Boolean) {
                return sharedPreferences.getBoolean(key, false);
            }
        }
        return null;
    }

    public void put(String key, Object object){
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(object instanceof Integer){
                int value = (Integer)object;
                editor.putInt(key, value);
            }else if(object instanceof String){
                String value = (String)object;
                editor.putString(key, value);
            }else if(object instanceof Boolean){
                boolean value = (Boolean)object;
                editor.putBoolean(key, value);
            }
            editor.commit();
        }
    }
}