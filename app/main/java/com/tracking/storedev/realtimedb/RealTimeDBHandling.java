package com.tracking.storedev.realtimedb;

import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tracking.storedev.util.Util;

import java.util.Map;

/**
 * Created by ZASS on 4/10/2018.
 */

public class RealTimeDBHandling {
    private static final RealTimeDBHandling ourInstance = new RealTimeDBHandling();
    private Util utilInstance = Util.getInstance();


    public static RealTimeDBHandling getInstance() {
        return ourInstance;
    }

   public void realTimeCurrentLocation(FirebaseFirestore db, String userID, Map<String, Object> user, final Context context){
       db.collection("tbl_users")
               .document(""+userID)
               .set(user)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       //Toast.makeText(context, "Current Location Synced", Toast.LENGTH_SHORT).show();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w("TAG", "Error adding document", e);
                   }
               });
   }

    public void RealTimeUserLocationHistory(FirebaseFirestore db, String userID, Map<String, Object> user, final Context context){
        String randomDocument = utilInstance.getRandomString();
        db.collection("tbl_users")
                .document(""+userID)
                .collection("user_history")
                .document(randomDocument)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       //Toast.makeText(context, "History location is synced", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public void setMileAge(FirebaseFirestore db, String userID, Map<String, Object> mileAgeObj){
        userID = "65";
        String randomDocument = utilInstance.getRandomString();
        db.collection("tbl_users")
                .document(""+userID)
                .collection("mileage")
                .document(randomDocument)
                .set(mileAgeObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Test");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

}
