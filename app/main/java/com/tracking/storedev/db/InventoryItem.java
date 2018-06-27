package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "InventoryItem")
public class InventoryItem extends Model {

    @Column(name = "InventoryID")
    public int InventoryID;

    @Column(name = "Product")
    public String Product;

    @Column(name = "Qty")
    public int Qty;

    @Column(name = "StoreID")
    public long StoreID;

    @Column(name = "StoreVisitID")
    public long StoreVisitID;

    @Column(name = "DateTime")
    public Date DateTime;

    @Column(name = "IsSync")
    public boolean IsSync;


}
