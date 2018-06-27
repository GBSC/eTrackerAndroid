package com.tracking.storedev.bean;

import com.intrusoft.sectionedrecyclerview.Section;
import com.tracking.storedev.db.Store;

import java.util.List;

/**
 * Created by ZASS on 6/26/2018.
 */

public class SectionHeader implements Section<Store> {

    List<Store> childList;
    String sectionText;

    public SectionHeader(List<Store> childList, String sectionText) {
        this.childList = childList;
        this.sectionText = sectionText;
    }

    @Override
    public List<Store> getChildItems() {
        return childList;
    }

    public String getSectionText() {
        return sectionText;
    }
}