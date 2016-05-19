package com.stadgent.mobiliteitapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stadgent.mobiliteitapp.activities.MainActivity;
import com.stadgent.mobiliteitapp.activities.OnFragmentInteractionListener;
import com.stadgent.mobiliteitapp.adapter.ItemAdapter;
import com.stadgent.mobiliteitapp.model.ItemType;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.repo.ItemsLoadedListener;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.session.FilterSessionManager;
import com.stadgent.mobiliteitapp.session.UserSessionManager;
import com.stadgent.mobiliteitapp.utitlity.NetworkManager;
import com.wunderlist.slidinglayer.SlidingLayer;
import com.wunderlist.slidinglayer.transformer.AlphaTransformer;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Fragment containing a list of items and a navigation slider to filter items
 */
public class ItemListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ItemsLoadedListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView rv;
    private ItemAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SlidingLayer slidingLayer;
    private ProgressDialog pd;
    private int selecteditempos = -1;

    private View rootView;


    /**
     * is called from the activity containing the fragment and instantiates the fragment
     * @return the newly created ItemListFragment
     */
    public static ItemListFragment getNewInstance() {
        ItemListFragment newFragment = new ItemListFragment();
        Bundle args = new Bundle();

        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.itemRecyclerView);
        slidingLayer = (SlidingLayer) rootView.findViewById(R.id.slider);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        if (!(ItemRepository.getItems().size() > 0) && NetworkManager.isNetworkAvailable(getContext())) {
            pd = new ProgressDialog(getContext());
            pd.setTitle("loading");
            pd.setMessage("Even geduld");
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(true);
                    pd.cancel();
                }
            });
            pd.show();
        }

        return rootView;
    }

    /**
     * register as a listener, create the itemview and scroll to the selected position
     */
    @Override
    public void onStart() {
        super.onStart();
        ItemRepository.registerListener(this);
        createItemView();
        scrollToPosition(selecteditempos);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createSlider();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ItemRepository.removeListener(this);
        if ((pd != null) && pd.isShowing())
            pd.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((pd != null) && pd.isShowing())
            pd.dismiss();
    }


    @Override
    public void onRefresh() {
        if (NetworkManager.isNetworkAvailable(getContext())) {
            ItemRepository.loadItems();
        } else {
            Toast.makeText(getContext(), "Verbind met het internet om items op te halen", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onItemsLoaded() {
        //adapter.setItems(ItemRepository.getItems());
    }

    @Override
    public void onAllItemsLoaded() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if (pd != null)
            pd.dismiss();

        //adapter.onAllItemsLoaded();
        adapter.setItems(ItemRepository.getItems());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAllItemsRefreshed() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if (pd != null)
            pd.dismiss();

        if(getContext()!=null)
            Toast.makeText(getContext(), "Items updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemsLoading() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onItemsSorted() {
        adapter.setItems(ItemRepository.getItems());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFailed() {
        //adapter.setItems(ItemRepository.getItems());
        if (getContext() != null)
            Toast.makeText(getContext(), "Niet alle items konden geladen worden", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemRemoved(Item item) {
        int index = adapter.getItems().indexOf(item);
        adapter.setItems(ItemRepository.getItems());
        if (index > -1)
            adapter.notifyItemRemoved(index);
        else
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemAdded(Item item) {
        adapter.setItems(ItemRepository.getItems());
        int index = adapter.getItems().indexOf(item);
        if (index > -1) {
            adapter.markCard(index);
            adapter.notifyItemInserted(index);
        } else
            adapter.notifyDataSetChanged();
    }

    /**
     * creates the navigation slider
     */
    private void createSlider() {
        try {
            slidingLayer.setShadowDrawable(R.drawable.sidebar_shadow);
            slidingLayer.setShadowSizeRes(R.dimen.shadow_size);
            slidingLayer.setChangeStateOnTap(false);
            slidingLayer.setStickTo(SlidingLayer.STICK_TO_LEFT);
            slidingLayer.setSlidingEnabled(false);
            slidingLayer.closeLayer(true);
            slidingLayer.setLayerTransformer(new AlphaTransformer());
            createSliderOptions();
        } catch (OutOfMemoryError oom) {
            System.gc();
            createSliderOptions();
        }
    }


    /**
     * creates the options in the slider
     */
    private void createSliderOptions() {
        try {

            Multimap<String, Filterdata> itemfilters = HashMultimap.create();

            itemfilters.put("Twitter", new Filterdata("Mentions en replies", Arrays.<ItemType>asList(ItemType.TwitterType.TWITTER_MENTION), R.drawable.twitter_icon_mention));

            itemfilters.put("Twitter", new Filterdata("Tweets", Arrays.<ItemType>asList(ItemType.TwitterType.TWITTER_TWEET), R.drawable.twitter_icon_tweet));

            itemfilters.put("Parkeergarages", new Filterdata("Volle parkings", Arrays.<ItemType>asList(ItemType.ParkingType.PARKING_ALERT), R.drawable.parking_icon_red));

            itemfilters.put("VGS", new Filterdata("Scenario updates", Arrays.<ItemType>asList(ItemType.VGSType.VGS_ALERT), R.drawable.vgs_icon));

            itemfilters.put("Waze", new Filterdata("Ongevallen", Arrays.<ItemType>asList(ItemType.WazeType.ACCIDENT), R.drawable.waze_accident));
            itemfilters.put("Waze", new Filterdata("Files", Arrays.<ItemType>asList(ItemType.WazeType.JAM), R.drawable.waze_traffic));
            itemfilters.put("Waze", new Filterdata("Risico's", Arrays.<ItemType>asList(ItemType.WazeType.WEATHERHAZARD), R.drawable.waze_hazard));
            itemfilters.put("Waze", new Filterdata("Pechstrook", Arrays.<ItemType>asList(ItemType.WazeType.SubType.HAZARD_ON_SHOULDER, ItemType.WazeType.SubType.HAZARD_ON_SHOULDER_CAR_STOPPED, ItemType.WazeType.SubType.HAZARD_ON_SHOULDER_MISSING_SIGN, ItemType.WazeType.SubType.HAZARD_ON_SHOULDER_ANIMALS), R.drawable.waze_onshoulder));
            itemfilters.put("Waze", new Filterdata("Afsluitingen", Arrays.<ItemType>asList(ItemType.WazeType.ROAD_CLOSED), R.drawable.waze_roadclosed));

            itemfilters.put("Coyote", new Filterdata("Ongevallen", Arrays.<ItemType>asList(ItemType.CoyoteType.ACCIDENT), R.drawable.coyote_accident));
            itemfilters.put("Coyote", new Filterdata("Files", Arrays.<ItemType>asList(ItemType.CoyoteType.JAM), R.drawable.coyote_jam));
            itemfilters.put("Coyote", new Filterdata("Risico's", Arrays.<ItemType>asList(ItemType.CoyoteType.HAZARD), R.drawable.coyote_incident));
            itemfilters.put("Coyote", new Filterdata("Wegversmallingen", Arrays.<ItemType>asList(ItemType.CoyoteType.RETRECISSEMENT), R.drawable.coyote_retricement));


            final FilterSessionManager sessionManager = new FilterSessionManager(getContext());


            View customslider = getActivity().getLayoutInflater().inflate(R.layout.custom_slider, null);

            FrameLayout sliderlayout = (FrameLayout) customslider.findViewById(R.id.sliderLayout);

            LinearLayout filteritemslayout = (LinearLayout) customslider.findViewById(R.id.filterItemsLayout);

            for (String source : itemfilters.keySet()) {

                View viewfiltersource = getActivity().getLayoutInflater().inflate(R.layout.view_filtersource, null);

                LinearLayout filtersourcelayout = (LinearLayout) viewfiltersource.findViewById(R.id.filtersourcelayout);

                TextView filteritemtitle = (TextView) filtersourcelayout.findViewById(R.id.filteritemtitle);
                filteritemtitle.setText(source);

                for (Filterdata itemfilter : itemfilters.get(source)) {

                    final String filtername = itemfilter.filtername;
                    int filterIcon = itemfilter.filterIcon;
                    final List<ItemType> types = itemfilter.filtertypes;

                    View viewfilteritem = getActivity().getLayoutInflater().inflate(R.layout.view_filteritem, null);

                    RelativeLayout filteritemlayout = (RelativeLayout) viewfilteritem.findViewById(R.id.layout_filterItem);

                    TextView filteritemtype = (TextView) filteritemlayout.findViewById(R.id.filteritemtype);
                    filteritemtype.setText(filtername);

                    ImageView filterItemImage = (ImageView) filteritemlayout.findViewById(R.id.filterItemImage);
                    filterItemImage.setImageResource(filterIcon);

                    final Switch filterItemSwitch = (Switch) filteritemlayout.findViewById(R.id.filterItemSwitch);
                    filterItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            ItemRepository.filterByType(isChecked, types);
                            sessionManager.putFilterValue(filtername, isChecked);
                        }
                    });

                    filterItemSwitch.setChecked(FilterSessionManager.getFilterValue(filtername));

                    filtersourcelayout.addView(filteritemlayout);

                }

                filteritemslayout.addView(filtersourcelayout);

            }

            slidingLayer.addView(sliderlayout);

            Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
            CheckBox refreshCheckbox = (CheckBox) rootView.findViewById(R.id.refreshCheckbox);
            Switch sorteerSwitch = (Switch) rootView.findViewById(R.id.sorteerSwitch);
            Spinner dateSpinner = (Spinner) rootView.findViewById(R.id.dateSpinner);


            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.timefilter_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dateSpinner.setAdapter(adapter);

            dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    long HOUR_IN_MS = 1000 * 60 * 60;
                    long DAY_IN_MS = HOUR_IN_MS * 24;
                    Date date;
                    switch (position) {
                        case 0:
                            date = null;
                            break;     //altijd
                        case 1:
                            date = new Date(new Date().getTime() - 7 * DAY_IN_MS);
                            break;     //1 week
                        case 2:
                            date = new Date(new Date().getTime() - DAY_IN_MS);
                            break;     //1 dag
                        case 3:
                            date = new Date(new Date().getTime() - HOUR_IN_MS);
                            break;     //1 uur
                        default:
                            date = null;
                            break;
                    }
                    ItemRepository.filterByDate(date);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            sorteerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ItemRepository.sortItems(isChecked);
                }
            });

            refreshCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //refreshProgress.setVisibility(isChecked?View.VISIBLE:View.INVISIBLE);
                    ItemRepository.refreshItemsWithInterval(isChecked, 60);
                }
            });
            refreshCheckbox.setChecked(true);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.customDialog))
                            .setTitle("Logout")
                            .setMessage("Ben je zeker dat je wil uitloggen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ItemRepository.userLoggedOut();
                                    getActivity().finish();
                                }

                            })
                            .setNegativeButton("Nee", null)
                            .show();
                }
            });
        }catch(Exception ignored){
        }
    }


    /**
     * opens/closes the navigation slider
     */
    public void toggleSlider() {

        if (ItemListFragment.this.isVisible()) {
            RecyclerViewDisabler disabler = new RecyclerViewDisabler();

            if (slidingLayer.isClosed()) {
                slidingLayer.openLayer(true);
                disabler.disableRv();
            } else {
                slidingLayer.closeLayer(true);
                disabler.enableRv();
            }
        } else {
            mListener.onFragmentInteraction(false, -1);
        }
    }

    /**
     * create a new recyclerview
     */
    private void createItemView() {

        //opbouw van de adapter voor de recyclerview
        LinearLayoutManager llm = new NpaLinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        adapter = new ItemAdapter(ItemRepository.getItems(), mListener, getContext());
        rv.setAdapter(adapter);

        rv.setItemAnimator(new DefaultItemAnimator());

        // swipe om te verwijderen logica
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                try {
                    int pos = viewHolder.getAdapterPosition();
                    Item item = null;
                    if (pos > -1)
                        item = adapter.getItems().get(pos);
                    if (item != null)
                        ItemRepository.removeItem(item);
                }catch(IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(rv);
    }


    /**
     * scrolls to the specified position and marks the card if position > -1, otherwise the recyclerview will scroll to the top
     * @param i position
     */
    public void scrollToPosition(int i) {

        selecteditempos = i;

        if(adapter!=null){
            if(selecteditempos>-1)
                adapter.markCard(i);
            else
                rv.scrollToPosition(0);
        }

    }


    /**
     * helper class to hold a list of filters
     */
    private class Filterdata {
        String filtername;
        List<ItemType> filtertypes;
        int filterIcon;

        public Filterdata(String filtername, List<ItemType> filtertypes, int filterIcon) {
            this.filtername = filtername;
            this.filtertypes = filtertypes;
            this.filterIcon = filterIcon;
        }
    }


    /**
     * helper class to disable interaction with the recyclerview
     */
    private class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

        public void disableRv() {
            rv.addOnItemTouchListener(this);
        }

        public void enableRv() {
            rv.removeOnItemTouchListener(this);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            slidingLayer.closeLayer(true);
            enableRv();
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     * helper class to solve a bug in recyclerview
     */
    private static class NpaLinearLayoutManager extends LinearLayoutManager {

        public NpaLinearLayoutManager(Context context) {
            super(context);
        }

        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }


    }
}
