package com.tracking.storedev.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.squareup.picasso.Picasso;
import com.tracking.storedev.R;
import com.tracking.storedev.bean.SectionHeader;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.web.WebURL;

import java.io.File;
import java.util.List;

/**
 * Created by ZASS on 6/26/2018.
 */

public class ShopSectionRecyclerAdapter extends SectionRecyclerViewAdapter<SectionHeader, Store, ShopSectionRecyclerAdapter.SectionViewHolder, ShopSectionRecyclerAdapter.ChildViewHolder> {

    Activity context;
    List<SectionHeader> sectionItemList;

    private ShopSectionRecyclerAdapter.OnItemClickListener onItemClickListener;

    public ShopSectionRecyclerAdapter(Activity context, List<SectionHeader> sectionItemList) {
        super(context, sectionItemList);
        this.context = context;
        this.sectionItemList = sectionItemList;
    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_header, sectionViewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journey_row, childViewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, SectionHeader section) {
        sectionViewHolder.name.setText(section.getSectionText());
    }

    public void refreshStoreVisist(List<SectionHeader> stores){
        sectionItemList.clear();
        notifyDataChanged(stores);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int sectionPosition, int childPosition, final Store child) {

        childViewHolder.txtShop.setText(child.ShopName+" - "+child.ShopKeeper);
        childViewHolder.txt_address.setText(child.City);
        childViewHolder.txtDistance.setText(child.Distance);

        try{
            if(!child.IsSync){
                File file = new File(child.ImagePath);
                if(file.exists()){
                    Picasso.get().load(file).placeholder(R.mipmap.ic_loading).into(childViewHolder.storeImg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{

            if(!child.ImgUrl.equals(null)){
                String url = WebURL.host+""+child.ImgUrl;
                Picasso.get().load(url).placeholder(R.mipmap.ic_logo
                ).into(childViewHolder.storeImg);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        childViewHolder.callDailgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCallForward = new Intent(Intent.ACTION_DIAL); // ACTION_CALL
                Uri uri2 = Uri.fromParts("tel", child.ContactNumber, "#");
                intentCallForward.setData(uri2);
                context.startActivity(intentCallForward);
            }
        });
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public SectionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.section);
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtShop, txt_address, txtDistance;
        private ImageView storeImg, callDailgImg;

        private ShopSectionRecyclerAdapter.OnItemClickListener onItemClickListener;
        TextView name;
        public ChildViewHolder(View view) {
            super(view);
            txtShop = (TextView) view.findViewById(R.id.txtStore);
            txt_address = (TextView) view.findViewById(R.id.txt_address);
            storeImg = (ImageView) view.findViewById(R.id.store_image);
            callDailgImg = (ImageView) view.findViewById(R.id.imgCall);
            txtDistance = (TextView) view.findViewById(R.id.txtDistance);

            view.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(ShopSectionRecyclerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int positioon);
    }

}