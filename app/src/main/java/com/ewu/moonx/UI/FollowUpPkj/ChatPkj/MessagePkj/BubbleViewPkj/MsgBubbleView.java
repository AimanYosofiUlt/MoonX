package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_Content;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_TextContent;

import java.util.ArrayList;

public abstract class MsgBubbleView extends BubbleView {
    Users user;
    Messages message;
    PublicMessages publicMessage;

    FrameLayout contentFL;
    TextView time;
    RelativeLayout infoLayout;
    BM_Content content;
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

    public MsgBubbleView(Activity con, int R_layout, short bubbleType, Users user, Messages message) {
        super(con, R_layout, bubbleType);
        this.user = user;
        this.message = message;
        setDateStr(message.getDate());
        initInstance();
    }

    public MsgBubbleView(Activity con, int R_layout, short bubbleType, PublicMessages publicMessage) {
        super(con, R_layout, bubbleType);
        this.publicMessage = publicMessage;
        setDateStr(publicMessage.getDate());
        if (publicMessage.getReplayMsgId() != null) {
            addPublicReplay();
        }
        initInstance();
    }

    public static void clear() {
        MsgBubbleView.selectedBubble = null;
    }

    public void setUnselected() {
        _f(R.id.mainRL).setBackgroundColor(con.getResources().getColor(R.color.noneColor));
    }

    protected final void addPublicReplay() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        Cursor cursor = DB.select(table.idCol)
                .select(table.userNameCol)
                .select(table.statueCol)
                .select(table.textCol)
                .from(table).where(table.idCol, publicMessage.getReplayMsgId()).start();

        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            ReplayView replayView = new ReplayView(con, cursor.getString(0)
                    , ((cursor.getString(2).contains("Its"))) ? con.getString(R.string.You) : cursor.getString(1)
                    , cursor.getString(3));

            _l(R.id.replayFL).addView(replayView.getMainView());
            _l(R.id.replayFL).setVisibility(View.VISIBLE);
        }

        cursor.close();
    }

    private void initInstance() {
        if (selectedBubble == null)
            selectedBubble = new ArrayList<>();

        this.infoLayout = mainView.findViewById(R.id.infoLayout);
        this.tale = mainView.findViewById(R.id.tale);

        this.content = initContent();
        this.contentFL = mainView.findViewById(R.id.contentFL);
        this.contentFL.addView(content.getMainView());

        this.time = mainView.findViewById(R.id.time);
        this.time.setText(Static.getTime(publicMessage.getDate()));

        initEvent();
    }

    private BM_Content initContent() {
        return new BM_TextContent(con, publicMessage.getText());
    }

    public PublicMessages getPublicMessage() {
        return publicMessage;
    }


    public BM_Content getContent() {
        return content;
    }

    public void setContent(BM_Content content) {
        this.content = content;
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

        theMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedBubble.isEmpty()) {
                    if (selectedBubble.contains(MsgBubbleView.this))
                        deleteBubble();
                    else
                        selectBubble();
                }
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
                            releaseView(event);
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

            private void releaseView(MotionEvent event) {
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

                    Intent intent = new Intent(con.getString(R.string.replay_messagesBroadcast));
                    intent.putExtra(Static.PublicMsg, publicMessage);
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

}
