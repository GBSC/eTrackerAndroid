package com.tracking.storedev.util;

import com.tracking.storedev.db.TrackerLogs;

/**
 * Created by ZASS on 5/8/2018.
 */

public class Logger {
    private static final Logger ourInstance = new Logger();

    public Util utilInstance = Util.getInstance();

    public static Logger getInstance() {
        return ourInstance;
    }

    public void setLog(TrackerLogs trackerLogs, String log, String status){

        if(trackerLogs != null){
            trackerLogs.Log = log;
            trackerLogs.LogDate = utilInstance.getCurrentDateAndTime();
            trackerLogs.logStatus = status;
            trackerLogs.save();
        }else{
            trackerLogs = new TrackerLogs();
            trackerLogs.Log = log;
            trackerLogs.LogDate = utilInstance.getCurrentDateAndTime();
            trackerLogs.logStatus = status;
            trackerLogs.save();
        }
    }
    
}
