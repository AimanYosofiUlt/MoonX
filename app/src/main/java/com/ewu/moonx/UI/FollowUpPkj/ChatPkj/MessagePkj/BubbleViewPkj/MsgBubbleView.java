package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_Content;

import java.util.ArrayList;

public abstract class MsgBubbleView extends BubbleView {

    Messages message;
    BM_Content content;

    RelativeLayout infoLayout;
    FrameLayout contentFL;
    TextView time;
    ImageView tale;

    float dX;
    float firstX = 0;
    float firstValue;
    boolean isNotVibrate = false;
    private boolean isInReplayMode = false;

    private static ArrayList<MsgBubbleView> selectedBubble;

    public static ArrayList<MsgBubbleView> getSelectedBubble() {
        return selectedBubble;
    }

    public MsgBubbleView(Activity con, int R_layout, short bubbleType, Messages message) {
        super(con, R_layout, bubbleType);
        this.message = message;
        setDateStr(message.getDate());
        init();
    }

    public static void clear() {
        MsgBubbleView.selectedBubble = null;
    }

    public void setUnselected() {
        _f(R.id.mainRL).setBackgroundColor(con.getResources().getColor(R.color.noneColor));
    }

    public void addReplayView(ReplayView replayView)
    {
        _l(R.id.replayFL).addView(replayView.getMainView());
        _l(R.id.replayFL).setVisibility(View.VISIBLE);
    }

    private void init() {
        if (selectedBubble == null)
            selectedBubble = new ArrayList<>();

        this.infoLayout = mainView.findViewById(R.id.infoLayout);
        this.tale = mainView.findViewById(R.id.tale);
        this.time = mainView.findViewById(R.id.time);
        this.time.setText(Static.getTime(message.getDate()));

        initEvent();
    }
    public Messages getMessage() {
        return message;
    }


    public BM_Content getContent() {
        return content;
    }

    public void setContent(BM_Content content) {
        this.content = content;
        this.contentFL = mainView.findViewById(R.id.contentFL);
        this.contentFL.addView(content.getMainView());
    }


    public String getTime() {
        return time.getText().toString();
    }

    public void setInfoLayoutVisibility(boolean visibility) {
        if (visibility) {
            infoLayout.setVisibility(View.VISIBLE);
        } else {
            infoLayout.setVisibility(View.GONE);
        }
    }

    public void setTaleVisibility(boolean visibility) {
        if (visibility) {
            tale.setVisibility(View.VISIBLE);
        } else {
            tale.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initEvent() {
        View theView = mainView.findViewById(R.id.theView);
        View replayImg = mainView.findViewById(R.id.replay);
        View theMainView = mainView.findViewById(R.id.mainRL);

        theMainView.setOnClickListener(v -> {
            if (!selectedBubble.isEmpty()) {
                if (selectedBubble.contains(MsgBubbleView.this))
                    deleteBubble();
                else
                    selectBubble();
            }
        });

        theMainView.setOnTouchListener(new View.OnTouchListener() {


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (selectedBubble.isEmpty()) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initValue(event);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            moveView(event);
                            break;

                        case MotionEvent.ACTION_UP:
                            releaseView();
                            break;
                        default:
                            return false;
                    }
                }
                return false;
            }

            private void initValue(MotionEvent event) {
                isNotVibrate = true;
                dX = theView.getX() - event.getRawX();
                if (firstX == 0)
                    firstX = theView.getX();
                firstValue = event.getRawX() + dX;
            }

            private void releaseView() {
                isInReplayMode = false;
                theMainView.getParent().requestDisallowInterceptTouchEvent(false);
                isNotVibrate = true;

                theView.animate()
                        .x(firstX)
                        .setDuration(100)
                        .start();
                replayImg.animate().alpha(0).start();
            }

            private void moveView(MotionEvent event) {
                float xValue = event.getRawX() + dX;
                float replayValue = getReplayValue(xValue, firstValue);
                if (isInBoundery(xValue, firstValue) && replayValue < 1.5) {
                    if (replayValue > 0.1) {
                        isInReplayMode = true;
                        theMainView.getParent().requestDisallowInterceptTouchEvent(true);
                    }

                    replayImg.animate().alpha(replayValue).setDuration(0).start();
                    theView.animate()
                            .x(xValue)
                            .setDuration(0)
                            .start();
                } else if (isNotVibrate && replayValue > 1.5) {
                    isNotVibrate = false;
                    Vibrator vb = (Vibrator) con.getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(10);

                    Intent intent = new Intent(con.getString(R.string.replayMessagesBroadcast));
                    intent.putExtra(Static.Message, message);
                    intent.putExtra(Static.BubbleType, bubbleType);
                    con.sendBroadcast(intent);
                }
            }
        });

        theMainView.setOnLongClickListener(v -> {
            if (!isInReplayMode) {
                if (selectedBubble.contains(MsgBubbleView.this))
                    deleteBubble();
                else
                    selectBubble();
                return true;
            } else
                return false;
        });
    }

    protected void selectBubble() {
        selectedBubble.add(MsgBubbleView.this);
        Intent intent = new Intent(con.getString(R.string.selectMsgBroadcast));
        con.sendBroadcast(intent);
        _f(R.id.mainRL).setBackgroundColor(con.getResources().getColor(R.color.selectColor));
    }

    private void deleteBubble() {
        selectedBubble.remove(MsgBubbleView.this);
        Intent intent = new Intent(con.getString(R.string.selectMsgBroadcast));
        con.sendBroadcast(intent);
        _f(R.id.mainRL).setBackgroundColor(con.getResources().getColor(R.color.noneColor));
    }


    protected abstract boolean isInBoundery(float xValue, float firstValue);

    protected abstract float getReplayValue(float xValue, float firstValue);

    public void startBlinkAnime() {
        int colorFrom = con.getResources().getColor(R.color.selectColor);
        int colorTo = con.getResources().getColor(R.color.noneColor);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.setRepeatCount(2);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.addUpdateListener(animator -> _f(R.id.mainRL).setBackgroundColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    public View getInfoLayout() {
        return _f(R.id.infoLayout);
    }

    public View getTale() {
        return _f(R.id.tale);
    }

}
