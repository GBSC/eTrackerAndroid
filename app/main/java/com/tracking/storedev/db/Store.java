package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "Store")
public class Store extends Model {

    @Column(name = "StoreID")
    public long StoreID;

    @Column(name = "ShopName")
    public String ShopName;

    @Column(name = "ShopKeeper")
    public String ShopKeeper;

    @Column(name = "ContactNumber")
    public String ContactNumber;

    @Column(name = "LandLine")
    public String LandLine;

    @Column(name = "Address")
    public String Address;

    @Column(name = "Street")
    public String Street;

    @Column(name = "City")
    public String City;

    @Column(name = "CNIC")
    public String CNIC;

    @Column(name = "Landmark")
    public String Landmark;

    @Column(name = "ImgUrl")
    public String ImgUrl;

    @Column(name = "Longitude")
    public String Longitude;

    @Column(name = "Latitude")
    public String Latitude;

    @Column(name = "DayRegistered")
    public int DayRegistered;

    @Column(name = "UserID")
    public String UserID;

    @Column(name = "StartTime")
    public String StartTime;

    @Column(name = "EndTime")
    public String EndTime;

    @Column(name = "SubSectionID")
    public int SubSectionID;

    @Column(name = "AreaID")
    public int AreaID;

    @Column(name = "registerDAte")
    public String registerDAte;

    @Column(name = "Distance")
    public String Distance;

    @Column(name = "Category")
    public String Category;

    @Column(name = "Classification")
    public String Classification;

    @Column(name = "StoreVisitDays")
    public String StoreVisitDays;

    @Column(name = "VisitDays")
    public String VisitDays;

    @Column(name = "Status")
    public String Status;

    @Column(name = "ImagePath")
    public String ImagePath;

    @Column(name = "HasShopKeeperAccount")
    public boolean HasShopKeeperAccount;

    @Column(name = "IsSync")
    public boolean IsSync;

    @Column(name = "CheckOutDate")
    public String CheckOutDate;

}
