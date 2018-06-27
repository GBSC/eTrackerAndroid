package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "Names")
public class Names extends Model {

    @Column(name = "storeID")
    public long storeID;

    @Column(name = "name_")
    public String name_;

    @Column(name = "number_")
    public String number_;
}
