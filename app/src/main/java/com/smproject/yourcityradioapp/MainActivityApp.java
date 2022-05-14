package com.smproject.yourcityradioapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smproject.yourcityradioapp.test.CreateNotification;
import com.smproject.yourcityradioapp.test.Playable;
import com.smproject.yourcityradioapp.test.Services.OnClearFromRecentService;

public class MainActivityApp extends AppCompatActivity implements Playable {

    ImageView play;
    TextView title;

    NotificationManager notificationManager;

     String stream = "https://c34.radioboss.fm:18234/stream";

     MediaPlayer mediaPlayer;

    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.btn_play);
        title = findViewById(R.id.title);

        mediaPlayer = MediaPlayer.create(this, Uri.parse(stream));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    onTrackPause();
                } else {
                    onTrackPlay();
                }
            }
        });
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    @Override
    public void onTrackPrevious() {

    }

    @Override
    public void onTrackPlay() {

        mediaPlayer.start();

        CreateNotification.createNotification(
                MainActivityApp.this,
                R.drawable.ic_pause_button
        );
        play.setImageResource(R.drawable.ic_pause_button);
        isPlaying = true;

    }

    @Override
    public void onTrackPause() {

        mediaPlayer.pause();

        CreateNotification.createNotification(
                MainActivityApp.this,
                R.drawable.ic_play_button
        );
        play.setImageResource(R.drawable.ic_play_button);
        isPlaying = false;

    }

    @Override
    public void onTrackNext() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }
}
