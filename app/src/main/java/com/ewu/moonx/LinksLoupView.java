package com.ewu.moonx;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class LinksLoupView extends View {
    ImageView searchBtn;
    ArrayList<View> views;
    Paint paint;

    public LinksLoupView(Context context) {
        super(context);
        init(context);
    }

    public LinksLoupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinksLoupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void addView(ImageView searchBtn, ArrayList<View> views) {
        this.searchBtn = searchBtn;
        this.views = views;
        postInvalidate();
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (searchBtn != null && views != null) {
            canvas.drawLine(searchBtn.getLeft(), searchBtn.getTop(),
                    searchBtn.getLeft() + 100, searchBtn.getTop(), paint);
            canvas.drawRect(searchBtn.getLeft(),searchBtn.getTop(),
                    searchBtn.getRight(),searchBtn.getBottom(),paint);
        }
    }
}
