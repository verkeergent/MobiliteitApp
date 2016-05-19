package com.stadgent.mobiliteitapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.picasso.Picasso;
import com.stadgent.mobiliteitapp.model.CoyoteItem;
import com.stadgent.mobiliteitapp.model.WazeItem;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.activities.OnFragmentInteractionListener;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.Location;
import com.stadgent.mobiliteitapp.model.TwitterItem;
import com.stadgent.mobiliteitapp.repo.ItemsLoadedListener;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Fragment containing an OSMdroid mapview to display the items
 */
public class MapFragment extends Fragment implements ItemsLoadedListener {

    public static final String ITEM_SELECTED_POSITION = "ItemPos";
    private int currentPosition = -1;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<RadiusMarkerClusterer> clusters = new ArrayList<>();
    private Multimap<Integer, Integer> clusterIcons = HashMultimap.create();

    private Handler mHandler;

    private final GeoPoint GENT = new GeoPoint(51.054342, 3.717424);

    private OnFragmentInteractionListener mListener;

    private MapView osm;
    private MapController mc;
    private Marker marker;


    //wordt aangeroepen in de mainActivity en geeft de index door

    /**
     * is called from the activity containing the fragment and instantiates the fragment
     * @param index position of the item in the list of items in the repository
     * @return the newly created MapFragment
     */
    public static MapFragment getNewInstance(int index) {
        MapFragment newFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ITEM_SELECTED_POSITION, index);

        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(ITEM_SELECTED_POSITION);
        }
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        createClusterIcons();

        osm = (MapView) rootView.findViewById(R.id.mapView);
        osm.setTileSource(TileSourceFactory.MAPNIK);
        osm.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        osm.setMultiTouchControls(true);
        osm.setBuiltInZoomControls(true);
        osm.setMinZoomLevel(12);


        //limit the map in a radius
        BoundingBoxE6 mapBounds = new BoundingBoxE6(51.267070677950585, 4.2633819580078125, 50.731240007135064, 3.2196807861328125);
        osm.setScrollableAreaLimit(mapBounds);

        //change the zoom
        mc = (MapController) osm.getController();
        mc.setZoom(13);

        mc.setCenter(GENT);

        ItemRepository.registerListener(this);
        mHandler = new Handler();
        updatePolylines.run();
        //startRepeatingTask();

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            currentPosition = args.getInt(ITEM_SELECTED_POSITION);
        }
        updateMap(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(ITEM_SELECTED_POSITION, currentPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        osm.getTileProvider().clearTileCache();
        mHandler.removeCallbacks(updatePolylines);
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
    }


    //update de kaart wanneer een ander item is geselecteerd

    /**
     * update the map for the specified index
     * @param index the index of the item in the list, if the index is -1, show all items
     */
    public void updateMap(int index) {
        currentPosition = index;
        updateMap(true);
    }


    /**
     * updates the display of the item or items
     * @param center true when the mapview needs to be centered
     */
    private void updateMap(final boolean center) {

                    osm.getOverlays().clear();
                    clusters = new ArrayList<>();

                    if (currentPosition > -1) {

                        if (currentPosition < ItemRepository.getItems().size()) {
                            Item item = ItemRepository.getItems().get(currentPosition);
                            addItemToMap(item);
                        }
                    } else {
                        if (center) {
                            final GeoPoint GENT = new GeoPoint(51.054342, 3.717424);
                            mc.setCenter(GENT);
                            mc.animateTo(GENT);
                            mc.zoomTo(12);
                        }
                        try {

                            for (Item item : Lists.reverse(ItemRepository.getItems())) {
                                addItemToMap(item);
                            }
                        }catch(Exception ignored){

                        }
                    }
    }

    /**
     * display an item
     * @param item the item to be displayed
     */
    private void addItemToMap(Item item){
        if (item != null) {
                if (item.getLocation() != null) {
                    addMarker(item);
                }

                if (item.getLine() != null) {
                    addRoadLine(item);
                }

                if (item.getLine() == null && item.getLocation() == null) {
                    addDialog(item);
                }
        }
    }


    /**
     * display a dialog with information about the item
     * @param item the item of which info needs to be shown
     */
    private void addDialog(Item item) {
        if (currentPosition > -1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final AlertDialog dialog = builder.create();

            View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);

            TextView title = (TextView) dialogLayout.findViewById(R.id.customDialogTitle);
            title.setText(item.getTitle());

            TextView desc = (TextView) dialogLayout.findViewById(R.id.customDialogDescription);
            desc.setText(item.getDetails());

            TextView date = (TextView) dialogLayout.findViewById(R.id.customDialogDate);
            date.setText(new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(item.getDate()));

            if (item instanceof TwitterItem) {
                if (((TwitterItem) item).getMediaUrl() != null && !(((TwitterItem) item).getMediaUrl().equals(""))) {
                    ImageView image = (ImageView) dialogLayout.findViewById(R.id.customDialogImage);
                    Picasso.with(getContext()).load(((TwitterItem) item).getMediaUrl()).into(image);
                }

                if (((TwitterItem) item).getUserImageUrl() != null && !(((TwitterItem) item).getUserImageUrl().equals(""))) {
                    ImageView image = (ImageView) dialogLayout.findViewById(R.id.customDialogUserImage);
                    Picasso.with(getContext()).load(((TwitterItem) item).getUserImageUrl()).into(image);
                }

                title.setText(((TwitterItem) item).getUser().getName());
                TextView subtitle = (TextView) dialogLayout.findViewById(R.id.customDialogSubtitle);
                subtitle.setText("@"+((TwitterItem) item).getUser().getScreenname());

                addReplyDialog(dialogLayout, (TwitterItem) item);
            }

            dialog.setView(dialogLayout);

            dialog.show();

            osm.invalidate();
        }
    }

    /**
     * adds a sublayout to the dialog to display related items
     * @param dialoglayout the original dialog
     * @param replyitem the item of which info needs to be shown
     */
    private void addReplyDialog(View dialoglayout, TwitterItem replyitem) {

        LinearLayout repliesLayout = (LinearLayout) dialoglayout.findViewById(R.id.repliesLayout);
        if (replyitem.getInReplyToStatusId() != null) {
            for (Item item : ItemRepository.getItems()) {

                if (item instanceof TwitterItem && item.getId().equals(replyitem.getInReplyToStatusId())) {

                    View replyLayout = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);
                    TextView replytitle = (TextView) replyLayout.findViewById(R.id.customDialogTitle);
                    replytitle.setText(((TwitterItem) item).getUser().getName());
                    TextView subtitle = (TextView) replyLayout.findViewById(R.id.customDialogSubtitle);
                    subtitle.setText("@"+((TwitterItem)item).getUser().getScreenname());
                    TextView replydesc = (TextView) replyLayout.findViewById(R.id.customDialogDescription);
                    replydesc.setText(item.getDetails());

                    TextView replydate = (TextView) replyLayout.findViewById(R.id.customDialogDate);
                    replydate.setText(new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(item.getDate()));

                    if (((TwitterItem) item).getMediaUrl() != null && !(((TwitterItem) item).getMediaUrl().equals(""))) {
                        ImageView image = (ImageView) replyLayout.findViewById(R.id.customDialogImage);
                        Picasso.with(getContext()).load(((TwitterItem) item).getMediaUrl()).into(image);
                    }

                    if (((TwitterItem) item).getUserImageUrl() != null && !(((TwitterItem) item).getUserImageUrl().equals(""))) {
                        ImageView image = (ImageView) replyLayout.findViewById(R.id.customDialogUserImage);
                        Picasso.with(getContext()).load(((TwitterItem) item).getUserImageUrl()).into(image);
                    }

                    if(!(repliesLayout.getChildCount()>8)) {
                        repliesLayout.addView(replyLayout);
                        if (((TwitterItem) item).getInReplyToStatusId() != null) {
                            addReplyDialog(replyLayout, (TwitterItem) item);
                        }
                    }
                }
            }
        }
    }


    /**
     * adds a marker to the mapview
     * @param item the item that needs to be displayed on the map
     */
    private void addMarker(final Item item) {

        if (!(item.isRemoved()) || currentPosition > -1) {
            try {

                Location location = item.getLocation();

                final GeoPoint pos = new GeoPoint(location.getY(), location.getX());

                if (currentPosition > -1) {
                    //mc.setCenter(pos);
                    mc.animateTo(pos);
                    if(!mListener.isSinglePaneActive())
                        mc.zoomTo(15);
                }

                        String titel = item.getTitle();
                        String omschrijving = item.getDescription();
                        String details = item.getDetails();

                        Drawable icon = ContextCompat.getDrawable(getContext(), item.getIcon());

                        // Scale icon naar 80x80
                        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                        Drawable markerIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));

                        if (marker != null)
                            marker.closeInfoWindow();

                        marker = new Marker(osm);
                        marker.setPosition(pos);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setTitle(titel);
                        marker.setSnippet(omschrijving);
                        marker.setSubDescription(details);
                        marker.setIcon(markerIcon);

                        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                mListener.onFragmentInteraction(false, ItemRepository.getItems().indexOf(item));
                                if (!(item instanceof TwitterItem))
                                    marker.showInfoWindow();
                                else
                                    addDialog(item);

                                addRoadLine(item);
                                return true;
                            }
                        });

                        if (clusterIcons.containsValue(item.getIcon())) {
                            for (int clustericon : clusterIcons.keySet()) {
                                if (clusterIcons.containsEntry(clustericon, item.getIcon())) {
                                    addMarkerToCluster(clustericon, marker);
                                }
                            }
                        } else {
                            osm.getOverlays().add(marker);
                        }
            } catch (NullPointerException ignored) {
            }
        }
    }

    /**
     * interval to update the mapview's polylines
     */
    Runnable updatePolylines = new Runnable() {
        @Override
        public void run() {
                try {
                    if(ItemRepository.getItems().size()>0&&!(currentPosition > -1)) {
                        for (Overlay o : osm.getOverlays()) {
                            if (o instanceof Polyline)
                                osm.getOverlays().remove(o);
                        }
                            for (Item item : Lists.reverse(ItemRepository.getItems())) {
                                if (item.getLine() != null)
                                    addRoadLine(item);
                            }
                    }
                } finally {
                    mHandler.postDelayed(updatePolylines, 5000);
                }
        }
    };

    /**
     * adds a polyline on the map for the specified item
     * @param item the item that needs to be displayed on the map
     */
    private void addRoadLine(final Item item) {

        if (item.getLine() != null) {


            final ArrayList<GeoPoint> roadLine = new ArrayList<>();
            final int MAX_POINTS = 20;
            final int color;
            if (item.getLocation() == null) {
                if (item instanceof WazeItem)
                    color = Color.RED;
                else
                    color = Color.YELLOW;
            } else {
                color = Color.GRAY;
            }

            List<Location> line = item.getLine();
            for (Location l : line) {
                GeoPoint pos = new GeoPoint(l.getY(), l.getX());
                roadLine.add(pos);
            }

            if (item.getLocation() == null && currentPosition != -1) {
                //mc.setCenter(roadLine.get(0));
                mc.animateTo(roadLine.get((roadLine.size()-1)/2));
                if(!mListener.isSinglePaneActive())
                    mc.zoomTo(15);
            }

                    final ArrayList<GeoPoint> zoomPoints = new ArrayList<>(roadLine);
                    if ((!(currentPosition > -1))) {
                        //Remove any points that are offscreen
                        BoundingBoxE6 bounds = osm.getBoundingBox();

                        for (Iterator<GeoPoint> iterator = zoomPoints.iterator(); iterator.hasNext(); ) {
                            GeoPoint point = iterator.next();

                            boolean inLongitude = point.getLatitudeE6() < bounds.getLatNorthE6() && point.getLatitudeE6() > bounds.getLatSouthE6();
                            boolean inLatitude = point.getLongitudeE6() > bounds.getLonWestE6() && point.getLongitudeE6() < bounds.getLonEastE6();
                            if (!inLongitude || !inLatitude) {
                                iterator.remove();
                            }
                        }
                    }

                    //If there's still too many then thin the array
                    if (zoomPoints.size() > MAX_POINTS) {
                        int stepSize = zoomPoints.size() / MAX_POINTS;
                        int count = 1;
                        for (Iterator<GeoPoint> iterator = zoomPoints.iterator(); iterator.hasNext(); ) {
                            iterator.next();

                            if (count != stepSize) {
                                iterator.remove();
                            } else {
                                count = 0;
                            }
                            count++;
                        }
                    }

                    //Update the map on the event thread
                    osm.post(new Runnable() {
                        @Override
                        public void run() {
                            //ideally the Polyline construction would happen in the thread but that causes glitches while the event thread
                            //waits for redraw:
                            if (getContext() != null) {
                                Polyline pathOverlay = new Polyline(getContext());
                                pathOverlay.setRelatedObject(item);
                                pathOverlay.setPoints(zoomPoints);
                                pathOverlay.setColor(color);
                                pathOverlay.setWidth(8f);
                                pathOverlay.setOnClickListener(new Polyline.OnClickListener() {
                                    @Override
                                    public boolean onClick(Polyline polyline, MapView mapView, GeoPoint geoPoint) {
                                        if (!item.isRemoved())
                                            mListener.onFragmentInteraction(false, ItemRepository.getItems().indexOf(item));

                                        Marker marker = new Marker(mapView);
                                        marker.setPosition(geoPoint);
                                        marker.setAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_BOTTOM);
                                        marker.setTitle(item.getTitle());
                                        marker.setSnippet(item.getDescription());
                                        marker.setSubDescription(item.getDetails());
                                        marker.setFlat(true);
                                        marker.setInfoWindowAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_BOTTOM);
                                        marker.showInfoWindow();
                                        return false;
                                    }
                                });
                                osm.getOverlays().add(0, pathOverlay);
                                osm.invalidate();
                            }
                        }
                    });
        }
    }

    /**
     * groups the items that need to be clustered together
     */
    private void createClusterIcons() {
        clusterIcons.put(R.drawable.cluster_parking, R.drawable.parking_icon_red);

        clusterIcons.put(R.drawable.cluster_roadclosed, R.drawable.waze_roadclosed);

        clusterIcons.put(R.drawable.cluster_traffic, R.drawable.waze_traffic_heavy);
        clusterIcons.put(R.drawable.cluster_traffic, R.drawable.waze_traffic_medium);
        clusterIcons.put(R.drawable.cluster_traffic, R.drawable.waze_traffic_light);
        clusterIcons.put(R.drawable.cluster_traffic, R.drawable.coyote_jam);

        clusterIcons.put(R.drawable.cluster_twitter, R.drawable.twitter_icon_mention);
        clusterIcons.put(R.drawable.cluster_twitter, R.drawable.twitter_icon_tweet);
    }

    /**
     * adds a marker to the set of clusters
     * @param icon the icon of the cluster
     * @param marker the marker to be added to the cluster
     */
    private void addMarkerToCluster(int icon, Marker marker) {

        RadiusMarkerClusterer rmc = null;
        for (RadiusMarkerClusterer c : clusters) {

            if (c.getName().equals("" + icon))
                rmc = c;
        }
        if (rmc == null) {

            rmc = new RadiusMarkerClusterer(getContext());
            rmc.setRadius(100);


            Drawable clusterIconD = ResourcesCompat.getDrawable(getResources(), icon, null);
            Bitmap clusterIcon = Bitmap.createScaledBitmap(((BitmapDrawable) clusterIconD).getBitmap(), 80, 80, true);
            rmc.setIcon(clusterIcon);


            rmc.getTextPaint().setTextSize(14.0f);
            rmc.mAnchorV = Marker.ANCHOR_BOTTOM;

            rmc.mTextAnchorU = 0.2f;
            rmc.mTextAnchorV = 0.23f;
            rmc.onSingleTapConfirmed(MotionEvent.obtain(0, 0, 0, 0, 0, 0), osm);

            rmc.setName("" + icon);


            clusters.add(rmc);
            osm.getOverlays().add(rmc);
        }

        rmc.add(marker);
    }


    @Override
    public void onItemsLoaded() {
    }

    @Override
    public void onAllItemsLoaded() {
        updateMap(-1);
        if(swipeRefreshLayout!=null&&swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAllItemsRefreshed() {
        if(!(currentPosition>-1))
            updateMap(false);
    }

    @Override
    public void onItemsLoading() {
        if(swipeRefreshLayout!=null&&mListener.isSinglePaneActive())
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onItemsSorted() {
        //updateMap(false);
    }

    @Override
    public void onItemRemoved(Item item) {
    }

    @Override
    public void onItemAdded(Item item) {
        addItemToMap(item);
    }

    @Override
    public void onLoadFailed() {
    }

}
