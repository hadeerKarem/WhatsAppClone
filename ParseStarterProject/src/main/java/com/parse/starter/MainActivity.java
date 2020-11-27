/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  String TAG = "MainActivityLogTag";
  EditText editTextUserName;
  EditText editTextPassword;
  Button buttonAuth;
  TextView textViewToggleMode;
  Boolean loginModeActive = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("WhatsApp Login/SignUp");

    Log.i(TAG, "started");

    editTextUserName = findViewById(R.id.editTextUserName);
    editTextPassword = findViewById(R.id.editTextPassword);
    buttonAuth = findViewById(R.id.buttonAuth);
    textViewToggleMode = findViewById(R.id.textViewToggleMode);

    redirectIfLoggedIn();

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  public void signUpLogin(View view) {
    if (loginModeActive) {
      ParseUser.logInInBackground(editTextUserName.getText().toString(),
              editTextPassword.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                  if (e == null) {
                    Log.i(TAG, "User: '"+ user.getUsername()  + "' is Logged in");
                    redirectIfLoggedIn();

                  } else {
                    String message = e.getMessage();
                    if (message.toLowerCase().contains("java")) {
                      Toast.makeText(MainActivity.this,
                              e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    } else {
                      Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                  }
                }
              });

    } else {
      ParseUser user = new ParseUser();
      user.setUsername(editTextUserName.getText().toString());
      user.setPassword(editTextPassword.getText().toString());
      user.signUpInBackground(new SignUpCallback() {
        @Override
        public void done(ParseException e) {
          if (e == null) {
            Log.i(TAG, "user signed Up");
            redirectIfLoggedIn();

          } else {
            String message = e.getMessage();
            if (message.toLowerCase().contains("java")) {
              Toast.makeText(MainActivity.this,
                      e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
          }
        }
      });
    }
  }

  public void toggleLoginMode(View view) {
    Log.i(TAG, "Mode Toggled");
    if (loginModeActive) {
      loginModeActive = false;
      buttonAuth.setText("Sign Up");
      textViewToggleMode.setText("Or, Login");

    } else {
      loginModeActive = true;
      buttonAuth.setText("Login");
      textViewToggleMode.setText("Or, Sign Up");
    }
  }

  public void redirectIfLoggedIn() {
    if (ParseUser.getCurrentUser() != null) {
      Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
      startActivity(intent);
    }
  }
}