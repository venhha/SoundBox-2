package com.soundbox.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.soundbox.R;
import com.soundbox.activity.MainActivity;
import com.soundbox.model.Song;


public class MyService extends Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;
    private MediaPlayer mediaPlayer;

    private Boolean isplaying;

    private Song newMusic;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Myservice", "MyService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song music = (Song) bundle.get("object-music");
            if (music != null) {
                newMusic = music;
                startMusic(music);
                sendNotification(music);
            }
        }

        int actionMusic = intent.getIntExtra("action_music_service", 0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void startMusic(Song music) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(music.getSongUrl()));
        }
        mediaPlayer.start();
        isplaying = true;
        sendActionToActyvity(ACTION_START);
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActyvity(ACTION_CLEAR);
                break;
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !isplaying) {
            mediaPlayer.start();
            isplaying = true;
            sendNotification(newMusic);
            sendActionToActyvity(ACTION_RESUME);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isplaying) {
            mediaPlayer.pause();
            isplaying = false;
            sendNotification(newMusic);
            sendActionToActyvity(ACTION_PAUSE);
        }
    }

    private void sendNotification(Song music) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_20);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_customer_music);
        remoteViews.setTextViewText(R.id.text_view_title_music, music.getSongName());
        remoteViews.setTextViewText(R.id.text_view_single_music, music.getSongArtist());
        remoteViews.setImageViewBitmap(R.id.img_music, bitmap);
        remoteViews.setImageViewResource(R.id.button_play_pause, R.drawable.ic_play_20);

        if (isplaying) {
            remoteViews.setOnClickPendingIntent(R.id.button_play_pause, getPendingInten(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.button_play_pause, R.drawable.ic_pause_20);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.button_play_pause, getPendingInten(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.button_play_pause, R.drawable.ic_play_20);
        }

        remoteViews.setOnClickPendingIntent(R.id.button_clear, getPendingInten(this, ACTION_CLEAR));

//        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setPriority(PRIORITY_MIN)
//                .setContentIntent(pendingIntent)
//                .setCustomContentView(remoteViews)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .setSound(null)
//                .build();
//
//       startForeground(1,notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setCustomContentView(remoteViews)
                    .setSound(null)
                    .build();
            startForeground(2, notification);
        }

    }

    private PendingIntent getPendingInten(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);


        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Myservice", "MyService onDestroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendActionToActyvity(int action) {
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_music", newMusic);
        bundle.putBoolean("status_player", isplaying);
        bundle.putInt("action_music", action);

        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
