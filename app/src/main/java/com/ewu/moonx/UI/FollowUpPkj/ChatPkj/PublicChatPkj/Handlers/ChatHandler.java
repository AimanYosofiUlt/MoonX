package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.View;
import android.widget.Toast;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.BubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.DateInfoBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.ReceiverBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.SenderBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_Content;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_TextContent;

import java.util.ArrayList;

public abstract class ChatHandler {
    BroadcastReceiver replayMessageBroadCast, innerReplayBroadcast;
    Activity con;
    Cursor chatCursor;
    Messages replayMsg;

    final ArrayList<BubbleView> orderingList;
    final ArrayList<SenderBubbleView> needSendedViews;

    ChatHandlerListener listener;

    public interface ChatHandlerListener {
        void onAddViews(View view, boolean setEDEmpty);

        View getTopView();

        void onAddFromDB(View view, int index);

        void onEndAddFromDB(View topView);

        void onReplay(Messages message, short bubbleType);

        void moveToReplayBubble(View view);

        void onDelete(View mainView);

        void hideOptionMenu();

    }

    public void setListener(ChatHandlerListener listener) {
        this.listener = listener;
    }

    public ChatHandler(Activity con) {
        this.con = con;
        orderingList = new ArrayList<>();
        needSendedViews = new ArrayList<>();
        initBroadcastReceivers();
    }

    protected void initBroadcastReceivers() {


        replayMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Messages message = (Messages) intent.getSerializableExtra(Static.Message);
                short bubbleType = intent.getShortExtra(Static.BubbleType, (short) 0);
                listener.onReplay(message, bubbleType);
            }
        };

        innerReplayBroadcast = new InnerReplayBroadcast(this);

        con.registerReceiver(replayMessageBroadCast, new IntentFilter(con.getString(R.string.replayMessagesBroadcast)));
        con.registerReceiver(innerReplayBroadcast, new IntentFilter(con.getString(R.string.innerReplayBroadcast)));
    }

    public abstract void initData();

    public void addFromDB() {
        final View topView = listener.getTopView();

        boolean isIntern = startAddFromDB();

        if (isIntern)
            listener.onEndAddFromDB(topView);
        else
            chatCursor.close();
    }

    protected boolean startAddFromDB() {
        boolean isIntern = false;
        int d = 0;
        while (chatCursor.moveToNext() && d < 20) {
            d++;
            isIntern = true;
            MsgBubbleView bubble = getBubbleFromChatCursor();
            if (bubble.getMessage().getReplayMsgId() != null)
                addMessageReplay(bubble);

            orderViewsAndDate(bubble);
            orderingList.add(1, bubble);
            listener.onAddFromDB(bubble.getMainView(), 1);
        }

        return isIntern;
    }

    protected abstract void addMessageReplay(MsgBubbleView bubble);

    protected MsgBubbleView getBubbleFromChatCursor() {
        MsgBubbleView bubble;
        if (itsReceiverMessages(chatCursor)) {
            bubble = new ReceiverBubbleView(con, getMessage());
            bubble.setContent(new BM_TextContent(con, bubble.getMessage().getText()));

            if (chatCursor.getString(5).equals(MessageTable.StUser_notRead)) {
                makeMessageAsReaded(bubble.getMessage());
            }

        } else {
            String statue = getStatueFromChatCursor();
            bubble = new SenderBubbleView(con, getMessage(), statue);
            bubble.setContent(new BM_TextContent(con, bubble.getMessage().getText()));

            if (statue.equals(MessageTable.ItsInProgress))
                needSendedViews.add(((SenderBubbleView) bubble));
        }
        return bubble;
    }

    protected abstract boolean itsReceiverMessages(Cursor chatCursor);

    protected abstract Messages getMessage();

    protected abstract void makeMessageAsReaded(Messages message);

    protected void orderViewsAndDate(MsgBubbleView bubble) {
        if (isFromDBNeedToAddDate(bubble)) {
            DateInfoBubbleView dateBabble = new DateInfoBubbleView(con, bubble.getMessage().getDate());
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
    }

    protected boolean isFromDBNeedToAddDate(MsgBubbleView bubble) {
        if (orderingList.isEmpty())
            return true;
        else
            return !orderingList.get(0).getDate().equals(bubble.getDate());
    }

    protected abstract String getStatueFromChatCursor();


    public void selectCancle() {
        for (MsgBubbleView bubbleView : MsgBubbleView.getSelectedBubble()) {
            bubbleView.setUnselected();
        }

        MsgBubbleView.getSelectedBubble().clear();
        listener.hideOptionMenu();
    }

    public void setReplayMsg(Messages replayMsg) {
        this.replayMsg = replayMsg;
    }


    public void copyBubble() {
        ClipData clip = ClipData.newPlainText(con.getString(R.string.app_name), MsgBubbleView.getSelectedBubble().get(0).getMessage().getText());
        ClipboardManager clipboard = (ClipboardManager) con.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clip);

        MsgBubbleView.getSelectedBubble().get(0).setUnselected();
        MsgBubbleView.getSelectedBubble().clear();
        listener.hideOptionMenu();

        Toast.makeText(con, con.getString(R.string.Message_copied), Toast.LENGTH_SHORT).show();
    }

    public void deleteBubbles() {
        for (MsgBubbleView bubbleView : MsgBubbleView.getSelectedBubble()) {
            int index = orderingList.indexOf(bubbleView);
            boolean itsPrevWithDate = setInfoLayoutVisibility_itsPrevWithDate(bubbleView, index);
            boolean hasNext = setTaleVisibility_hasNext(bubbleView, index);
            if (itsPrevWithDate && !hasNext) {
                BubbleView dateBubble = orderingList.get(index - 1);
                orderingList.remove(dateBubble);
                listener.onDelete(dateBubble.getMainView());
            }
            deleteFromDB(bubbleView.getMessage());
            orderingList.remove(bubbleView);
            listener.onDelete(bubbleView.getMainView());
        }
        MsgBubbleView.getSelectedBubble().clear();
    }

    protected abstract void deleteFromDB(Messages message);

    private boolean setTaleVisibility_hasNext(MsgBubbleView bubbleView, int index) {
        if (bubbleView.getTale().getVisibility() == View.VISIBLE) {
            if (bubbleView.getInfoLayout().getVisibility() != View.VISIBLE) {
                BubbleView nextBubble = orderingList.get(index + 1);
                if (nextBubble.getBubbleType() == bubbleView.getBubbleType()) {
                    ((MsgBubbleView) nextBubble).setTaleVisibility(true);
                    return true;
                } else return hasNext(index);
            } else return hasNext(index);
        } else return hasNext(index);
    }

    private boolean hasNext(int index) {
        return index != orderingList.size() - 1 && orderingList.get(index + 1).getBubbleType() != BubbleView.DATE_BUBBLE;
    }

    private boolean setInfoLayoutVisibility_itsPrevWithDate(MsgBubbleView bubbleView, int index) {
        BubbleView prevBubble = orderingList.get(index - 1);

        if (bubbleView.getInfoLayout().getVisibility() == View.VISIBLE) {
            if (bubbleView.getTale().getVisibility() != View.VISIBLE) {
                if (prevBubble.getBubbleType() == bubbleView.getBubbleType()) {
                    if (((MsgBubbleView) prevBubble).getTime().equals(bubbleView.getTime()))
                        ((MsgBubbleView) prevBubble).setInfoLayoutVisibility(true);

                    return false;
                } else
                    return prevBubble.getBubbleType() == BubbleView.DATE_BUBBLE;
            } else
                return prevBubble.getBubbleType() == BubbleView.DATE_BUBBLE;
        } else
            return prevBubble.getBubbleType() == BubbleView.DATE_BUBBLE;
    }


    public void replayMessage() {
        Intent intent = new Intent(con.getString(R.string.replayMessagesBroadcast));
        intent.putExtra(Static.Message, MsgBubbleView.getSelectedBubble().get(0).getMessage());
        intent.putExtra(Static.BubbleType, MsgBubbleView.getSelectedBubble().get(0).getBubbleType());
        con.sendBroadcast(intent);

        MsgBubbleView.getSelectedBubble().get(0).setUnselected();
        MsgBubbleView.getSelectedBubble().clear();
        listener.hideOptionMenu();
    }

    private boolean isNewNeedToAddDate(MsgBubbleView bubble) {
        if (orderingList.isEmpty())
            return true;
        else
            return !orderingList.get(orderingList.size() - 1).getDate().equals(bubble.getDate());
    }

    protected void makeSendBubbleDone(Messages message) {
        for (SenderBubbleView bubble : needSendedViews) {
            if (bubble.getMessage().getId().equals(message.getId())) {
                bubble.seStatue(MessageTable.ItsSent);
                needSendedViews.remove(bubble);
                break;
            }
        }
    }

    protected void addSendedMessages(SenderBubbleView bubble) {
        orderOnAddViews(bubble);
        listener.onAddViews(bubble.getMainView(), true);
        orderingList.add(bubble);
        needSendedViews.add(bubble);
    }

    protected void receiveBubble(Messages message) {
        ReceiverBubbleView bubble = new ReceiverBubbleView(con, message);
        bubble.setContent(getReceiveContent(message));

        if (bubble.getMessage().getReplayMsgId() != null)
            addMessageReplay(bubble);

        orderOnAddViews(bubble);
        listener.onAddViews(bubble.getMainView(), false);
        orderingList.add(bubble);
    }

    protected abstract BM_Content getReceiveContent(Messages message);

    protected void orderOnAddViews(MsgBubbleView bubble) {
        if (isNewNeedToAddDate(bubble)) {
            DateInfoBubbleView dateBabble = new DateInfoBubbleView(con, bubble.getMessage().getDate());
            listener.onAddViews(dateBabble.getMainView(), true);
            orderingList.add(dateBabble);
        } else
            mergeMessages(bubble);
    }

    protected void mergeMessages(MsgBubbleView bubble) {
        if (orderingList.get(orderingList.size() - 1).getBubbleType() == bubble.getBubbleType()) {
            MsgBubbleView temp = (MsgBubbleView) orderingList.get(orderingList.size() - 1);
            if (temp.getTime().equals(bubble.getTime())) {
                temp.setInfoLayoutVisibility(false);
                bubble.setTaleVisibility(false);
            }
        }
    }


    public abstract void sendTextMessage(String msgStr);

    public abstract boolean isPublic();

    public void clear() {
        con.unregisterReceiver(innerReplayBroadcast);
        con.unregisterReceiver(replayMessageBroadCast);

        MsgBubbleView.clear();
        if (chatCursor != null && !chatCursor.isClosed())
            chatCursor.close();
    }
}
