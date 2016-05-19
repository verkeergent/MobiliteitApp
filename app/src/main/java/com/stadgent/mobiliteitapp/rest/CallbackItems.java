package com.stadgent.mobiliteitapp.rest;

import android.util.Log;

import com.stadgent.mobiliteitapp.model.CoyoteItem;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ParkingItem;
import com.stadgent.mobiliteitapp.model.TwitterItem;
import com.stadgent.mobiliteitapp.model.VGSItem;
import com.stadgent.mobiliteitapp.model.WazeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 17-Mar-16.
 */

//klasse die gebruikt wordt om lijsten van items te stockeren (moet gelijk zijn aan de naam van de json array)
public class CallbackItems {
    public List<WazeItem.WazeItemAlert> alerts;
    public List<WazeItem.WazeItemJam> jams;
    public List<TwitterItem> twitterItems;
    public List<ParkingItem> parkingItems;
    public List<CoyoteItem.CoyoteAlert> coyotealerts;
    public List<CoyoteItem.CoyoteTravelAlert> coyotetraveltimes;
    public List<VGSItem> vgsItems;

    //geeft een lijst met de items van de huidige callback (bvb client stuurt twitteritem request:
    //twitteritems wordt ingevuld, de andere lijsten blijven null
    public List<Item> getAllItems(){
        List<Item> items = new ArrayList<>();
        if (parkingItems != null)
            items.addAll(parkingItems);
        if (twitterItems != null)
            items.addAll(twitterItems);
        if (alerts != null)
            items.addAll(alerts);
        if (jams != null)
            items.addAll(jams);
        if(coyotealerts!=null)
            items.addAll(coyotealerts);
        if(coyotetraveltimes !=null)
            items.addAll(coyotetraveltimes);
        if(vgsItems !=null)
            items.addAll(vgsItems);


        return items;
    }
}
