package com.stadgent.mobiliteitapp;

import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ItemType;

import java.util.Date;

/**
 * Created by floriangoeteyn on 17-May-16.
 */
public class TestItem extends Item {

    private String id;
    private String title;
    private String description;
    private String details;
    private ItemType type;


    public TestItem(String id, String title, String description, String details, ItemType type){

    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public ItemType getType() {
        return type;
    }

    @Override
    public int getColor() {
        return R.color.notype;
    }
}
