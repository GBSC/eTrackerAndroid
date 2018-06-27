package com.tracking.storedev.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
//import com.android.volley.error.VolleyError;
import com.tracking.storedev.R;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.adapter.OrderAdapter;
import com.tracking.storedev.bean.Stocks;
import com.tracking.storedev.db.OrderItem;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.db.StoreVisit;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.DBToObject;
import com.tracking.storedev.util.Logger;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.OrderListDialog;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irfan Ali on 2/28/2018.
 */

public class OrderTakingFragment extends Fragment {

    private Util util = Util.getInstance();
    private HttpCaller httpCaller = HttpCaller.getInstance();
    private DBToObject dbToObject = DBToObject.getInstance();
    private Logger logger = Logger.getInstance();

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    OrderListDialog orderDialog;
    private FloatingActionButton fab;

    private Store store = StoreDetailActivity.storeInfo;
    public StoreVisit storeVisit = StoreDetailActivity.storeVisit;

    private List<OrderItem> storeList = new ArrayList<>();

    public static OrderTakingFragment orderFragment = new OrderTakingFragment();

    public static OrderTakingFragment getInstance() {
        return orderFragment;
    }

    public int indexDuplicateItem = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.store_taking_fragment, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        final long storeID = store.StoreID;
        TextView textViewTitle = view.findViewById(R.id.txt_title);
        textViewTitle.setText("Order");

        recyclerView = (RecyclerView) view.findViewById(R.id.journey_recycler_view);
        orderAdapter = new OrderAdapter(storeList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(orderAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long storeVisitID = storeVisit.StoreVisitID;
                orderDialog = new OrderListDialog(getActivity(), storeID, storeVisitID, new OrderListDialog.OrderCallback() {
                    @Override
                    public void OrderGetter(ArrayList<Stocks> orderItems) {

                        boolean isInternetConnection = util.isNetworkConnected(getActivity());
                        List<OrderItem> orderList = new ArrayList<>();
                        for(Stocks stocks : orderItems){
                            OrderItem orderItem = new OrderItem();
                            orderItem.Product = stocks.Product;
                            orderItem.IsSync = isInternetConnection;
                            orderItem.Qty = stocks.Qty;
                            orderItem.StoreVisitID = storeVisitID;
                            orderItem.StoreID = storeID;
                            orderList.add(orderItem);
                        }

                        JSONObject jsonObject = dbToObject.orderTakingToJSONObject(orderList);
                        if(isInternetConnection) {
                            postOrderTaken(jsonObject, orderItems);
                        }else{
                            logger.setLog(null, orderList.size() + " Order added successfully Offline", "Success");
                            refreshOrderList(orderItems, isInternetConnection);
                        }
                    }
                });
                orderDialog.show();
            }
        });
        return view;
    }


    public void postOrderTaken(JSONObject jsonObject, final ArrayList<Stocks> orderStocksItem) {
        String url = WebURL.addmultipleorders;
        httpCaller.jsonObjectPOSTRequest(true, getActivity(), url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    logger.setLog(null, orderStocksItem.size() + " Order successfully synced to web", "Success");
                    refreshOrderList(orderStocksItem, true);
                    Toast.makeText(getActivity(), R.string.order_added_success, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                logger.setLog(null, orderStocksItem.size() + " Order failed to web", "Failed");
                error.printStackTrace();
            }
        });
    }

    public void refreshOrderList(ArrayList<Stocks> orderStocksItem, boolean isInternetConnection){
        for(Stocks stock : orderStocksItem){
            long storeVisitID = storeVisit.StoreVisitID;
            OrderItem orderItem = getDuplicateOrder(stock.Product, storeVisitID);
            if(orderItem != null){
                orderItem.Qty = stock.Qty+orderItem.Qty;
                orderItem.IsSync = isInternetConnection;
                storeList.set(indexDuplicateItem, orderItem);
                orderAdapter.notifyDataSetChanged();
            }else{
                orderItem = new OrderItem();
                orderItem.OrderID = util.getUniqueID();
                orderItem.StoreVisitID = storeVisit.StoreVisitID;
                orderItem.StoreID = storeVisitID;
                orderItem.IsSync = isInternetConnection;
                orderItem.Product = stock.Product;
                orderItem.Qty = stock.Qty;
                orderItem.save();
                orderAdapter.refrehAdapter(orderItem);
            }
        }
    }

    public OrderItem getDuplicateOrder(String product, long storeVisitID){
        for(int a=0;a<storeList.size() ; a++){
            OrderItem orderItem = storeList.get(a);
            if(orderItem.Product.equals(""+product) && orderItem.StoreVisitID == storeVisitID ){
                indexDuplicateItem = a;
                return orderItem;
            }
        }
        return null;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
