package com.stadgent.mobiliteitapp.model;

import com.stadgent.mobiliteitapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by floriangoeteyn on 15-Feb-16.
 */
public class VGSItem extends Item {


    String id;
    String name;
    String description;
    String state;
    String type;
    String date;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return "VGS - update";
    }

    @Override
    public String getDescription() {
        return "("+id+")"+" Scenario update: "+name;
    }

    @Override
    public String getDetails() {
        return description;
    }

    @Override
    public Date getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            return df.parse(this.date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override
    public ItemType getType() {
        return null;
    }

    @Override
    public int getColor() {
        return R.color.vgs;
    }
}
