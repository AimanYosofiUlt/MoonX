package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.BubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;

public class InnerReplayBroadcast extends BroadcastReceiver {
    ChatHandler chatHandler;

    public InnerReplayBroadcast(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    public void onReceive(Context context, final Intent intent) {
        HandlerThread handlerThread = new HandlerThread("innerReplay");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            boolean isFounded = false;
            String msgId = intent.getStringExtra(Static.msgId);
            for (BubbleView bubbleView : chatHandler.orderingList) {
                if (bubbleView.getBubbleType() != BubbleView.DATE_BUBBLE) {
                    if (((MsgBubbleView) bubbleView).getMessage().getId().equals(msgId)) {
                        chatHandler.listener.moveToReplayBubble(bubbleView.getMainView());
                        chatHandler.con.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MsgBubbleView) bubbleView).startBlinkAnime();
                            }
                        });
                        isFounded = true;
                        break;
                    }
                }
            }

            if (!isFounded) {
                PublicMessagesTable table = new PublicMessagesTable(context);
                Cursor cursor = DB.select(table.statueCol).from(table).where(table.idCol, msgId).start();
                if (cursor.getCount() > 0) {
                    getReplayMessageFromDB(msgId);
                }
                cursor.close();
            }
        });
    }

    private void getReplayMessageFromDB(String msgId) {
        getReplayMessageFromDB(msgId, null, 0);
    }

    private void getReplayMessageFromDB(String msgId, MsgBubbleView currentBubble, int callCount) {

        int d = 0;
        boolean isIntern = false;
        while (chatHandler.chatCursor.moveToNext() && d < 5) {
            d++;
            isIntern = true;

            MsgBubbleView bubble = chatHandler.getBubbleFromChatCursor();

            chatHandler.orderingList.add(1, bubble);
            chatHandler.con.runOnUiThread(() -> chatHandler.listener.onAddFromDB(bubble.getMainView(), 1));

            if (bubble.getMessage().getId().equals(msgId)) {
                callCount = 1;
                currentBubble = bubble;
            }
        }


        if (callCount == 2 || !isIntern) {
            chatHandler.listener.moveToReplayBubble(currentBubble.getMainView());
            MsgBubbleView bubble = currentBubble;
            chatHandler.con.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bubble.startBlinkAnime();
                }
            });

            if (!isIntern)
                chatHandler.chatCursor.close();
            return;
        }

        if (callCount == 1)
            callCount = 2;

        getReplayMessageFromDB(msgId, currentBubble, callCount);
    }
}
