package com.ewu.moonx.UI.FollowUpPkj.ChatPkj;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.KeyboardUtils;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.PublicChatService;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.PublicHandler;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ImageView mediaBtn, cameraBtn, recordBtn, sendBtn;
    ScrollView chatSC;
    boolean isChatDownL = true;
    EditText msgED;
    LinearLayout chatLL;
    PublicHandler handler;
    View cancleReplayBtn;

    View space, chatEditTextLayout;
    private BroadcastReceiver selectMsgBroadcast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
        initHandler();
        iniBroadcasts();
        initEvent();
    }

    private void iniBroadcasts() {
        selectMsgBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MsgBubbleView.getSelectedBubble().isEmpty()) {
                    findViewById(R.id.optionMenu).setVisibility(View.INVISIBLE);
                    findViewById(R.id.infoMenu).setVisibility(View.VISIBLE);
                } else {
                    if (MsgBubbleView.getSelectedBubble().size() == 1) {
                        findViewById(R.id.menu_copy).setVisibility(View.VISIBLE);
                        findViewById(R.id.menu_replay).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.menu_copy).setVisibility(View.GONE);
                        findViewById(R.id.menu_replay).setVisibility(View.GONE);
                    }
                    findViewById(R.id.optionMenu).setVisibility(View.VISIBLE);
                    findViewById(R.id.infoMenu).setVisibility(View.INVISIBLE);
                }
            }
        };
        registerReceiver(selectMsgBroadcast, new IntentFilter(getString(R.string.selectMsgBroadcast)));
    }

    private void init() {
        mediaBtn = findViewById(R.id.mediaBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        recordBtn = findViewById(R.id.recordBtn);
        sendBtn = findViewById(R.id.sendBtn);
        msgED = findViewById(R.id.msgED);
        chatLL = findViewById(R.id.chatLL);
        chatSC = findViewById(R.id.chatSC);
        cancleReplayBtn = findViewById(R.id.cancleReplayBtn);
        space = findViewById(R.id.space);
        chatEditTextLayout = findViewById(R.id.chatEditTextLayout);
        KeyboardUtils.keyBoardIsVisible = false;
    }

    private void setSpaceHeight() {
        chatEditTextLayout.post(() -> {
            space.getLayoutParams().height = chatEditTextLayout.getHeight();
            space.requestLayout();
            if (isChatDownL)
                chatSC.post(() -> chatSC.fullScroll(View.FOCUS_DOWN));
        });
    }


    private void initHandler() {
        boolean isPublicChat = getIntent().getBooleanExtra(Static.isPublicMsg, false);
        if (isPublicChat) {
            handler = new PublicHandler(this,
                    getIntent().getStringExtra(Static.UserId),
                    getIntent().getStringExtra(Static.UserName),
                    getIntent().getStringExtra(Static.UserType));

            if (getIntent().getStringExtra(Static.UserType).equals(UsersTable.hisEmpAdmin)) {
                ((TextView) findViewById(R.id.userName)).setText(getIntent().getStringExtra(Static.UserName));
                findViewById(R.id.userStatus).setVisibility(View.GONE);
            }
            setChatPublic();
        } else {

        }

        handler.setListener(new PublicHandler.ChatHandlerListener() {
            @Override
            public void onAddViews(View view, boolean setEDEmpty) {
                if (setEDEmpty) {
                    msgED.setText("");
                }
                isChatDownL = true;
                chatLL.addView(view);
                setReplayGone();
                setSpaceHeight();
            }

            @Override
            public View getTopView() {
                return (chatLL.getChildCount() > 1) ? chatLL.getChildAt(1) : null;
            }

            @Override
            public void onAddFromDB(View view, int index) {
                chatLL.addView(view, index);
            }

            @Override
            public void onEndAddFromDB(View topView) {
                if (topView != null) {
                    View view = chatLL.getChildAt(chatLL.indexOfChild(topView) - 1);
                    new Handler().post(() -> chatSC.scrollTo(0, view.getBottom()));
                    new Handler().post(() -> chatSC.smoothScrollTo(0, view.getBottom() - 200));
                } else {
                    setSpaceHeight();
                }
            }

            @Override
            public void onReplay(PublicMessages message, short bubbleType) {
                findViewById(R.id.editTextBackground).setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.chat_edittext_inreplay));

                if (bubbleType == MsgBubbleView.RECEIVER_BUBBLE)
                    ((TextView) findViewById(R.id.tool_howUser)).setText(message.getUserName());
                else
                    ((TextView) findViewById(R.id.tool_howUser)).setText(getString(R.string.You));

                ((TextView) findViewById(R.id.tool_replayText)).setText(message.getText().trim());
                setSpaceHeight();
                if (!KeyboardUtils.keyBoardIsVisible) {
                    KeyboardUtils.toggleKeyboardVisibility(ChatActivity.this);
                    msgED.requestFocus();
                    KeyboardUtils.keyBoardIsVisible = true;
                }
                handler.setReplayMsg(message);
                findViewById(R.id.replay_shower).setVisibility(View.VISIBLE);
            }

            @Override
            public void moveToReplayBubble(View view) {
                new Handler().post(() -> chatSC.smoothScrollTo(0, view.getTop() - 100));
            }

            @Override
            public void onDelete(View view) {
                chatLL.removeView(view);
                hideMenu();
            }

            @Override
            public void hideMenu() {
                findViewById(R.id.optionMenu).setVisibility(View.INVISIBLE);
                findViewById(R.id.infoMenu).setVisibility(View.VISIBLE);
            }
        });

        handler.initData();
    }

    private void setChatPublic() {
        mediaBtn.setVisibility(View.GONE);
        cameraBtn.setVisibility(View.GONE);
        recordBtn.setVisibility(View.GONE);
    }

    private void initEvent() {
        chatSC.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (chatSC.getScrollY() == 0) {
                handler.addFromDB();
            }
            isChatDownL = (chatSC.getScrollY() + chatSC.getHeight() - space.getHeight()) >= chatLL.getHeight() - 30;
        });

        msgED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setChatED_Visibility();
            }

            private void setChatED_Visibility() {
                if (msgED.getText().toString().trim().equals("")) {
                    sendBtn.setVisibility(View.GONE);
                    if (!handler.isPublic()) {
                        mediaBtn.setVisibility(View.VISIBLE);
                        cameraBtn.setVisibility(View.VISIBLE);
                        recordBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    sendBtn.setVisibility(View.VISIBLE);
                    mediaBtn.setVisibility(View.GONE);
                    cameraBtn.setVisibility(View.GONE);
                    recordBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(v -> {
            if (!PublicChatService.isRunning) {
                Intent chatIntent = new Intent(ChatActivity.this, PublicChatService.class);
                chatIntent.putExtra(Static.UserType, getIntent().getStringExtra(Static.UserType));
                startService(chatIntent);
            }

            handler.sendTextMessage(msgED.getText().toString());
        });

        cancleReplayBtn.setOnClickListener(v -> {
            setReplayGone();
            setSpaceHeight();
        });

        KeyboardUtils.addKeyboardToggleListener(this, isVisible -> {
            if (isVisible) {
                setSpaceHeight();
            }
            KeyboardUtils.keyBoardIsVisible = isVisible;
        });

        _f(R.id.menu_delete).setOnClickListener(v -> {
            handler.deleteBubbles();
        });

        _f(R.id.menu_copy).setOnClickListener(v -> {
            handler.copyBubble();
        });

        _f(R.id.menu_replay).setOnClickListener(v -> {
            handler.replayMessage();
        });
    }

    private View _f(int id) {
        return findViewById(id);
    }

    private void setReplayGone() {
        findViewById(R.id.editTextBackground).setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.chat_edittext));
        findViewById(R.id.replay_shower).setVisibility(View.GONE);
        handler.setReplayMsg(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.clear();

        unregisterReceiver(selectMsgBroadcast);
    }
}

