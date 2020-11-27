package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String TAG = "ChatActivityLogTag";
    String activeUser = "";
    EditText editTextChat;
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView chatListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextChat = findViewById(R.id.editTextChat);

        Intent intent = getIntent();
        activeUser = intent.getStringExtra("username");

        setTitle("Chat with " + activeUser);

        chatListView = findViewById(R.id.listViewChat);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, messages);
        chatListView.setAdapter(arrayAdapter);

        //set a query for sent messages
        ParseQuery<ParseObject> sentMessagesQuery = new ParseQuery<>("Message");
        sentMessagesQuery.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        sentMessagesQuery.whereEqualTo("recipient", activeUser);

        //set a
        ParseQuery<ParseObject> receivedMessagesQuery = new ParseQuery<>("Message");
        receivedMessagesQuery.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        receivedMessagesQuery.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> messagesQueries = new ArrayList<>();
        messagesQueries.add(sentMessagesQuery);
        messagesQueries.add(receivedMessagesQuery);

        ParseQuery<ParseObject> query = ParseQuery.or(messagesQueries);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        messages.clear();

                        for (ParseObject message : objects) {
                            String messageContent = message.getString("content");

                            if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                messageContent = "> " + messageContent;
                            }
                            messages.add(messageContent);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    public void sendMessage(View view) {
        //create a database class for messages and fill its attributes

        ParseObject message = new ParseObject("Message");
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("content", editTextChat.getText().toString());

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    messages.add(editTextChat.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    editTextChat.setText("");
                }
            }
        });
    }
}