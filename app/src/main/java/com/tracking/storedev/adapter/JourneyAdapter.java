package com.tracking.storedev.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tracking.storedev.R;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.web.WebURL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZASS on 3/20/2018.
 */

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> implements Filterable {
    private List<Store> mArrayList;
    private List<Store> mFilteredList;
    private Context context;
    private JourneyAdapter.OnItemClickListener onItemClickListener;

    public JourneyAdapter(List<Store> arrayList, Context context) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.context = context;
    }

    @Override
    public JourneyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journey_row, viewGroup, false);
        return new ViewHolder(view, onItemClickListener);
    }

    public void refreshStoreVisist(List<Store> stores){
        mArrayList.clear();
        if(stores.size() == 0){
            notifyDataSetChanged();
        }
        for(Store store : stores){
            add(store);
        }
    }

    @Override
    public void onBindViewHolder(JourneyAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.txtShop.setText(mFilteredList.get(i).ShopName+" - "+mFilteredList.get(i).ShopKeeper);
        viewHolder.txt_address.setText(mFilteredList.get(i).City);
        viewHolder.txtDistance.setText(mFilteredList.get(i).Distance);

        try{
            Store storeInfo = mFilteredList.get(i);
            if(!storeInfo.IsSync){
                File file = new File(storeInfo.ImagePath);
                if(file.exists()){
                    Picasso.get().load(file).placeholder(R.mipmap.ic_loading).into(viewHolder.storeImg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        try{

            if(!mFilteredList.get(i).ImgUrl.equals(null)){
                String url = WebURL.host+""+mFilteredList.get(i).ImgUrl;
                Picasso.get().load(url).placeholder(R.mipmap.ic_logo
                ).into(viewHolder.storeImg);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        viewHolder.callDailgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCallForward = new Intent(Intent.ACTION_DIAL); // ACTION_CALL
                Uri uri2 = Uri.fromParts("tel", mFilteredList.get(i).ContactNumber, "#");
                intentCallForward.setData(uri2);
                context.startActivity(intentCallForward);
            }
        });
    }

    public void add(Store store){
        mFilteredList.add(store);
        notifyDataSetChanged();
    }

    public void update(Store store){
        mFilteredList.remove(store);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<Store> filteredList = new ArrayList<>();

                    for (Store Store : mArrayList) {

                        if (Store.ShopName.toLowerCase().contains(charString) || Store.ShopKeeper.toLowerCase().contains(charString)) {

                            filteredList.add(Store);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Store>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtShop, txt_address, txtDistance;
        private ImageView storeImg, callDailgImg;

        private JourneyAdapter.OnItemClickListener onItemClickListener;

        public ViewHolder(View view, JourneyAdapter.OnItemClickListener onItemClickListener) {
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

    public void setOnItemClickListener(JourneyAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int positioon);
    }

}