package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "StoreVisit")
public class StoreVisit extends Model {

    @Column(name = "StoreVisitID")
    public long StoreVisitID;

    @Column(name = "StartTime")
    public String StartTime;

    @Column(name = "EndTime")
    public String EndTime;

    @Column(name = "Latitude")
    public String Latitude;

    @Column(name = "Longitude")
    public String Longitude;

    @Column(name = "Location")
    public String Location;

    @Column(name = "Notes")
    public String Notes;

    @Column(name = "StoreID")
    public long StoreID;

    @Column(name = "ContactName")
    public String ContactName;

    @Column(name = "ContactNumber")
    public String ContactNumber;

    @Column(name = "entryDate")
    public Date entryDate;

    @Column(name = "NextScheduledVisit")
    public String NextScheduledVisit;

    @Column(name = "Status")
    public String Status;

    @Column(name = "IsSync")
    public boolean IsSync;
}
