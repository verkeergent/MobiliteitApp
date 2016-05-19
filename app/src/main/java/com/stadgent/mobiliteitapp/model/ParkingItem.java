package com.stadgent.mobiliteitapp.model;

import android.util.Log;

import com.stadgent.mobiliteitapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by floriangoeteyn on 14-Mar-16.
 */
public class ParkingItem extends Item {

    Long id;
    String name;
    String description;
    double latitude;
    double longitude;
    String address;
    ParkingStatus parkingStatus;


    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return new Location(longitude, latitude);
    }

    public String getAddress() {
        return address;
    }

    public Long getAvailableCapacity() {
        return parkingStatus.availableCapacity;
    }

    public Long getTotalCapacity() {
        return parkingStatus.totalCapacity;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public int getIcon(){
        float availablePercentage = (getAvailableCapacity()*100.0f)/getTotalCapacity();
        if(availablePercentage<20)
            return R.drawable.parking_icon_red;
        else
            return R.drawable.parking_icon_green;
    }

    @Override
    public String getTitle() {
        return "Parking - "+name;
    }

    @Override
    public String getDescription() {
        return description+"\n"+
                parkingStatus.availableCapacity+" plaatsen van "+parkingStatus.totalCapacity+" beschikbaar";
    }

    @Override
    public String getDetails() {
        return address;
    }

    @Override
    public Date getDate() {
        Date date;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        try {
            date = df.parse(parkingStatus.lastModifiedDate);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public ItemType.ParkingType getType() {
        return ItemType.ParkingType.PARKING_ALERT;
    }

    @Override
    public int getColor() {
        return R.color.parking;
    }

    private class ParkingStatus{
        Long availableCapacity;
        Long totalCapacity;
        String lastModifiedDate;
    }

}
