package com.ewu.moonx.UI.FollowUpPkj.ChatPkj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.KeyboardUtils;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers.ChatHandler;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers.PublicChatHandler;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers.UserChatHandler;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.PublicService.PublicChatService;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService.UserChatService;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ImageView mediaBtn, cameraBtn, recordBtn, record_space, sendBtn;
    ScrollView chatSC;
    boolean isChatDownL = true;
    EditText msgED;
    LinearLayout chatLL;
    ChatHandler handler;
    View cancleReplayBtn;
    View recordTimeL;

    View space, chatEditTextLayout;
    View optionView;
    View circleFlash;
    View recordCancleTitle;
    private BroadcastReceiver selectMsgBroadcast;
    private boolean meidaOptionShow = false;
    boolean itsRecordding = false, isInRecordMode = false;

    MediaRecorder recorder;
    private final int PROMISSION_CODE = 12;
    String recordMesId;
    private int recordTimeCount = 0;
    CountDownTimer countDownTimer;
    private String recordTimeStr;

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
        record_space = findViewById(R.id.record_space);
        sendBtn = findViewById(R.id.sendBtn);
        msgED = findViewById(R.id.msgED);
        chatLL = findViewById(R.id.chatLL);
        chatSC = findViewById(R.id.chatSC);
        cancleReplayBtn = findViewById(R.id.cancleReplayBtn);
        space = findViewById(R.id.space);
        chatEditTextLayout = findViewById(R.id.chatEditTextLayout);
        optionView = _f(R.id.optionView);
        optionView.setY(_f(R.id.mainChatRL).getHeight());
        recordTimeL = _f(R.id.recordTimeL);
        circleFlash = _f(R.id.circleFlash);
        recordCancleTitle = _f(R.id.recordCancleTitle);
        KeyboardUtils.keyBoardIsVisible = false;

        recorder = new MediaRecorder();
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
            handler = new PublicChatHandler(this,
                    getIntent().getStringExtra(Static.UserId),
                    getIntent().getStringExtra(Static.UserName),
                    getIntent().getStringExtra(Static.UserType));

            if (getIntent().getStringExtra(Static.UserType).equals(SettingTable.hisEmpAdmin)) {
                ((TextView) findViewById(R.id.userName)).setText(getIntent().getStringExtra(Static.UserName));
                findViewById(R.id.userStatus).setVisibility(View.GONE);
            }
            setChatPublic();
        } else {
            Users chatUser = ((Users) getIntent().getSerializableExtra(Static.UserInfo));
            handler = new UserChatHandler(this, chatUser);
            ((TextView) findViewById(R.id.userName)).setText(String.format("%s %s", chatUser.getFirstName(), chatUser.getThirdName()));
        }

        handler.setListener(new PublicChatHandler.ChatHandlerListener() {
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
            public void onReplay(Messages message, short bubbleType) {
                findViewById(R.id.editTextBackground).setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.chat_edittext_inreplay));

                if (bubbleType == MsgBubbleView.RECEIVER_BUBBLE)
                    ((TextView) findViewById(R.id.tool_howUser)).setText(getIntent().getStringExtra(Static.UserName));
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
            }

            @Override
            public void hideOptionMenu() {
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
        record_space.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                disappearEDOpinions();
            }

            private void setChatED_Visibility() {
                if (msgED.getText().toString().trim().equals("")) {
                    sendBtn.setVisibility(View.GONE);
                    if (!handler.isPublic()) {
                        mediaBtn.setVisibility(View.VISIBLE);
                        cameraBtn.setVisibility(View.VISIBLE);
                        record_space.setVisibility(View.VISIBLE);
                        recordBtn.clearAnimation();
                        recordBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    sendBtn.setVisibility(View.VISIBLE);
                    mediaBtn.setVisibility(View.GONE);
                    cameraBtn.setVisibility(View.GONE);
                    record_space.setVisibility(View.GONE);
                    recordBtn.clearAnimation();
                    recordBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(v -> {
            if (!PublicChatService.isRunning) {
                Intent publicChatIntent = new Intent(ChatActivity.this, PublicChatService.class);
                publicChatIntent.putExtra(Static.UserType, getIntent().getStringExtra(Static.UserType));
                startService(publicChatIntent);
            }

            if (!UserChatService.isRunning) {
                Intent userChatIntent = new Intent(ChatActivity.this, UserChatService.class);
                startService(userChatIntent);
            }

            disappearEDOpinions();
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
            new AlertDialog.Builder(ChatActivity.this)
                    .setTitle(getString(R.string.deleteMsgs_title))
                    .setMessage(getString(R.string.deleteMsgs_alert))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> handler.deleteBubbles())
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                    .create().show();
        });

        _f(R.id.menu_copy).setOnClickListener(v -> {
            handler.copyBubble();
        });

        _f(R.id.menu_replay).setOnClickListener(v -> {
            handler.replayMessage();
        });

        _f(R.id.menu_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.selectCancle();
            }
        });

        mediaBtn.setOnClickListener(v -> {
            if (!meidaOptionShow)
                showEDOptions();
            else
                disappearEDOpinions();
        });

        _f(R.id.foucsL).setOnClickListener(v -> disappearEDOpinions());
        _f(R.id.foucsL2).setOnClickListener(v -> disappearEDOpinions());

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    moveView(event);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showRecodeEffects();
                    if (!isInRecordMode) {
                        isInRecordMode = true;
                        startRecord();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    dispearRecodeEffects();
                    stopRecord();
                    isInRecordMode = false;
                }
                return false;
            }

            private void startRecord() {
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    recordTimeCount = 0;

                    countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                            recordTimeCount++;
                            long secound = recordTimeCount;
                            int minute = (int) (secound / 60);
                            int hour = minute / 60;
                            minute = minute % 60;
                            secound = secound % 60;
                            hour = hour % 60;

                            if (hour == 0)
                                recordTimeStr = String.format(Locale.ENGLISH, "%02d:%02d", minute, secound);
                            else
                                recordTimeStr = String.format(Locale.ENGLISH, "%02d:%02d%02d", hour, minute, secound);

                            ((TextView) _f(R.id.recordTime)).setText(recordTimeStr);
                        }

                        @Override
                        public void onFinish() {
                        }
                    };

                    countDownTimer.start();

                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recordMesId = ((UserChatHandler) handler).getMesId();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        itsRecordding = true;
                        recorder.setOutputFile(Static.getRecordFile(ChatActivity.this, recordMesId));
                    } else {
                        recorder.setOutputFile(Static.getRecordFile(ChatActivity.this, recordMesId).getPath());
                    }

                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        recorder.prepare();
                    } catch (IOException e) {
                        Log.e("ChatActivity", "startRecord: " + e.getMessage());
                    }
                    recorder.start();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PROMISSION_CODE);
                    }
                }
            }

            private void stopRecord() {
                if (itsRecordding) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    countDownTimer.cancel();
                    ((UserChatHandler) handler).sendRecord(recordMesId, recordTimeStr);
                }
            }

            private void initValue(MotionEvent event) {
            }

            private void moveView(MotionEvent event) {
            }

            private void showRecodeEffects() {
                recordBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoonin));
                circleFlash.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));
                recordBtn.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.round_btn_active));
                msgED.setVisibility(View.INVISIBLE);
                cameraBtn.setVisibility(View.GONE);
                mediaBtn.setVisibility(View.GONE);
                recordTimeL.animate().alpha(1).withStartAction(() -> recordTimeL.setVisibility(View.VISIBLE)).start();
                recordCancleTitle.animate().alpha(1).withStartAction(() -> recordCancleTitle.setVisibility(View.VISIBLE)).start();
            }

            private void dispearRecodeEffects() {
                recordBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoonout));
                recordBtn.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.round_btn_outclick));
                circleFlash.clearAnimation();
                msgED.setVisibility(View.VISIBLE);
                cameraBtn.setVisibility(View.VISIBLE);
                mediaBtn.setVisibility(View.VISIBLE);
                recordCancleTitle.animate().alpha(0).setDuration(100).withEndAction(() -> recordCancleTitle.setVisibility(View.VISIBLE)).start();
                recordTimeL.animate().alpha(0).setDuration(100).withEndAction(() -> {
                    recordTimeL.setVisibility(View.GONE);
                    msgED.setVisibility(View.VISIBLE);
                    msgED.requestFocus();
                }).start();
            }
        });

        _f(R.id.option_images).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), Static.RESULT_CODE1);
        });
    }


    private View _f(int id) {
        return findViewById(id);
    }

    private void disappearEDOpinions() {
        if (meidaOptionShow) {
            optionView.animate().alpha(0).translationY(_f(R.id.mainChatRL).getHeight())
                    .withEndAction(() -> {
                        optionView.setY(_f(R.id.mainChatRL).getHeight());
                        _f(R.id.foucsL).setVisibility(View.GONE);
                        _f(R.id.foucsL2).setVisibility(View.GONE);
                    }).start();
            meidaOptionShow = false;
        }

    }

    private void showEDOptions() {
        optionView.animate().alpha(1).translationY(0)
                .withStartAction(() -> optionView.setVisibility(View.VISIBLE))
                .withEndAction(() -> {
                    _f(R.id.foucsL).setVisibility(View.VISIBLE);
                    _f(R.id.foucsL2).setVisibility(View.VISIBLE);
                }).start();
        meidaOptionShow = true;
        optionView.requestFocus();

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

