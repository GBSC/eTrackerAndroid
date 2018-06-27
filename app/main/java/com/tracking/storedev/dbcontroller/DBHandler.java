package com.tracking.storedev.dbcontroller;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.tracking.storedev.bean.SectionHeader;
import com.tracking.storedev.bean.ShopMark;
import com.tracking.storedev.db.CompetitiveItem;
import com.tracking.storedev.db.InventoryItem;
import com.tracking.storedev.db.Names;
import com.tracking.storedev.db.OrderItem;
import com.tracking.storedev.db.Store;
import com.tracking.storedev.db.StoreVisit;
import com.tracking.storedev.db.SubSection;
import com.tracking.storedev.realtimedb.RealTimeDBHandling;
import com.tracking.storedev.util.PrefManager;
import com.tracking.storedev.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZASS on 3/20/2018.
 */

public class DBHandler  {

    public static DBHandler dbHandler = new DBHandler();
    public Util utilInstance = Util.getInstance();

    public static DBHandler getInstance(){
        return dbHandler;
    }

    public List<Store> getStoreList(String status, String day){
        List<Store> storeList = null;
        if(status.equals("Target")) {
            storeList = new Select().from(Store.class).where("VisitDays LIKE ?", new String[]{'%' + day + '%'}).and("Status = 'Target'").execute();
            return storeList;
        }else if(status.equals("Achieved")){
            storeList = new Select().from(Store.class).where("VisitDays LIKE ?", new String[]{'%' + day + '%'}).and("Status = 'Achieved'").execute();
        }else{
            storeList = new Select().from(Store.class).execute();
        }
        return storeList;
    }

    public List<Store> getAllStoreList(){
        List<Store>  storeLisst = new Select().from(Store.class).where("HasShopKeeperAccount =?", false).and("IsSync = ?", true).execute();
        return storeLisst;
    }

    public List<String> getContactList(long storeID){
        List<String> namesList1 = new ArrayList<>();
        List<Names> namesList = new Select().from(Names.class).where("storeID = ?", storeID).execute();
        for(Names name : namesList){
            namesList1.add(name.name_);
        }
        return namesList1;
    }

    public String getContactNumber(String name){
        Names names = new Select().from(Names.class).where("name_ =  '"+name+"'" ).executeSingle();
        if(names != null){
            return names.number_;
        }else
            return "";
    }

    public List<Store> getUnSyncedStores(){
        List<Store> storeList = new Select().from(Store.class).where("IsSync = ?", false).execute();
        return storeList;
    }

    public List<StoreVisit> getUnSyncedStoreVisits(){
        List<StoreVisit> storeList = new Select().from(StoreVisit.class).where("IsSync = ?", false).execute();
        return storeList;

    }

    public List<OrderItem> getUnSyncedOrderByStoreVisits(int storeVisitID){
        List<OrderItem> orderList = new Select().from(OrderItem.class).where("IsSync = ?", false).and("StoreVisitID = ?", storeVisitID).execute();
        return orderList;
    }

    public List<InventoryItem> getUnSyncedInventoryByStoreVisits(int storeVisitID){
        List<InventoryItem> orderList = new Select().from(InventoryItem.class).where("IsSync = ?", false).and("StoreVisitID = ?", storeVisitID).execute();
        return orderList;
    }

    public List<CompetitiveItem> getUnSyncedCompitativeStockByStoreVisits(int storeVisitID){
        List<CompetitiveItem> orderList = new Select().from(CompetitiveItem.class).where("IsSync = ?", false).and("StoreVisitID = ?", storeVisitID).execute();
        return orderList;
    }

    public Store getStoreDetail(long storeID){
        return new Select().from(Store.class).where("StoreID =?", storeID).executeSingle();
    }

    public ArrayList<ShopMark> getShopsLocation(){
        ArrayList<ShopMark> locationList = new ArrayList<>();
        String day = utilInstance.getDayOfWeek()+"";
        List<Store> storeList = new Select().from(Store.class).where("VisitDays LIKE ?", new String[]{'%' + day + '%'}).and("Status = 'Target'").execute();

        for(Store store : storeList){
            try{
                ShopMark shopMark= new ShopMark();

                double lati = Double.parseDouble(store.Latitude);
                double longi = Double.parseDouble(store.Longitude);
                LatLng points =  new LatLng(lati , longi);

                shopMark.latLng = points;
                shopMark.store = store;
                locationList.add(shopMark);
            }catch (Exception e){
             e.printStackTrace();
            }

        }
        return locationList;
    }

    public List<SubSection> getSubsection(){
        List<SubSection> townList =  new Select().from(SubSection.class).execute();
        return townList;
    }

    public SubSection getSubSection(int subsectionId){
        return  new Select().from(SubSection.class).where("subsectionId =? ", subsectionId).executeSingle();
    }


    public void updateStatus(){
        String todayDate = utilInstance.getCurrentDate();
        String today = utilInstance.getDayOfWeek()+"";
        List<Store> wachievedList = new Select().from(Store.class).execute();
        List<Store> achievedList = new Select().from(Store.class).where("Status = 'Achieved'").or("CheckOutDate != ?", today).or("CheckOutDate = null").or("Status = null").execute();
        if(achievedList.size() > 0){
            for(Store store : achievedList){
                if(store.Status.equals("Achieved")){
                    String days[] = store.VisitDays.split(",");
                    for(String day : days){
                        if(today.equals(day)){
                            store.Status = "Target";
                            store.save();
                        }
                    }
                }
            }
        }

        achievedList = new Select().from(Store.class).execute();
        if(achievedList.size() > 0){
            for(Store store : achievedList){
                if(store.Status == null || store.CheckOutDate == null){
                    String days[] = store.VisitDays.split(",");
                    for(String day : days){
                        if(today.equals(day)){
                            store.Status = "Target";
                            store.save();
                        }
                    }
                }
            }
        }
    }

    public long getUniqueID(String modelString){
        long id = 0;
        if(modelString.equals("Store")) {
            Store store = new Select().from(Store.class).orderBy("id DESC").executeSingle();
            if(store == null)
                return 1;
            else
             id = store.StoreID+1;
        }
        else if(modelString.equals("StoreVisit")) {
            StoreVisit storeVisit = new Select().from(StoreVisit.class).orderBy("id DESC").executeSingle();
            if(storeVisit == null)
                id =1;
            else
                id = storeVisit.StoreVisitID+1;
        }
        return id;
    }

    public List<SectionHeader> getOrderByStoreID(String days, String status){

        List<SectionHeader> listDataHeader = new ArrayList<SectionHeader>();

        String strDays[] = days.split(",");
        for(int i = 0; i < strDays.length; i++){
            int day = Integer.parseInt(strDays[i]);
            String daysName = utilInstance.getNumberToDay(day);

            List<Store> storeList = new Select().from(Store.class).where("VisitDays LIKE ?", new String[]{'%' + ""+day + '%'}).and("Status = '"+status+"'").execute();
            listDataHeader.add(new SectionHeader(storeList, daysName));
        }
        return listDataHeader;
    }

}
