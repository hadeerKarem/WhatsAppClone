package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    String TAG = "UserListActivityLogTag";
    ListView usersListView;
    ArrayList<String> usersArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("Contacts");

        usersListView = findViewById(R.id.usersListView);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item " + position + " clicked!");

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("username", usersArrayList.get(position));
                startActivity(intent);
            }
        });

        usersArrayList.clear();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersArrayList);
        usersListView.setAdapter(arrayAdapter);

        //get a query of all users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Log.i(TAG, "looping through all users");
                        for (ParseUser user : objects) {
                            Log.i(TAG, "adding user: " + user.getUsername() + " to the arrayList");
                            usersArrayList.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}