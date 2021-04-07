package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class BM_SoundContent extends BM_Content {
    boolean isInPlayMode = true;
    MediaPlayer player;
    private Handler mHandler;

    public BM_SoundContent(Activity con, String fileName) {
        super(con, R.layout.view_sound_content);
        player = new MediaPlayer();
        try {
            player.setDataSource(Static.getRecordFile(con, fileName).getPath());
            player.prepare();
        } catch (IOException e) {
            Toast.makeText(con, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        initInerEvent();
    }

    public void setAsSender() {
        _f(R.id.start_play).setVisibility(View.VISIBLE);
        _f(R.id.seekBar_luminosite).setVisibility(View.VISIBLE);
    }

    protected void initInerEvent() {
        _f(R.id.udBtn).setOnClickListener(v -> {
            if (isInProgress) {
                stop();
            } else {
                continua();
            }
        });

        _f(R.id.start_play).setOnClickListener(v -> {
            if (isInPlayMode) {
                _f(R.id.start_play).animate().alpha(0).setDuration(100).withEndAction(() -> {
                    ((ImageView) _f(R.id.start_play)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.pause));
                    _f(R.id.start_play).animate().alpha(1).setDuration(100).start();
                }).start();

                player.start();
            } else {
                _f(R.id.start_play).animate().alpha(0).setDuration(100).withEndAction(() -> {
                    ((ImageView) _f(R.id.start_play)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.play));
                    _f(R.id.start_play).animate().alpha(1).setDuration(100).start();
                }).start();
                player.pause();
            }
            isInPlayMode = !isInPlayMode;
        });

        player.setOnCompletionListener(mp -> {
            player.stop();

            _f(R.id.start_play).animate().alpha(0).setDuration(100).withEndAction(() -> {
                ((ImageView) _f(R.id.start_play)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.play));
                _f(R.id.start_play).animate().alpha(1).setDuration(100).start();
            }).start();
        });

        ((SeekBar) _f(R.id.seekBar_luminosite)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void done() {
        super.done();
        _f(R.id.viewImage2).animate().alpha(0).withEndAction(() -> {
            _f(R.id.viewImage).setVisibility(View.VISIBLE);
            _f(R.id.seekBar_luminosite).setVisibility(View.VISIBLE);
            _f(R.id.start_play).setVisibility(View.VISIBLE);
            _f(R.id.viewImage).animate().alpha(1).start();
            _f(R.id.viewImage2).setVisibility(View.GONE);
        }).start();
    }

}
