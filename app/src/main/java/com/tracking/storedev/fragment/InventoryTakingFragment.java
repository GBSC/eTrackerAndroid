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
import com.android.volley.VolleyError;
import com.tracking.storedev.R;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.adapter.InventoryAdapter;
import com.tracking.storedev.bean.Stocks;
import com.tracking.storedev.db.InventoryItem;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.db.StoreVisit;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.DBToObject;
import com.tracking.storedev.util.Logger;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;
import com.tracking.storedev.view.InventoryTakingDialog;
import com.tracking.storedev.web.HttpCaller;
import com.tracking.storedev.web.WebURL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irfan Ali on 2/28/2018.
 */

public class InventoryTakingFragment extends Fragment {

    private Util util = Util.getInstance();
    private HttpCaller httpCaller = HttpCaller.getInstance();
    private DBToObject dbToObject = DBToObject.getInstance();
    private Logger logger = Logger.getInstance();

    public StoreVisit storeVisit = StoreDetailActivity.storeVisit;
    private List<InventoryItem> inventoryList = new ArrayList<>();

    private RecyclerView recyclerView;
    private InventoryAdapter inventoryAdapter;
    private Store store = StoreDetailActivity.storeInfo;
    private int indexDuplicateItem = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.store_taking_fragment, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        TextView textViewTitle = view.findViewById(R.id.txt_title);
        textViewTitle.setText("Current Inventory");

        final long StoreID = store.StoreID;

        recyclerView = (RecyclerView) view.findViewById(R.id.journey_recycler_view);
        inventoryAdapter = new InventoryAdapter(inventoryList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(inventoryAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InventoryTakingDialog orderDialog = new InventoryTakingDialog(getActivity(),StoreID, new InventoryTakingDialog.InventoryCallback() {
                    @Override
                    public void InventoryGetter(ArrayList<Stocks> inventoryItemArrayList) {

                        boolean isInternetConnection = util.isNetworkConnected(getActivity());
                        List<InventoryItem> inventoryItems = new ArrayList<>();
                        for(Stocks stocks : inventoryItemArrayList){
                            InventoryItem orderItem = new InventoryItem();
                            orderItem.Product = stocks.Product;
                            orderItem.IsSync = isInternetConnection;
                            orderItem.Qty = stocks.Qty;
                            orderItem.StoreVisitID = storeVisit.StoreVisitID;
                            inventoryItems.add(orderItem);
                        }

                        JSONObject jsonObject = dbToObject.InventoryTakingToJSONObject(inventoryItems);
                        if(isInternetConnection)
                            postInventoryTaken(jsonObject, inventoryItemArrayList);
                        else{
                            logger.setLog(null, inventoryItemArrayList.size() + " Inventories added successfully Offline", "Success");
                            refreshOrderList(inventoryItemArrayList, isInternetConnection);
                        }
                    }
                });
                orderDialog.show();
            }
        });
        return view;
    }


    public void postInventoryTaken(JSONObject jsonObject, final ArrayList<Stocks> inventoryItemArrayList){
        String url = WebURL.addMultipleStock;
        httpCaller.jsonObjectPOSTRequest(true, getActivity(), url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null){
                    logger.setLog(null, inventoryItemArrayList.size() + " Inventories successfully synced to web", "Success");
                    refreshOrderList(inventoryItemArrayList, true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logger.setLog(null, inventoryItemArrayList.size() + " Inventories failed to web", "Failed");
                Toast.makeText(getActivity(), R.string.inventory_added_failed, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    public void refreshOrderList(ArrayList<Stocks> orderStocksItem, boolean isInternetConnection){
        try{
            for(Stocks stock : orderStocksItem){
                long storeVisitID = storeVisit.StoreVisitID;
                InventoryItem orderItem = getDuplicateOrder(stock.Product, storeVisitID);
                if(orderItem != null){
                    orderItem.Qty = stock.Qty+orderItem.Qty;
                    inventoryList.set(indexDuplicateItem, orderItem);
                    inventoryAdapter.notifyDataSetChanged();
                }else{
                    orderItem = new InventoryItem();
                    orderItem.InventoryID = util.getUniqueID();
                    orderItem.StoreVisitID = storeVisit.StoreVisitID;
                    orderItem.StoreID = storeVisitID;
                    orderItem.Product = stock.Product;
                    orderItem.Qty = stock.Qty;
                    orderItem.IsSync = isInternetConnection;
                    orderItem.save();
                    inventoryAdapter.refrehAdapter(orderItem);
                }
            }


            Toast.makeText(getActivity(), R.string.inventory_add_success, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public InventoryItem getDuplicateOrder(String product, long storeVisitID){
        for(int a=0;a<inventoryList.size() ; a++){
            InventoryItem orderItem = inventoryList.get(a);
            if(orderItem.Product.equals(""+product) && orderItem.StoreVisitID == storeVisitID ){
                indexDuplicateItem = a;
                return orderItem;
            }
        }
        return null;
    }



}
