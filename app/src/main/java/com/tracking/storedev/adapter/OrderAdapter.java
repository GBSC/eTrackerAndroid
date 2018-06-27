package com.tracking.storedev.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.db.OrderItem;

import java.util.List;

/**
 * Created by ZASS on 3/20/2018.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderItem> mArrayList;
    private Context context;

    public OrderAdapter(List<OrderItem> arrayList, Context context) {
        mArrayList = arrayList;
        this.context = context;
    }

    public void refrehAdapter(OrderItem orderItem){
        mArrayList.add(orderItem);
        notifyDataSetChanged();
    }


    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txtOrder.setText(""+mArrayList.get(i).Product);
        viewHolder.txtQty.setText(""+mArrayList.get(i).Qty);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtOrder, txtQty;
        private OrderAdapter.OnItemClickListener onItemClickListener;

        public ViewHolder(View view) {
            super(view);

            txtOrder = (TextView) view.findViewById(R.id.txtOrder);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int positioon);
    }

}