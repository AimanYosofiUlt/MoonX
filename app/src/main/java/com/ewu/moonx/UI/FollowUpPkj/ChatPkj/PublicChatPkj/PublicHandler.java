package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DBOrder;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.BubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.DateInfoBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.ReceiverBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.SenderBubbleView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class PublicHandler {
    Activity con;
    Cursor chatCursor;
    final ArrayList<BubbleView> orderingList;
    final ArrayList<SenderBubbleView> needSendedViews;
    String userId, userName;
    String userType;
    ChatHandlerListener listener;
    BroadcastReceiver sendDone_PublicMessageBroadCast, receiveMessageBroadCast, replayMessageBroadCast;
    PublicMessages replayMsg;
    private BroadcastReceiver innerReplayBroadcast;

    public void setReplayMsg(PublicMessages replayMsg) {
        this.replayMsg = replayMsg;
    }


    public PublicHandler(Activity con, String userId, String userName, String userType) {
        this.con = con;
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        orderingList = new ArrayList<>();
        needSendedViews = new ArrayList<>();
        initBroadcastReceivers();
    }

    private void initBroadcastReceivers() {

        replayMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessages messages = (PublicMessages) intent.getSerializableExtra(Static.PublicMsg);
                short bubbleType = intent.getShortExtra(Static.BubbleType, (short) 0);
                listener.onReplay(messages, bubbleType);
            }
        };

        sendDone_PublicMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessages message = (PublicMessages) intent.getSerializableExtra(Static.PublicMsg);

                for (SenderBubbleView bubble : needSendedViews) {
                    if (bubble.getPublicMessage().getId().equals(message.getId())) {
                        bubble.setPublicChatStatue(MessageTable.ItsSent);
                        needSendedViews.remove(bubble);
                        break;
                    }
                }
            }
        };

        receiveMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessages message = (PublicMessages) intent.getSerializableExtra(Static.PublicMsg);
                if (message.getUserId().equals(userId)) {
                    ReceiverBubbleView bubble = new ReceiverBubbleView(con, message);
                    orderOnAddViews(bubble);
                    listener.onAddViews(bubble.getMainView(), false);
                    orderingList.add(bubble);

                    PublicMessagesTable table = new PublicMessagesTable(context);
                    DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, message.getId()).exec();
                }
            }
        };

        innerReplayBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                HandlerThread handlerThread = new HandlerThread("innerReplay");
                handlerThread.start();

                Handler handler = new Handler(handlerThread.getLooper());
                handler.post(() -> {
                    boolean isFounded = false;
                    String msgId = intent.getStringExtra(Static.msgId);
                    for (BubbleView bubbleView : orderingList) {
                        if (bubbleView.getBubbleType() != BubbleView.DATE_BUBBLE) {
                            if (((MsgBubbleView) bubbleView).getPublicMessage().getId().equals(msgId)) {
                                listener.moveToReplayBubble(bubbleView.getMainView());
                                con.runOnUiThread(new Runnable() {
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
                while (chatCursor.moveToNext() && d < 5) {
                    d++;
                    isIntern = true;

                    MsgBubbleView bubble = getBubbleFromChatCursor();

                    orderingList.add(1, bubble);
                    con.runOnUiThread(() -> listener.onAddFromDB(bubble.getMainView(), 1));

                    if (bubble.getPublicMessage().getId().equals(msgId)) {
                        callCount = 1;
                        currentBubble = bubble;
                    }
                }


                if (callCount == 2 || !isIntern) {
                    listener.moveToReplayBubble(currentBubble.getMainView());
                    MsgBubbleView bubble = currentBubble;
                    con.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bubble.startBlinkAnime();
                        }
                    });

                    if (!isIntern)
                        chatCursor.close();
                    return;
                }

                if (callCount == 1)
                    callCount = 2;

                getReplayMessageFromDB(msgId, currentBubble, callCount);
            }
        };


        con.registerReceiver(innerReplayBroadcast, new IntentFilter(con.getString(R.string.innerReplayBroadcast)));
        con.registerReceiver(replayMessageBroadCast, new IntentFilter(con.getString(R.string.replay_messagesBroadcast)));
        con.registerReceiver(sendDone_PublicMessageBroadCast, new IntentFilter(con.getString(R.string.sendDone_PublicMessageBroadCast)));
        con.registerReceiver(receiveMessageBroadCast, new IntentFilter(con.getString(R.string.receiveMessageBroadCast)));
    }

    public void deleteBubbles() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        for (MsgBubbleView bubbleView : MsgBubbleView.getSelectedBubble()) {
            DB.delete(table).where(table.idCol, bubbleView.getPublicMessage().getId()).exec();
            listener.onDelete(bubbleView.getMainView());

            int index = orderingList.indexOf(bubbleView);
            if (orderingList.indexOf(bubbleView) != orderingList.size() - 1) {
                BubbleView nextBubble = orderingList.get(index + 1);
                if (nextBubble.getBubbleType() != BubbleView.DATE_BUBBLE) {
                    ((MsgBubbleView) nextBubble).setTaleVisibility(false);
                }
            }
        }
        MsgBubbleView.getSelectedBubble().clear();
    }

    public void copyBubble() {
        ClipData clip = ClipData.newPlainText(con.getString(R.string.app_name), MsgBubbleView.getSelectedBubble().get(0).getPublicMessage().getText());
        ClipboardManager clipboard = (ClipboardManager) con.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clip);

        MsgBubbleView.getSelectedBubble().get(0).setUnselected();
        MsgBubbleView.getSelectedBubble().clear();
        listener.hideMenu();

        Toast.makeText(con, con.getString(R.string.Message_copied), Toast.LENGTH_SHORT).show();
    }

    public void replayMessage() {
        Intent intent = new Intent(con.getString(R.string.replay_messagesBroadcast));
        intent.putExtra(Static.PublicMsg, MsgBubbleView.getSelectedBubble().get(0).getPublicMessage());
        intent.putExtra(Static.BubbleType, MsgBubbleView.getSelectedBubble().get(0).getBubbleType());
        con.sendBroadcast(intent);

        MsgBubbleView.getSelectedBubble().get(0).setUnselected();
        MsgBubbleView.getSelectedBubble().clear();
        listener.hideMenu();
    }

    public interface ChatHandlerListener {
        void onAddViews(View view, boolean setEDEmpty);

        View getTopView();

        void onAddFromDB(View view, int index);

        void onEndAddFromDB(View topView);

        void onReplay(PublicMessages messages, short bubbleType);

        void moveToReplayBubble(View view);

        void onDelete(View mainView);

        void hideMenu();
    }

    public void setListener(ChatHandlerListener listener) {
        this.listener = listener;
    }

    public boolean isPublic() {
        return true;
    }

    public void initData() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        chatCursor = DB.selectAll().from(table).where(table.userIdCol, userId).orderBy(table.dateCol, DBOrder.DESC).start();
        addFromDB();
    }

    public void addFromDB() {

        final View topView = listener.getTopView();

        boolean isIntern = startAddFromDB();

        if (isIntern)
            listener.onEndAddFromDB(topView);
        else
            chatCursor.close();
    }

    private boolean startAddFromDB() {
        boolean isIntern = false;
        int d = 0;
        while (chatCursor.moveToNext() && d < 20) {
            d++;
            isIntern = true;
            MsgBubbleView bubble = getBubbleFromChatCursor();

            orderingList.add(1, bubble);
            listener.onAddFromDB(bubble.getMainView(), 1);
        }

        return isIntern;
    }

    private MsgBubbleView getBubbleFromChatCursor() {
        MsgBubbleView bubble;
        if (chatCursor.getString(5).equals(MessageTable.StUser_Read)) {
            bubble = new ReceiverBubbleView(con, getPublicMessage());

        } else if (chatCursor.getString(5).equals(MessageTable.StUser_notRead)) {
            bubble = new ReceiverBubbleView(con, getPublicMessage());

            PublicMessagesTable table = new PublicMessagesTable(con);
            DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, bubble.getPublicMessage().getId()).exec();

        } else {
            String statue = chatCursor.getString(5);
            bubble = new SenderBubbleView(con, getPublicMessage(), statue);

            if (statue.equals(MessageTable.ItsInProgress))
                needSendedViews.add(((SenderBubbleView) bubble));
        }

        if (isFromDBNeedToAddDate(bubble)) {
            DateInfoBubbleView dateBabble = new DateInfoBubbleView(con, bubble.getPublicMessage().getDate());
            con.runOnUiThread(() -> listener.onAddFromDB(dateBabble.getMainView(), 0));
            orderingList.add(0, dateBabble);
        } else {
            MsgBubbleView temp = ((MsgBubbleView) orderingList.get(1));
            if (temp.getBubbleType() == bubble.getBubbleType()
                    && temp.getTime().equals(bubble.getTime())) {
                bubble.setInfoLayoutVisibility(false);
                temp.setTaleVisibility(false);
            }
        }
        return bubble;
    }

    private boolean isFromDBNeedToAddDate(MsgBubbleView bubble) {
        if (orderingList.isEmpty())
            return true;
        else
            return !orderingList.get(0).getDate().equals(bubble.getDate());
    }

    private PublicMessages getPublicMessage() {
        PublicMessages message = new PublicMessages();
        message.setId(chatCursor.getString(0));
        message.setUserId(chatCursor.getString(1));
        message.setUserName(chatCursor.getString(2));
        message.setText(chatCursor.getString(3));
        message.setReplayMsgId(chatCursor.getString(6));
        try {
            message.setDate(Static.getDate(chatCursor.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void sendTextMessage(String msgStr) {
        String mesId = Static.getSendPublicRef(userType, userId).push().getKey();

        PublicMessages message = new PublicMessages(mesId, userId, userName, msgStr, Calendar.getInstance().getTime(), null);
        if (replayMsg != null) {
            message.setReplayMsgId(replayMsg.getId());
            Toast.makeText(con, "" + replayMsg.getId(), Toast.LENGTH_SHORT).show();
        }

        SenderBubbleView bubble = new SenderBubbleView(con, message, MessageTable.ItsInProgress);
        orderOnAddViews(bubble);
        listener.onAddViews(bubble.getMainView(), true);
        orderingList.add(bubble);

        needSendedViews.add(bubble);
        Firebase.addSenderPublicMessageToDB(con, message);
        sendMessageBroadcast(message);
    }

    private void orderOnAddViews(MsgBubbleView bubble) {
        if (isNewNeedToAddDate(bubble)) {
            DateInfoBubbleView dateBabble = new DateInfoBubbleView(con, bubble.getPublicMessage().getDate());
            listener.onAddViews(dateBabble.getMainView(), true);
            orderingList.add(dateBabble);
        } else
            mergeMessages(bubble);
    }

    private void mergeMessages(MsgBubbleView bubble) {
        if (orderingList.get(orderingList.size() - 1).getBubbleType() == bubble.getBubbleType()) {
            MsgBubbleView temp = (MsgBubbleView) orderingList.get(orderingList.size() - 1);
            if (temp.getTime().equals(bubble.getTime())) {
                temp.setInfoLayoutVisibility(false);
                bubble.setTaleVisibility(false);
            }
        }
    }

    private boolean isNewNeedToAddDate(MsgBubbleView bubble) {
        if (orderingList.isEmpty())
            return true;
        else
            return !orderingList.get(orderingList.size() - 1).getDate().equals(bubble.getDate());
    }

    private void sendMessageBroadcast(PublicMessages message) {
        Intent intent = new Intent(con.getString(R.string.send_PublicMessageBroadCast));
        intent.putExtra(Static.PublicMsg, message);
        con.sendBroadcast(intent);
    }


    public void clear() {
        con.unregisterReceiver(sendDone_PublicMessageBroadCast);
        con.unregisterReceiver(receiveMessageBroadCast);
        con.unregisterReceiver(innerReplayBroadcast);

        MsgBubbleView.clear();
        if (chatCursor != null && !chatCursor.isClosed())
            chatCursor.close();
    }
}
