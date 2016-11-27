package com.example.eliad.dontforgetthem;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Random;

public class BabyReminderActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    CircularProgressBar circularProgressBar,circularProgressBar2;
    BroadcastReceiver broadcast = new Receiver();
    Intent mute , childInCar,returnBaby;
    NotificationCompat.Builder nBuilder;
    static NotificationManager mNotifyMgr;
    RemoteViews remoteViews;
    TextView tv,userSpeed;
    static TextView timerText;
    long[] vibe = {500,1000};
    static Thread thread ,thread2,thread3;
    String timer,str,headline,massage;
    static int overSpeed = 3,  mNotificationId = new Random().nextInt();
    boolean twice = false;
    boolean isGPSEnabled;
    static boolean isSpeedHigh = true,flag = true;
    static boolean checkMyself = true,cm = true;
    LocationManager manager;
    LocationRequest mLocationRequest;
    Uri alarmSound;
    GoogleApiClient mGoogleApiClient;
    static int mySpeed1;
    public static final int ONE_MINUTE = 1000 * 60;
    public static final int FIVE_MINUTES = ONE_MINUTE * 5;
    public static final int TEN_MINUTES = FIVE_MINUTES * 2;
    static CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.baby_remainder_counter);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        circularProgressBar = (CircularProgressBar) findViewById(R.id.mycircular);
        int animationDuration = 1000; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(65, animationDuration);
        tv = (TextView) findViewById(R.id.textView);
        userSpeed = (TextView) findViewById(R.id.kh);
        timerText = (TextView) findViewById(R.id.timertext);

        isMyGpsOn();
    }

    private void isMyGpsOn() {
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {


        }
        if (!isGPSEnabled) {
            Toast.makeText(this,R.string.IsGpsEnable,Toast.LENGTH_LONG).show();

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }



    private void onUpdate(MySpeed location) {
        float speed = 2f;
        mySpeed1 = Math.round(speed);

        if (location != null) {
            speed = location.getSpeed();

            speed = (float) (speed * 3.6);
            mySpeed1 = Math.round(speed);
            str = String.valueOf(mySpeed1);
            userSpeed.setText(str);


        }
        if (mySpeed1 >= overSpeed && isSpeedHigh) {
            Toast.makeText(this,"speed",Toast.LENGTH_SHORT).show();
            sendNotification();
            isSpeedHigh = false;



        }
    }

    private void sendNotification() {
        String CATEGORY_MESSAGE = "msg";
        int VISIBILITY_PUBLIC = 1;
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        headline = getResources().getString(R.string.remoteview_notification_headline);
        massage = getResources().getString(R.string.babyText);
        nBuilder = new NotificationCompat.Builder(this);
        nBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(VISIBILITY_PUBLIC)
                .setSound(alarmSound)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setOngoing(false)
                .setCategory(CATEGORY_MESSAGE)
                .setContentTitle(headline)
                .setVibrate(vibe)
                .setContentText(massage);
        returnBaby = new Intent(this, BabyReminderActivity.class);
        mute = new Intent();
        mute.setAction("MUTE");
        childInCar = new Intent();
        childInCar.setAction("CHILD");
        PendingIntent muteIntent = PendingIntent.getBroadcast(this, 0, mute, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent childCar = PendingIntent.getBroadcast(this, 0, childInCar, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent compatIntent = PendingIntent.getActivity(this, 0, returnBaby, 0);
        nBuilder.setContentIntent(compatIntent);
        nBuilder.addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "MUTE", muteIntent));
        nBuilder.addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "YES", childCar));
        Notification notification = nBuilder.build();
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, notification);
        NotificationManagerCompat.from(this);


        }



    public static class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MUTE")) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < ONE_MINUTE; i--) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                        isSpeedHigh = true;
                    }
                });
                thread.start();
            }

            if (intent.getAction().equals("CHILD")) {
                Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                checkSpeed();
//                mNotifyMgr.cancel(mNotificationId);
            }
        }

        private void checkSpeed() {

            thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Log.d("thread","threadStarted");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            while (checkMyself) {
                                try {
                                    if (mySpeed1 == 0) {
                                        countDownTimer =  new CountDownTimer(500,1000){
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                timerText.setText(Integer.toString((int) (millisUntilFinished/1000)));
                                            }

                                            @Override
                                            public void onFinish() {
                                                Log.d("onTick","finish");
                                            }
                                        }.start();
                                    }else if ( mySpeed1 > 0) {
                                        countDownTimer.cancel();
                                    }else if(flag && countDownTimer.equals("05:00")){
                                        flag = false;
                                        break;
                                    }
                                    thread2.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }


                        }
                    });
                }
            });
            thread2.start();
        }
    }

    private void myStrings() {
        timer = getResources().getString(R.string.timer);
        tv.setText(timer);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }
    
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        MySpeed myLocation = new MySpeed(location);
        this.onUpdate(myLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        Toast.makeText(this,"Check if you location is on ",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {

        if (twice == true) {
            Intent main = new Intent(Intent.ACTION_MAIN);
            main.addCategory(Intent.CATEGORY_HOME);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finish();
            System.exit(0);
        }

        twice = true;
        Toast.makeText(this, R.string.getOut, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        },3000);

    }


    @Override
    protected void onDestroy() {
        super.onPause();
        onResume();

        Log.d("onDestroy()","onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onpause()","onpause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onstart()","onstart");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onstop()","onstop");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



}