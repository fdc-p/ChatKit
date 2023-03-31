package com.stfalcon.chatkit.sample.features.demo.styled;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.stfalcon.chatkit.ChatConfig;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.R;
import com.stfalcon.chatkit.sample.common.data.fixtures.MessagesFixtures;
import com.stfalcon.chatkit.sample.common.data.model.Message;
import com.stfalcon.chatkit.sample.common.data.model.User;
import com.stfalcon.chatkit.sample.features.demo.DemoMessagesActivity;
import com.stfalcon.chatkit.sample.features.demo.def.DefaultMessagesActivity;
import com.stfalcon.chatkit.sample.utils.AppUtils;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.tiptop.ai.GPTProxy;

import java.util.Date;

public class StyledMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    public static void open(Context context, String aiGuide, String userText, String token, int versionCode) {
        Intent intent = new Intent(context, StyledMessagesActivity.class);
        intent.putExtra("guide", aiGuide);
        intent.putExtra("text", userText);
        intent.putExtra("token", token);
        intent.putExtra("version", versionCode);
        context.startActivity(intent);
    }

    private MessageInput mInputView = null;
    private String mToken = "";
    private int mVersion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled_messages);

        mInputView = findViewById(R.id.input);
        mInputView.setInputListener(this);
        mInputView.setAttachmentsListener(this);

        //这个是最先调用的 其他的后面一些
        initAdapterStyleDesign();

        Intent intent = getIntent();
        String aiGuide = intent.getStringExtra("guide");
        String userText = intent.getStringExtra("text");
        mToken = intent.getStringExtra("token");
        mVersion = intent.getIntExtra("version", 1);

        if (!TextUtils.isEmpty(aiGuide)) {
            loadGuide(aiGuide);
        }

        mInputView.messageInput.requestFocus();
        if (!TextUtils.isEmpty(userText)) {
            mInputView.messageInput.setText(userText);
        }
    }

    @Override
    public boolean onSubmit(CharSequence text) {
        Message sendMessage = MessagesFixtures.getTextMessage(text.toString());
        sendMessage.setText(text.toString());
        sendMessage.setUser(MessagesFixtures.getSendUser());
        messagesAdapter.addToStart(sendMessage
                , true);

        User aiUser = MessagesFixtures.getAIUser();
        Message aiMessage = new Message(aiUser.getId(), aiUser, "loading...");
        messagesAdapter.addToStart(aiMessage
                , true);

        GPTProxy.requestInThread(mToken, mVersion, text.toString(), new GPTProxy.IGTPCallback() {
            @Override
            public void onReceiveMsg(String msg) {
                aiMessage.setText(msg);
                messagesAdapter.update(aiMessage);
            }
        });
        return true;
    }

    @Override
    public void onAddAttachments() {
        if (ChatConfig.ENABLE_LOG)
            Log.i(ChatConfig.TAG, "onAddAttachments: ....");
    }
}
