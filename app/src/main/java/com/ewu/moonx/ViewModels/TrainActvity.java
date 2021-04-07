package com.ewu.moonx.ViewModels;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.ewu.moonx.R;

import java.util.ArrayList;

public class TrainActvity extends AppCompatActivity {
    Palette palette;
    ImageView imageView;
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    int d = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train);

        imageView = findViewById(R.id.pic);

        findViewById(R.id.run).setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Palette.from(bitmap).maximumColorCount(32).generate(palette -> {
                vibrantSwatch = palette.getVibrantSwatch();
                lightVibrantSwatch = palette.getLightVibrantSwatch();
                darkVibrantSwatch = palette.getDarkVibrantSwatch();
                mutedSwatch = palette.getMutedSwatch();
                lightMutedSwatch = palette.getLightMutedSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
            });
        });

        findViewById(R.id.copy).setOnClickListener(v -> {
            ArrayList<Integer> colors = new ArrayList<>();
            if (lightMutedSwatch != null) {
                findViewById(R.id.p5).setBackgroundColor(lightMutedSwatch.getRgb());
                colors.add(lightMutedSwatch.getRgb());
            }
            if (vibrantSwatch != null) {
                findViewById(R.id.p).setBackgroundColor(vibrantSwatch.getRgb());
                colors.add(vibrantSwatch.getRgb());
            }
            if (mutedSwatch != null) {
                findViewById(R.id.p4).setBackgroundColor(mutedSwatch.getRgb());
                colors.add(mutedSwatch.getRgb());
            }
            if (lightVibrantSwatch != null) {
                findViewById(R.id.p2).setBackgroundColor(lightVibrantSwatch.getRgb());
                colors.add(lightVibrantSwatch.getRgb());
            }
            if (darkMutedSwatch != null) {
                findViewById(R.id.p6).setBackgroundColor(darkMutedSwatch.getRgb());
                colors.add(darkMutedSwatch.getRgb());
            }
            if (darkVibrantSwatch != null) {
                findViewById(R.id.p3).setBackgroundColor(darkVibrantSwatch.getRgb());
                colors.add(darkVibrantSwatch.getRgb());
            }

            int[] clist = new int[colors.size()];

            for (int i = 0; i < clist.length; i++) {
                clist[i] = colors.get(i);
                if (i == 1)
                    break;
            }

            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, clist);
            gd.setCornerRadius(0f);
            findViewById(R.id.LL).setBackground(gd);
            findViewById(R.id.LL).setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        });

        findViewById(R.id.stop).setOnClickListener(v -> {
            if (d == 0)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.todel));
            else if (d == 1)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.moonx_img));
            else if (d == 2)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.glasslogo));
            else if (d == 3)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.person));
            else if (d == 4)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.yemen));
            else if (d == 5)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.yemen_city_background));

            else if (d == 6)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.option_picture));

            else if (d == 7)
                imageView.setImageDrawable(ContextCompat.getDrawable(TrainActvity.this, R.drawable.clap));
            else
                d = 0;

            d++;

            findViewById(R.id.LL).setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        });

    }
}
