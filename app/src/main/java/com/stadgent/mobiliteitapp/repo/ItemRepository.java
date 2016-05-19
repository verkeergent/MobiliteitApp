package com.stadgent.mobiliteitapp.repo;


import android.os.Handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stadgent.mobiliteitapp.loader.ItemLoader;
import com.stadgent.mobiliteitapp.loader.LoginLoader;
import com.stadgent.mobiliteitapp.model.*;
import com.stadgent.mobiliteitapp.session.UserSessionManager;
import com.stadgent.mobiliteitapp.utitlity.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//repository die alle items bijhoudt, updatet, filtert en sorteert
public class ItemRepository {

    private static List<Item> items = new ArrayList<>();
    private static ItemLoader itemLoader;

    private static boolean sortByDate = false;
    private static Multimap<ItemType, Item> filteredItems= HashMultimap.create();
    private static List<ItemType> filteredTypes = new ArrayList<>();
    public static boolean filterEnableAfsluitingen = false;

    private static List<Item> filteredDateItems= new ArrayList<>();
    private static Date dateFilter=null;

    private static List<Item> removedItems = new ArrayList<>();

    private static User currentUser;

    private static List<ItemsLoadedListener> itemsLoadedListeners = new ArrayList<>();
    private static List<OnLoggedInListener> loggedInListeners = new ArrayList<>();


    /**
     * get a list of all items
     * @return Item list
     */
    public static List<Item> getItems() {
        return items;
    }

    /**
     * get the user that was logged in
     * @return current User
     */
    public static User getCurrentUser() {
        return currentUser;
    }


    public static void registerListener(OnLoggedInListener listener) {
        ItemRepository.loggedInListeners.add(listener);
    }

    public static void removeListener(OnLoggedInListener listener) {
        ItemRepository.loggedInListeners.remove(listener);
    }

    /**
     * creates a new loginCallback, which sends a request to the server with the username and password
     * @param username account name String
     * @param password account password String
     */
    public static void login(String username, String password){
        new LoginLoader(username, password);
    }

    public static void onLoginSuccess(User user){
        for(OnLoggedInListener listener: loggedInListeners){
            listener.onLoginSuccess(user);
        }
    }

    public static void onLoginFailed(){
        for(OnLoggedInListener listener: loggedInListeners){
            listener.onLoginFailed();
        }
    }


    /**
     * Register a new listener that will be called every time the data changes
     * @param listener new ItemsLoadedListener
     */
    public static void registerListener(ItemsLoadedListener listener) {
        ItemRepository.itemsLoadedListeners.add(listener);
    }

    /**
     * Remove an existing listener from the list of ItemsLoadedListeners
     * @param listener the ItemsLoadedListener to be removed
     */
    public static void removeListener(ItemsLoadedListener listener) {
        ItemRepository.itemsLoadedListeners.remove(listener);
    }

    /**
     * method to be called when the user wants to log out
     */
    public static void userLoggedOut() {
        UserSessionManager.logoutUser();
        ItemRepository.currentUser = null;
        ItemRepository.items = new ArrayList<>();
    }

    /**
     * Remove a specific item and notify the listeners.
     * The Item will be added to the removedItems stack, in case the user wants to undo removing the item
     * A call to the server will be made to remove the item and the listeners are notified
     *
     * @param item the item to be removed
     */
    public static void removeItem(Item item) {
        removedItems.add(item);
        items.remove(item);
        notifyListenersItemRemoved(item);
        itemLoader.deleteItem(item.getId());
    }


    /**
     * adds the last item that was removed back to the list of items and notifies the listeners
     */
    public static void undoRemoveItem() {
        if (removedItems.size() > 0) {
            Item item = removedItems.get(removedItems.size() - 1);
            removedItems.remove(removedItems.size() - 1);
            itemLoader.undoDeleteItem(item.getId());
            items.add(item);
            sortItems(sortByDate);
            notifyListenersItemAdded(item);
        }
    }

    /**
     * Called when a group of items are loaded and notifies the listeners
     * @param items the items that were loaded
     * @deprecated call onAllItemsLoaded with a full list of items instead, to increase performance
     */
    public static void onItemsLoaded(List<Item> items) {
        //ItemRepository.items.addAll(items);
        notifyListenersItemsLoaded();
    }

    /**
     * Call this when all items are loaded/failed. Creates the list of items and notifies the listeners
     * @param items list of all items that were loaded
     */
    public static void onAllItemsLoaded(List<Item> items) {
        createItemsList(items);
        notifyListenersAllItemsLoaded();
    }


    /**
     * Call this when all items have been refreshed. Creates the list of items and notifies the listeners (similar to onAllItemsLoaded)
     * @param items list of all items that were loaded
     */
    public static void onAllItemsRefreshed(List<Item> items) {
        createItemsList(items);
        notifyListenersAllItemsRefreshed();
    }

    /**
     * creates new list of items, sorts them and filters them by type and date
     * @param items the items that were loaded
     */
    private static void createItemsList(List<Item> items){
        ItemRepository.items = new ArrayList<>(items);
        sortItems(sortByDate);
        filterByType(sortByDate, new ArrayList<>(filteredTypes));
        filterByDate(dateFilter);
    }

    /**
     * To be called when a group of items have failed to load, notifies listeners
     */
    public static void onLoadFailed() {
        notifyListenersLoadFailed();
    }

    /**
     * helper method to notify all listeners a group of items were loaded
     */
    private static void notifyListenersItemsLoaded() {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onItemsLoaded();
        }
    }

    /**
     * helper method to notify all listeners the items were refreshed
     */
    private static void notifyListenersAllItemsRefreshed() {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onAllItemsRefreshed();
        }
    }

    /**
     * helper method to notify all listeners all items were loaded
     */
    private static void notifyListenersAllItemsLoaded() {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onAllItemsLoaded();
        }
    }

    /**
     * helper method to notify all listeners a group of items failed to load
     */
    private static void notifyListenersLoadFailed() {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onLoadFailed();
        }
    }

    /**
     * helper method to notify all listeners an item was removed
     */
    private static void notifyListenersItemRemoved(Item item) {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onItemRemoved(item);
        }
    }

    /**
     * helper method to notify all listeners the items were sorted
     */
    private static void notifyListenersItemsSorted() {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onItemsSorted();
        }
    }

    /**
     * helper method to notify all listeners an item was added (undo)
     */
    private static void notifyListenersItemAdded(Item item) {
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onItemAdded(item);
        }
    }


    private static Timer refreshtimer;
    /**
     * method to be called to toggle the auto refresh function
     * @param refresh if true, items will be refreshed in the specified interval
     * @param seconden interval in seconds
     */
    public static void refreshItemsWithInterval(final boolean refresh, int seconden) {

        if (refreshtimer == null)
            refreshtimer = new Timer();

        if (refresh) {

            final Handler handler = new Handler();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                items = new ArrayList<>();
                                itemLoader = new ItemLoader(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };

            refreshtimer.schedule(doAsynchronousTask, seconden *1000, seconden *1000);
        }
        else {
            refreshtimer.cancel();
            refreshtimer.purge();
            refreshtimer = new Timer();
        }
    }

    /**
     * method called to start loading the items
     */
    public static void loadItems() {
        items = new ArrayList<>();
        for (ItemsLoadedListener listener : itemsLoadedListeners) {
            listener.onItemsLoading();
        }
        itemLoader = new ItemLoader();
    }

    /**
     * method called to sort the items by date or priority
     * @param sortByDate if true, items will be sorted by date, otherwise items are sorted by priority
     */
    public static void sortItems(boolean sortByDate) {
        ItemRepository.sortByDate = sortByDate;
        if (sortByDate) {
            Collections.sort(items, new DateComparator());
        } else {
            Collections.sort(items, new PriorityComparator());
        }
        notifyListenersItemsSorted();
    }

    /**
     * Method to filter the items, to be called when a group of types has to be shown/hidden.
     * Filtered items are stored in a separate map
     * @param showItemsForTypes if true, the types will be filtered out of the list, else they are added back
     * @param types a list of types to be filtered/added back
     */
    public static void filterByType(boolean showItemsForTypes, List<ItemType> types) {

        for(ItemType type: types) {

            if(type == ItemType.WazeType.ROAD_CLOSED){
                filterEnableAfsluitingen=showItemsForTypes;
            }

            if (showItemsForTypes) {
                filteredTypes.remove(type);
                items.addAll(filteredItems.get(type));
                filteredItems.removeAll(type);
            } else {
                if(!filteredTypes.contains(type))
                filteredTypes.add(type);
                for (Item i : items) {
                    if ((i.getType()==type||i.getSubtype()==type) && !filteredItems.containsEntry(type, i)) {
                        filteredItems.put(type, i);
                    }
                }
                items.removeAll(filteredItems.get(type));
            }

            sortItems(sortByDate);

        }
    }

    /**
     * filter items by date
     * @param date items before this date are filtered from the list of all items
     */
    public static void filterByDate(Date date){
        dateFilter=date;

        if(date!=null) {
            List<Item> okItems = new ArrayList<>();
            for (Item filteredItem : filteredDateItems) {
                //als de opgegeven datum ouder is dan de datum van het item in gefilterde items, voeg item toe aan items
                if (date.compareTo(filteredItem.getDate()) < 0) {
                    items.add(filteredItem);
                    okItems.add(filteredItem);
                }
            }

            for (Item item : items) {
                //als de opgegeven datum recenter is dan de datum van het item, wordt het item verwijderd
                if (date.compareTo(item.getDate()) >= 0) {
                    filteredDateItems.add(item);
                }
            }
            filteredDateItems.removeAll(okItems);
            items.removeAll(filteredDateItems);
            items.addAll(okItems);
        }else{
            items.addAll(filteredDateItems);
            filteredDateItems=new ArrayList<>();
        }
        sortItems(sortByDate);

    }


    /**
     * helper class to compare items by date
     */
    private static class DateComparator implements Comparator<Item> {

        @Override
        public int compare(Item item1, Item item2) {
            if (item1.getDate() == null) {
                return 1;
            } else if (item2.getDate() == null) {
                return -1;
            } else
                return -(item1.getDate().compareTo(item2.getDate()));
        }
    }

    /**
     * helper class to compare items by priority
     */
    private static class PriorityComparator implements Comparator<Item> {
        @Override
        public int compare(Item item1, Item item2) {
            if (Priority.getPriority(item1) > Priority.getPriority(item2)) {
                return -1;
            } else if (Priority.getPriority(item1) < Priority.getPriority(item2)) {
                return 1;
            } else {
                return new DateComparator().compare(item1, item2);
            }
        }
    }
}
