package com.tracking.storedev.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tracking.storedev.SettingsActivity;
import com.tracking.storedev.realtimedb.RealTimeDBHandling;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class CoreService extends Service {

    private static CoreService coreService = new CoreService();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PrefManager prefManager = PrefManager.getPrefInstance();
    private RealTimeDBHandling realTimeDBHandling = RealTimeDBHandling.getInstance();
    private Util utilInstance = Util.getInstance();

    public static CoreService coreInstance;
    private Handler syncLocationHandler;
    private Runnable myRunnable;
    private static SimpleLocation locationInstance;

    private static double startLatitude;
    private static double startLongitude;

    private static double startLastLatitude = 0.0;
    private static double startLastLongitude = 0.0;
    double lastDistanceDouble = 0.0;


    public static CoreService getCoreService() {
        return coreService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLocation();
        coreInstance = this;
        return super.onStartCommand(intent, flags, startId);
    }


    public void updateLocation() {
        long updateIntervalInMilliseconds = 1 * 5 * 1000;

        locationInstance = new SimpleLocation(this, false, false, updateIntervalInMilliseconds, true);
        if (locationInstance == null)
            return;

        locationInstance.setListener(new SimpleLocation.Listener() {
            @Override
            public void onPositionChanged() {
                startLatitude = locationInstance.getLatitude();
                startLongitude = locationInstance.getLongitude();

                double distanceCov = 0.0;
                if(startLastLatitude == 0.0 && startLastLongitude == 0.0)
                    distanceCov = locationInstance.calculateDistance(startLatitude, startLongitude, startLatitude, startLongitude);
                else
                    distanceCov = locationInstance.calculateDistance(startLatitude, startLongitude, startLastLatitude, startLastLongitude);

                if(distanceCov > .5){
                    String lastPreferenceDistance = (String)prefManager.get(Constants.DISTANCE_COVERED, "");
                    if(lastPreferenceDistance.equals(""))
                        lastDistanceDouble = 0.0;
                    else
                        lastDistanceDouble = Double.parseDouble(lastPreferenceDistance);

                    double totalDistance = lastDistanceDouble+distanceCov;
                    prefManager.put(Constants.DISTANCE_COVERED, totalDistance+"");
                }

                initRealTimeFirebase();
                startLastLatitude = locationInstance.getLatitude();
                startLastLongitude = locationInstance.getLongitude();

            }
        });
        locationInstance.beginUpdates();
    }

    public void initRealTimeFirebase() {
        Map<String, Object> userObject = getUserHashMapObject(locationInstance);
        double distance = locationInstance.calculateDistance(startLatitude, startLongitude, locationInstance.getLatitude(), locationInstance.getLongitude());
        if (distance >= 1 || true) {
            realTimeDatabase(userObject); // for realTime Tracking
            historyLocationSync(userObject); // for tracking history

        }
    }

    public void realTimeDatabase(Map<String, Object> user) {
        if (user != null) {
            int userID = prefManager.getUserID();
            realTimeDBHandling.realTimeCurrentLocation(db, "" + userID, user, CoreService.this);
        }
    }

    public void historyLocationSync(final Map<String, Object> user) {
        if (user != null) {
            syncLocationHandler = new Handler();
            final int intervalTime = 1 * 10 * 1000; //3 minutes
            myRunnable = new Runnable() {
                public void run() {
                    int userID = prefManager.getUserID();
                    if (utilInstance.isNetworkConnected(CoreService.this))
                        try {
                           realTimeDBHandling.RealTimeUserLocationHistory(db, "" + userID, user, CoreService.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            };
            syncLocationHandler.postDelayed(myRunnable, intervalTime);
        }
    }

    public void stopService(SettingsActivity context) {
        context.stopService(new Intent(context, CoreService.class));
        if (syncLocationHandler != null) {
            syncLocationHandler.removeCallbacks(myRunnable);
        }
        if(locationInstance != null){
            locationInstance.endUpdates();
        }
    }

    public void stopService() {
        if (locationInstance != null) {
            locationInstance.endUpdates();
        }

        if (syncLocationHandler != null) {
            syncLocationHandler.removeCallbacks(myRunnable);
        }

    }

    public Map<String, Object> getUserHashMapObject(SimpleLocation simpleLocation) {
        int userID = prefManager.getUserID();
        Map<String, Object> user = new HashMap<>();
        user.put("lat", "" + simpleLocation.getLatitude());
        user.put("lng", "" + simpleLocation.getLongitude());
        user.put("timestamp", utilInstance.getCurrentDateIntoUTC());
        user.put("userid", userID);
        return user;
    }
}
