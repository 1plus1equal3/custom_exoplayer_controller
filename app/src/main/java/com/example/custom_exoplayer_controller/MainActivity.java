package com.example.custom_exoplayer_controller;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

public class MainActivity extends AppCompatActivity {

    //UI player Views
    /*LinearLayout controllerClick;*/
    ImageButton uiBackBtn, play_pauseBtn, playerBackBtn, playerForwardBtn, playerSkipPreviousBtn, playerSkipNextBtn;
    ImageView songImage;
    TextView songName, artistName, timeStartProgress, timeEndProgress;
    ProgressBar songProgress;

    //Exoplayer player
    ExoPlayer player;

    //Thread for progress update
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind view's id
        uiBackBtn = findViewById(R.id.ui_back_btn);
        play_pauseBtn = findViewById(R.id.play_pause_btn);
        playerBackBtn = findViewById(R.id.back_btn);
        playerForwardBtn = findViewById(R.id.forward_btn);
        playerSkipPreviousBtn = findViewById(R.id.skip_previous_btn);
        playerSkipNextBtn = findViewById(R.id.skip_next_btn);
        songImage = findViewById(R.id.song_image);
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.artist_name);
        timeStartProgress = findViewById(R.id.time_start_progress);
        timeEndProgress = findViewById(R.id.time_end_progress);
        songProgress = findViewById(R.id.progressBar);

        //Set up player
        player = new ExoPlayer.Builder(this).build();
        MediaItem item0 = MediaItem.fromUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_song_0));
        MediaItem item1 = MediaItem.fromUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_song_1));
        MediaItem item2 = MediaItem.fromUri(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_song_2));
        player.addMediaItem(item0);
        player.addMediaItem(item1);
        player.addMediaItem(item2);
        player.prepare();

        //Set max progress
        player.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    songProgress.setMax((int) player.getDuration() / 1000);
                    Log.e("Song duration: ", String.valueOf(player.getDuration()));
                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    play_pauseBtn.setBackground(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_play_circle_24));
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying)
                    play_pauseBtn.setBackground(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_pause_circle_24));
                else
                    play_pauseBtn.setBackground(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_play_circle_24));
            }

        });

        //Progress update
        runnable = () -> {
            songProgress.setProgress((int) player.getCurrentPosition() / 1000);
            long minuteStart = player.getCurrentPosition() / 60000;
            long secondStart = (player.getCurrentPosition() / 1000) % 60;
            long minuteEnd = (player.getDuration() - player.getCurrentPosition()) / 60000;
            long secondEnd = ((player.getDuration() - player.getCurrentPosition()) / 1000) % 60;
            setProgressStartTime(minuteStart, secondStart);
            setProgressEndTime(minuteEnd, secondEnd);
            handler.post(runnable);
        };
        handler.postDelayed(runnable, 1000);

    }

    //Set progress timeline
    public void setProgressStartTime(long minute, long second) {
        String minuteStr;
        String secondStr;
        if (minute < 10) minuteStr = "0" + minute;
        else minuteStr = "" + minute;
        if (second < 10) secondStr = "0" + second;
        else secondStr = "" + second;
        timeStartProgress.setText(minuteStr + ":" + secondStr);
    }

    public void setProgressEndTime(long minute, long second) {
        String minuteStr;
        String secondStr;
        if (minute < 10) minuteStr = "0" + minute;
        else minuteStr = "" + minute;
        if (second < 10) secondStr = "0" + second;
        else secondStr = "" + second;
        timeEndProgress.setText(minuteStr + ":" + secondStr);
    }

    //Player's button onClick methods
    public void playBtn(View view) {
        if (!player.isPlaying()) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        } else {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    public void replayBtn(View view) {
        if (player.getCurrentPosition() >= 10000)
            player.seekTo(player.getCurrentPosition() - 10000);
        else
            player.seekTo(0);
    }

    public void forwardBtn(View view) {
        if (player.getCurrentPosition() < player.getDuration() - 10000)
            player.seekTo(player.getCurrentPosition() + 10000);
    }

    public void skipPreviousBtn(View view) {
        if (player.hasPreviousMediaItem())
            player.seekToPreviousMediaItem();
        else
            player.seekTo(0);
    }

    public void skipNextBtn(View view) {
        if (player.hasNextMediaItem())
            player.seekToNextMediaItem();
        else
            player.seekTo(player.getDuration());
    }
}