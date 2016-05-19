package com.stadgent.mobiliteitapp.activities;

import android.support.v4.app.Fragment;

/**
 * Created by floriangoeteyn on 01-Mar-16.
 */
public interface OnFragmentInteractionListener {
    //methode die wordt uitgevoerd bij interactie met een fragment, true: interactie met listfragment, false: interactie met mapfragment
    //positie: de index van het item in de recyclerview waarop geklikt is

    /**
     * checks if landscape or portrait layout is active
     * @return true if portrait layout, false if landscape layout
     */
    boolean isSinglePaneActive();

    /**
     * handles interaction between fragments
     * @param listFragmentInteraction true if the interacted fragment was an ItemListFragment
     * @param position position of the interacted item
     */
    void onFragmentInteraction(boolean listFragmentInteraction, int position);
}
