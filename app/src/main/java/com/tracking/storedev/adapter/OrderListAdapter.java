package com.tracking.storedev.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.bean.Stocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZASS on 3/20/2018.
 */

public class OrderListAdapter extends ArrayAdapter {
    Context context;
    List<Stocks> stocksList;

    public OrderListAdapter(Context context, List<Stocks> resource) {
        super(context, R.layout.item_row, resource);
        this.context = context;
        this.stocksList = resource;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_row, parent, false);

        TextView textView = (TextView) convertView.findViewById(R.id.txtItem);
        Button btnMinus = (Button) convertView.findViewById(R.id.btnMinus);
        Button btnPlus = (Button) convertView.findViewById(R.id.btnPlus);
        final EditText editTextQty = (EditText) convertView.findViewById(R.id.editTextQty);

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = stocksList.get(position).Qty;
                if(qty == 0){
                    qty = 0;
                }else{
                    --qty;
                }

                editTextQty.setText(""+qty);

                Stocks stocks =  stocksList.get(position);
                stocks.Qty = qty;
                stocksList.set(position, stocks);
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = stocksList.get(position).Qty;
                ++qty;
                editTextQty.setText(""+qty);

                Stocks stocks =  stocksList.get(position);
                stocks.Qty = qty;
                stocksList.set(position, stocks);
            }
        });

        editTextQty.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    int qty = Integer.parseInt(s.toString());
                    Stocks stocks =  stocksList.get(position);
                    stocks.Qty = qty;
                    stocksList.set(position, stocks);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


        textView.setText(stocksList.get(position).Product);
        return convertView;
    }

    public ArrayList<Stocks> getStocksList(){
        ArrayList<Stocks> stringList = new ArrayList<>();
        for(int i = 0; i < stocksList.size() ; i++){
           if(stocksList.get(i).Qty != 0){
               Stocks stock = stocksList.get(i);
               stringList.add(stock);
           }
        }
        return stringList;
    }
}