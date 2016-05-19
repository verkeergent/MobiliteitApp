package com.stadgent.mobiliteitapp;

import android.test.suitebuilder.annotation.LargeTest;

import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ItemType;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 17-May-16.
 */
@RunWith(JUnit4.class)
@LargeTest
public class ItemListTest {

    private List<Item> items;


    @Before
    public void initItems() {
        items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(new TestItem("" + i, "item title " + i, "item desc " + i, "item details " + i, ItemType.WazeType.NO_TYPE));
        }

    }




}
