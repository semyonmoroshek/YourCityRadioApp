package com.smproject.yourcityradioapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.smproject.yourcityradioapp.fromudemy.Player;
import com.smproject.yourcityradioapp.fromudemy.PlayerService;
import com.smproject.yourcityradioapp.test.CreateNotification;
import com.smproject.yourcityradioapp.test.Playable;

public class MainActivityApp extends AppCompatActivity {

    static ImageView play;
    TextView title;
    NotificationManager notificationManager;
    String stream = "https://c34.radioboss.fm:18234/stream";
    PlayerService mBoundService;
    boolean mServiceBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;

        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
            flipPlayPauseButton(isPlaying);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.btn_play);
        title = findViewById(R.id.title);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceBound) {
                    mBoundService.togglePlayer();
                }
            }
        });
        startStreamingService();
    }

    private void startStreamingService() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("url", stream);
        startService(intent);

        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if(mServiceBound) {
//            unbindService(mServiceConnection);
//            mServiceBound = false;
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("changePlayButton"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public static void flipPlayPauseButton(boolean isPlaying) {
        if (isPlaying) {
            play.setImageResource(R.drawable.ic_pause_button);
        } else {
            play.setImageResource(R.drawable.ic_play_button);
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getExtras().getString("actionname");
//
//            switch (action) {
//                case CreateNotification.ACTION_PREVIUOS:
//                    onTrackPrevious();
//                    break;
//                case CreateNotification.ACTION_PLAY:
//                    if (isPlaying) {
//                        onTrackPause();
//                    } else {
//                        onTrackPlay();
//                    }
//                    break;
//                case CreateNotification.ACTION_NEXT:
//                    onTrackNext();
//                    break;
//            }
//        }
//    };

//    @Override
//    public void onTrackPrevious() {
//
//    }

//    @Override
//    public void onTrackPlay() {
//
//        mediaPlayer.start();
//
//        CreateNotification.createNotification(
//                MainActivityApp.this,
//                R.drawable.ic_pause_button
//        );
//        play.setImageResource(R.drawable.ic_pause_button);
//        isPlaying = true;
//
//    }

//    @Override
//    public void onTrackPause() {
//
//        mediaPlayer.pause();
//
//        CreateNotification.createNotification(
//                MainActivityApp.this,
//                R.drawable.ic_play_button
//        );
//        play.setImageResource(R.drawable.ic_play_button);
//        isPlaying = false;
//
//    }

//    @Override
//    public void onTrackNext() {
//
//    }

}
