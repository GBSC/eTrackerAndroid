package com.tracking.storedev.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ZASS on 3/20/2018.
 */

@Table(name = "SubSection")
public class SubSection extends Model {

    @Column(name = "subsectionId")
    public int subsectionId;

    @Column(name = "name")
    public String name;
}
