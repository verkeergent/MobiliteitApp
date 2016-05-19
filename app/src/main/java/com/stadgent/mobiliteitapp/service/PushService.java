package com.stadgent.mobiliteitapp.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.stadgent.mobiliteitapp.R;
import com.stadgent.mobiliteitapp.activities.MainActivity;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.repo.ItemsLoadedListener;
import com.stadgent.mobiliteitapp.utitlity.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 11-May-16.
 */
public class PushService extends Service implements ItemsLoadedListener {

    private static List<String> pushmessageStack = new ArrayList<>();
    private NotificationManagerCompat notificationManager;
    private final static String GROUPKEY = "MOBILITEITPUSHMESSAGES";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager = NotificationManagerCompat.from(this);
        ItemRepository.registerListener(this);

        return Service.START_NOT_STICKY;
    }

    private void sendMessage(int id, String title, String text, int icon) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setGroup(GROUPKEY)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on.
        notificationManager.notify(id, mBuilder.build());

    }

    private void removeMessage(int id){
        notificationManager.cancel(id);
    }


    @Override
    public void onItemsLoaded() {

    }

    @Override
    public void onAllItemsLoaded() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    int id = 0;
                    for (Item item : ItemRepository.getItems()) {

                        if (Priority.getPriority(item) >= 100) {

                            if (!(pushmessageStack.contains(item.getId()))) {
                                if(id>10)
                                    id=0;
                                else
                                    id++;
                                sendMessage(id, item.getTitle(), item.getDescription(), R.drawable.hazardlogo);
                                pushmessageStack.add(item.getId());
                            }
                        }
                    }
                }
            }).start();
        }catch (Exception ignored){}
    }

    @Override
    public void onAllItemsRefreshed() {
        this.onAllItemsLoaded();
    }

    @Override
    public void onItemsLoading() {

    }

    @Override
    public void onItemsSorted() {

    }

    @Override
    public void onItemRemoved(Item item) {

    }

    @Override
    public void onItemAdded(Item item) {

    }

    @Override
    public void onLoadFailed() {

    }
}
