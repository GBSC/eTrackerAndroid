package com.tracking.storedev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.tracking.storedev.adapter.ShopAccountRegisterationAdapter;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.Util;

import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    private DBHandler dbHandler = DBHandler.getInstance();
    private Util utilInstance = Util.getInstance();

    private RecyclerView recyclerView;
    private ShopAccountRegisterationAdapter journeyAdapter;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final List<Store> storeList = dbHandler.getAllStoreList();

        recyclerView = (RecyclerView)findViewById(R.id.journey_recycler_view);
        imgBack = (ImageView) findViewById(R.id.btnBack);
        if(journeyAdapter == null){
            journeyAdapter = new ShopAccountRegisterationAdapter(storeList,CreateAccountActivity.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(CreateAccountActivity.this));
            recyclerView.setAdapter(journeyAdapter);
        }else{
            journeyAdapter.refreshStoreVisist(storeList);
        }

        journeyAdapter.setOnItemClickListener(new ShopAccountRegisterationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int positioon) {
                Store store = storeList.get(positioon);
                Intent intent = new Intent(CreateAccountActivity.this, AssignUserActivity.class);
                intent.putExtra("ContactNumber", store.ContactNumber);
                intent.putExtra("Address", store.Address);
                intent.putExtra("CNIC", store.CNIC);
                intent.putExtra("storeID", (int)store.StoreID);
                intent.putExtra("ShopKeeperName", store.ShopKeeper);
                startActivityForResult(intent, 1000);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void refreshStoresUpdate(){
        final List<Store> storeList = dbHandler.getAllStoreList();
        journeyAdapter.refreshStoreVisist(storeList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            refreshStoresUpdate();

        }
    }
}
