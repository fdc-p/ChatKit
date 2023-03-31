package com.stfalcon.chatkit.sample.features.demo;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.DemoConfig;
import com.stfalcon.chatkit.sample.R;
import com.stfalcon.chatkit.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatkit.sample.common.data.model.Message;
import com.stfalcon.chatkit.sample.utils.AppUtils;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.chatkit.utils.ToolUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Created by troy379 on 04.04.17.
 */
public abstract class DemoMessagesActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener, MessagesListAdapter.OnMessageViewClickListener, DateFormatter.Formatter,
        MessagesListAdapter.OnLoadMoreListener {

    private static final String TAG = "DemoMessagesActivity";

    private static final int TOTAL_MESSAGES_COUNT = 100;

    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    private MessagesList messagesList;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);
    }

    protected void initAdapterStyleDesign() {
        messagesList = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(DemoMessagesActivity.this);
        messagesAdapter.setDateHeadersFormatter(DemoMessagesActivity.this);
        messagesAdapter.registerViewClickListener(R.id.messageCopy, this);
        messagesAdapter.registerViewClickListener(R.id.messageShare, this);
        messagesAdapter.registerViewClickListener(R.id.messageDelete, this);
        messagesList.setAdapter(messagesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (DemoConfig.ENABLE_LOG)
            Log.i(TAG, "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
//        menu.findItem(R.id.action_delete).setVisible(count > 0);
//        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    @Override
    public void onMessageViewClick(View view, IMessage message) {
        if (DemoConfig.ENABLE_LOG) {
            Log.i(TAG, "onMessageViewClick: message=" + message.toString());
            Log.i(TAG, "onMessageViewClick: view=" + view);
        }

        if (view.getId() == R.id.messageCopy) {
            String result = message.getText();
            ToolUtils.copyToClipboard(this, result);
            AppUtils.showToast(this, R.string.copied_message, false);
            return;
        }
        if (view.getId() == R.id.messageShare) {
            String result = message.getText();
            Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
            return;
        }
        if (view.getId() == R.id.messageDelete) {
            messagesAdapter.deleteById(message.getId());
            return;
        }
    }

    protected void loadGuide(String guide) {
        Message sendMessage = MessagesFixtures.getTextMessage(guide);
        sendMessage.setText(guide);
        sendMessage.setUser(MessagesFixtures.getAIUser());
        messagesAdapter.addToStart(sendMessage, true);
    }

    protected void loadMessages() {
        //imitation of internet connection
        //todo am_111 暂时不需要缓存以前的历史逻辑
//        new Handler().postDelayed(() -> {
//            ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
//            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//            messagesAdapter.addToEnd(messages, false);
//        }, 1000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return message -> {
            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.getCreatedAt());

            String text = message.getText();
            if (text == null) text = "[attachment]";

            return String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.getUser().getName(), text, createdAt);
        };
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return getString(R.string.date_header_today);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}
