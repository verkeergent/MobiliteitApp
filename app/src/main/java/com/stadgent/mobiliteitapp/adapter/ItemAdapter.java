package com.stadgent.mobiliteitapp.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.activities.OnFragmentInteractionListener;
import com.stadgent.mobiliteitapp.model.CoyoteItem;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ParkingItem;
import com.stadgent.mobiliteitapp.model.TwitterItem;
import com.stadgent.mobiliteitapp.model.VGSItem;
import com.stadgent.mobiliteitapp.model.WazeItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    private List<Item> items = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private static Context context;
    private static boolean allItemsLoaded=false;

    private RecyclerView rv;
    private int selectedpos = -1;


    //doorgeven van de fragmentInteractionListener en items
    public ItemAdapter(List<Item> items, OnFragmentInteractionListener mListener, Context context) {
        this.items = new ArrayList<>();
        for(Item i: items){
            if(!i.isRemoved())
                this.items.add(i);
        }
        //this.items = items;
        this.mListener = mListener;
        ItemAdapter.context = context;
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item, viewGroup, false);
        return new ItemViewHolder(v);
    }


    public void setItems(List<Item> items) {
        this.items = new ArrayList<>();
        List<Item> tempitems = new ArrayList<>();
        for(Item i: items){
            if(!(i.isRemoved()))
                tempitems.add(i);
        }
        this.items=tempitems;
        allItemsLoaded=false;
    }

    public void markCard(int i) {
            try {

                    int oldpos = selectedpos;
                    selectedpos = i;
                    notifyItemChanged(i);
                    notifyItemChanged(oldpos);
                if(i>-1) {
                    rv.scrollToPosition(i);
                }

            } catch (IndexOutOfBoundsException ignored) {
            }
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {

        //pas text en omschrijving in de itemviewholder aan voor elk item
        final Item item = items.get(i);

        itemViewHolder.itemTitle.setText(item.getTitle());
        itemViewHolder.itemDescription.setText(item.getDescription());
        itemViewHolder.itemCount.setText("" + (i + 1));
        if (item.getDate() != null)
            itemViewHolder.itemDateTime.setText(new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH).format(item.getDate()));


        itemViewHolder.itemIcon.setImageResource(item.getIcon());


        int color;
        try {
            color = ContextCompat.getColor(context, item.getColor());
        }catch(Exception e){
            color = ContextCompat.getColor(context, R.color.other);
        }

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(5);

        //
        if (i==selectedpos) {
            gd.setStroke(3, 0xFF000000);
            itemViewHolder.itemView.setBackground(gd);
        }

        itemViewHolder.itemView.setBackground(gd);

        //onclicklistener voor de items in de recyclerview (item op kaart)
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markCard(-1);
                mListener.onFragmentInteraction(true, itemViewHolder.getAdapterPosition());
            }
        });

        //onlongclick voor de items in de recyclerview (opties voor delen)
        itemViewHolder.onLongClick(item);

        //animateCard(itemViewHolder);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.rv = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /*public static void animateCard(RecyclerView.ViewHolder holder) {
        if(allItemsLoaded) {
            holder.itemView.setPivotY(0);
            holder.itemView.setPivotX(holder.itemView.getHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", 200, 0);
            ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(holder.itemView, "rotationX", -90f, 0f);
            ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1f);
            animatorSet.playTogether(animatorTranslateY, animatorRotation, animatorScaleX);
            animatorSet.setInterpolator(new DecelerateInterpolator(1.1f));
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    public void onAllItemsLoaded() {
        allItemsLoaded=true;
    }*/


    //itemviewholder: custom layout voor items
    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView itemIcon;
        TextView itemTitle;
        TextView itemDescription;
        TextView itemCount;
        TextView itemDateTime;
        RelativeLayout layout;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.itemCardView);
            itemIcon = (ImageView) itemView.findViewById(R.id.itemIcon);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemDescription = (TextView) itemView.findViewById(R.id.itemDescription);
            itemCount = (TextView) itemView.findViewById(R.id.itemCount);
            itemDateTime = (TextView) itemView.findViewById(R.id.itemDateTime);
            layout = (RelativeLayout) itemView.findViewById(R.id.itemLayout);

        }

        public void onLongClick(final Item item){

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    final String[] options = {"Mail", "SMS", "Tweet"};

                    final Integer[] icons = {R.drawable.icon_email, R.drawable.icon_sms, R.drawable.icon_twitter};

                    ListAdapter adapter = new ArrayAdapterWithIcon(context, options, icons);


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delen:");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int option) {

                            if (option == 0) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                                i.putExtra(Intent.EXTRA_SUBJECT, "Opgelet: " + item.getTitle());
                                i.putExtra(Intent.EXTRA_TEXT, "Beste,\n\n" + item.getDescription());

                                try {
                                    context.startActivity(i);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(context, "Installeer een e-mailclient", Toast.LENGTH_SHORT).show();
                                }
                            } else if (option == 1) {
                                String srcNumber = "";
                                String message = "" + item.getTitle() + "\n" + item.getDescription();
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + srcNumber));
                                i.putExtra("sms_body", message);
                                context.startActivity(i);
                            } else if (option == 2) {
                                String text = item.getDescription();

                                text = text.replaceAll("#","%23");
                                text = text.replaceAll("&","%26");

                                String tweetUrl = "https://twitter.com/intent/tweet?text=" + text;
                                Uri uri = Uri.parse(tweetUrl);
                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    context.startActivity(i);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(context, "Installeer Twitter op uw toestel", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                    return true;
                }
            });

        }
    }
}
