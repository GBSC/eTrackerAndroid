package com.tracking.storedev.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.db.InventoryItem;

import java.util.List;

/**
 * Created by ZASS on 3/20/2018.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private List<InventoryItem> mArrayList;
    private Context context;

    public InventoryAdapter(List<InventoryItem> arrayList, Context context) {
        mArrayList = arrayList;
        this.context = context;
    }

    public void refrehAdapter(InventoryItem inventoryItemArrayList){
        mArrayList.add(inventoryItemArrayList);
        notifyDataSetChanged();
    }

    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InventoryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txtShop.setText(""+mArrayList.get(i).Product);
        viewHolder.txt_address.setText(""+mArrayList.get(i).Qty);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtShop, txt_address;
        private InventoryAdapter.OnItemClickListener onItemClickListener;

        public ViewHolder(View view) {
            super(view);

            txtShop = (TextView) view.findViewById(R.id.txtOrder);
            txt_address = (TextView) view.findViewById(R.id.txtQty);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int positioon);
    }

}