package com.stfalcon.chatkit.sample.features.demo.styled;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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

    public static void open(Context context) {
        context.startActivity(new Intent(context, StyledMessagesActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled_messages);

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

        //这个是最先调用的 其他的后面一些
        initAdapterStyleDesign();
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Message sendMessage = MessagesFixtures.getTextMessage(input.toString());
        sendMessage.setText(input.toString());
        sendMessage.setUser(MessagesFixtures.getSendUser());
        messagesAdapter.addToStart(sendMessage
                , true);

        User aiUser = MessagesFixtures.getAIUser();
        Message aiMessage = new Message(aiUser.getId(), aiUser, "loading...");
        messagesAdapter.addToStart(aiMessage
                , true);

        //todo am_111
        GPTProxy.requestInThread("test", 1, input.toString(), new GPTProxy.IGTPCallback() {
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
        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
    }
}
