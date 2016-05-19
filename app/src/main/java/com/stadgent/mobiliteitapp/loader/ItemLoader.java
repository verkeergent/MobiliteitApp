package com.stadgent.mobiliteitapp.loader;

import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.rest.ItemCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 15-Mar-16.
 */

//klasse verantwoordelijk voor communicatie tussen REST en Repository
public class ItemLoader{

    private ItemCallback callback;
    private boolean refreshItems = false;
    private List<Item> allItems;

    /**
     * creates a new REST callback
     */
    public ItemLoader(){
        new ItemLoader(false);
    }

    /**
     * delete an item
     * @param id id of the item to be deleted
     */
    public void deleteItem(String id){
        if(callback==null)
            callback=new ItemCallback(this);
        callback.deleteItem(id);
    }

    /**
     * undo deleting the item
     * @param id id of the removed item
     */
    public void undoDeleteItem(String id){
        if(callback==null)
            callback=new ItemCallback(this);

            callback.undoDeleteItem(id);
    }

    /**
     * creates a new REST callback
     * @param refresh true if items need to be refreshed, false if items need to be loaded
     */
    public ItemLoader(boolean refresh){
        allItems=new ArrayList<>();
        this.refreshItems=refresh;
        this.callback = new ItemCallback(this);
        callback.getItems();
    }

    //update repository telkens een response geladen is

    /**
     * called when a group of items were loaded, sends a message to the repository
     * @param items the items that were loaded
     */
    public void onItemsLoaded(List<Item> items) {
        allItems.addAll(items);
        ItemRepository.onItemsLoaded(items);
    }

    /**
     * called when all items were loaded, sends a message to the repository
     */
    public void onAllItemsLoaded(){
        if(refreshItems)
            ItemRepository.onAllItemsRefreshed(allItems);
        else
            ItemRepository.onAllItemsLoaded(allItems);
        refreshItems=false;
    }

    /**
     * called when a group of items failed to load, sends a message to the repository
     */
    public void onLoadFailed(){
        ItemRepository.onLoadFailed();
    }

}
