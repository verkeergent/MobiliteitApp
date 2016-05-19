package com.stadgent.mobiliteitapp.repo;

import com.stadgent.mobiliteitapp.model.Item;

import java.util.List;

/**
 * Created by floriangoeteyn on 02-Mar-16.
 */
public interface ItemsLoadedListener {
    /**
     * called when a group of items is loaded
     */
    void onItemsLoaded();

    /**
     * called when all items are loaded
     */
    void onAllItemsLoaded();

    /**
     * called when the list of items has been refreshed
     */
    void onAllItemsRefreshed();

    /**
     * called when the items are being loaded
     */
    void onItemsLoading();

    /**
     * called when the items have been sorted
     */
    void onItemsSorted();

    /**
     * called when an item was removed
     * @param item the removed item
     */
    void onItemRemoved(Item item);

    /**
     * called when an item was added
     * @param item the added item
     */
    void onItemAdded(Item item);

    /**
     * called when a group of items failed to load
     */
    void onLoadFailed();
}
