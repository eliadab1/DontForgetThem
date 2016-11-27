package com.example.eliad.dontforgetthem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {


    static Thread thread ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        MyTimer();

        }

    private void MyTimer() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i >= 0  ; i--) {
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                Intent baby = new Intent(MainActivity.this,BabyReminderActivity.class);
                baby.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(baby);
                MainActivity.this.finish();


                Log.d("onstop","onstop");
            }
        });

        thread.start();
    }




}

