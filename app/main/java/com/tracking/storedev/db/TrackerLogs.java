package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "TrackerLogs")
public class TrackerLogs extends Model {

    @Column(name = "logStatus")
    public String logStatus;

    @Column(name = "Log")
    public String Log;

    @Column(name = "LogDate")
    public String LogDate;
}
