package com.tracking.storedev.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.db.SubSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZASS on 3/20/2018.
 */

public class SubSectionAdapter extends ArrayAdapter {
    Context context;
    private List<SubSection> subSectionList;
    private List<SubSection> mFilteredList;


    public SubSectionAdapter(Context context, List<SubSection> townList) {
        super(context, R.layout.sub_section_row, townList);
        this.context = context;
        this.subSectionList = townList;
        mFilteredList = new ArrayList<>();
        this.mFilteredList.addAll(townList);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.sub_section_row, parent, false);

        TextView textView = (TextView) convertView.findViewById(R.id.textid);

        textView.setText(subSectionList.get(position).name);
        return convertView;
    }


    public void filter(String filter) {

        subSectionList.clear();
        if (filter.length() == 0) {
            subSectionList.addAll(mFilteredList);
        }
        else
        {
            for (SubSection  town : mFilteredList)
            {
                if (town.name.toLowerCase(Locale.getDefault()).contains(filter))
                {
                    subSectionList.add(town);
                }
            }
        }
        notifyDataSetChanged();

    }
}