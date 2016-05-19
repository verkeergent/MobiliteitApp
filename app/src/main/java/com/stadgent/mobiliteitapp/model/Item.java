package com.stadgent.mobiliteitapp.model;


import com.stadgent.mobiliteitapp.R;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by floriangoeteyn on 01-Feb-16.
 */
public abstract class Item implements Serializable{

    public abstract String getId();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getDetails();

    public abstract Date getDate();

    public abstract ItemType getType();

    public abstract int getColor();

    public ItemType getSubtype(){
        return null;
    }

    public int getIcon(){
        return R.drawable.empty_icon;
    }

    public Location getLocation(){
        return null;
    }

    public List<Location> getLine(){
        return null;
    }

    public boolean isRemoved(){
        return false;
    }

}
