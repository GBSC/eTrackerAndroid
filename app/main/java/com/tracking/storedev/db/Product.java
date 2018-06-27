package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "Product")
public class Product extends Model {

    @Column(name = "ProductID")
    public int ProductID;

    @Column(name = "Product")
    public String Product;


}
