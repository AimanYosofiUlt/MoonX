package com.ewu.moonx.UI.UserPkj;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.ewu.moonx.R;
import com.wang.avi.AVLoadingIndicatorView;

public class RefreshButton {
    ImageView imageView;
    AVLoadingIndicatorView avi;
    Activity con;
    RefreshButtonListener listener;

    public void setListener(RefreshButtonListener listener) {
        this.listener = listener;
    }

    interface RefreshButtonListener {
        void onClick();
    }

    public RefreshButton(Activity con, int RId_Img, int RId_AVI) {
        this.con = con;
        imageView = con.findViewById(RId_Img);
        avi = con.findViewById(RId_AVI);

        initEvent();

    }

    private void initEvent() {
        imageView.setOnClickListener(v -> {
            avi.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setEnabled(false);
            avi.animate().alpha(1);

            listener.onClick();
        });
    }

    public ImageView geRefreshImg() {
        return imageView;
    }


    public void done(boolean hasError) {
        avi.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                avi.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                if (hasError)
                    imageView.setImageDrawable(con.getResources().getDrawable(R.drawable.error));
                else
                    imageView.setImageDrawable(con.getResources().getDrawable(R.drawable.done));
            }
        });


        imageView.animate().alpha(0).setStartDelay(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                imageView.animate().alpha(1).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        if (hasError)
                            imageView.setImageDrawable(con.getResources().getDrawable(R.drawable.refresh_error));
                        else
                            imageView.setImageDrawable(con.getResources().getDrawable(R.drawable.refresh));
                        imageView.setEnabled(true);
                    }
                });
            }
        });

    }
}
