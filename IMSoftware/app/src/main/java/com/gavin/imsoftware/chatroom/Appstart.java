package com.gavin.imsoftware.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.gavin.imsoftware.R;

public class Appstart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.appstart);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Appstart.this, WelcomeActivity.class);
                startActivity(intent);
                Appstart.this.finish();
            }
        }, 2000);
    }
}
