package com.stadgent.mobiliteitapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.fragments.ItemListFragment;
import com.stadgent.mobiliteitapp.fragments.MapFragment;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.service.PushService;
import com.stadgent.mobiliteitapp.session.UserSessionManager;
import com.stadgent.mobiliteitapp.utitlity.NetworkManager;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener{

    @Nullable
    @Bind(R.id.itemView)
    protected FrameLayout singlePaneLayout;


    private ItemListFragment itemListFragment;
    private MapFragment mapFragment;

    private int index =-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserSessionManager session = new UserSessionManager(getApplicationContext());
        if(!session.checkLogin()) {
            finish();
            return;
        }


        /*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);*/

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        itemListFragment = (ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.item_list_fragment);


        if (singlePaneLayout != null&&(itemListFragment==null||!(itemListFragment.isVisible()))) {

            itemListFragment = ItemListFragment.getNewInstance();

            itemListFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.itemView, itemListFragment).commit();
        }

        if(NetworkManager.isNetworkAvailable(this))
            ItemRepository.loadItems();
        else{
            Toast.makeText(this, "Verbind met het internet om items op te halen", Toast.LENGTH_SHORT).show();
        }

        createActionbar();

    }


    /**
     * creates the cutsom actionbar
     */
    private void createActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.LEFT);
        actionBar.setCustomView(mActionBarView, lp);
        Toolbar parent = (Toolbar) actionBar.getCustomView().getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        createActionbarOptions(mActionBarView);
    }


    /**
     * handles the options of the custom actionbar
     * @param mActionBarView the custom actionbar
     */
    private void createActionbarOptions(View mActionBarView) {

        ImageButton info = ButterKnife.findById(mActionBarView, R.id.action_info);
        ImageButton filter = ButterKnife.findById(mActionBarView, R.id.action_filter);
        ImageButton showAll = ButterKnife.findById(mActionBarView, R.id.action_showAll);
        ImageButton refresh = ButterKnife.findById(mActionBarView, R.id.action_refresh);
        ImageButton undo = ButterKnife.findById(mActionBarView, R.id.action_undo);
        TextView username = ButterKnife.findById(mActionBarView, R.id.tvUsername);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.customDialog))
                        .setMessage(R.string.info_dialog_text)
                        .setTitle(R.string.info_dialog_title)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteraction(true, -1);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemRepository.loadItems();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemListFragment!=null&&itemListFragment.isVisible())
                    itemListFragment.toggleSlider();
                else{
                    itemListFragment=new ItemListFragment();

                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.itemView, itemListFragment)
                            .commit();

                    itemListFragment.scrollToPosition(index);
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemRepository.undoRemoveItem();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.customDialog))
                        .setTitle("Logout")
                        .setMessage("Ben je zeker dat je wil uitloggen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ItemRepository.userLoggedOut();
                                finish();
                            }

                        })
                        .setNegativeButton("Nee", null)
                        .show();

            }
        });


        HashMap<String, String> user = UserSessionManager.getUserDetails();
        String _username = user.get(UserSessionManager.KEY_NAME);
        username.setText(_username);
        username.setSelected(true);

        mActionBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListFragment.scrollToPosition(-1);
            }
        });

    }


    @Override
    public boolean isSinglePaneActive() {
        return singlePaneLayout != null;
    }

    @Override
    public void onFragmentInteraction(boolean listFragmentInteraction, int index) {
        if(listFragmentInteraction) {

            if (mapFragment == null || !(mapFragment.isVisible())) {

                mapFragment = MapFragment.getNewInstance(index);

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.itemView, mapFragment).commit();
            }

            if (mapFragment != null&&mapFragment.isVisible()) {
                mapFragment.updateMap(index);
            }

        }else{

                if(itemListFragment!=null)
                    itemListFragment.scrollToPosition(index);
        }

        this.index=index;
    }
}
