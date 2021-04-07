package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public abstract class BM_Content extends CustomView {
    public BM_Content(Activity con, int R_layout) {
        super(con, R_layout);
    }

    boolean isInProgress = false;

    public void setProgress(double progress) {
        isInProgress = true;
        ((CircularProgressBar) _f(R.id.circularProgressBar)).setProgress((float) progress);
    }

    @Override
    protected void initEvent() {

    }

    public void done() {
        isInProgress = false;
        _f(R.id.circularProgressBar).animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                _f(R.id.circularProgressBar).setVisibility(View.GONE);
                _f(R.id.udBtn).setVisibility(View.GONE);
            }
        }).start();
    }

    public void stop() {
        _f(R.id.udBtn).animate().alpha(0).setDuration(100).withEndAction(() -> {
            ((ImageView) _f(R.id.udBtn)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.upload));
            _f(R.id.circularProgressBar).animate().alpha(0).start();
            _f(R.id.udBtn).animate().alpha(1).setDuration(100).setDuration(100).start();
        });
    }

    public void continua() {
        _f(R.id.udBtn).animate().alpha(0).setDuration(100).withEndAction(() -> {
            ((ImageView) _f(R.id.udBtn)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.cancel));
            _f(R.id.circularProgressBar).animate().alpha(1).start();
            _f(R.id.udBtn).animate().alpha(1).setDuration(100).setDuration(100).start();
        });
    }
}
