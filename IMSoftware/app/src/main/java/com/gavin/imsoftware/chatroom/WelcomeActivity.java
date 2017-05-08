package com.gavin.imsoftware.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gavin.imsoftware.R;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }

    public void welcome_login(View v) {
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    public void welcome_exit(View view){
        this.finish();
        System.exit(0);
    }
}
