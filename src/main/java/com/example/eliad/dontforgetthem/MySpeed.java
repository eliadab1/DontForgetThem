package com.example.eliad.dontforgetthem;

import android.location.Location;

/**
 * Created by eliad on 03/07/2016.
 */
public class MySpeed  extends Location {

    static boolean change = false;


    public MySpeed(Location l) {
        super(l);
    }

    public float getSpeed()
    {
        float speed = super.getSpeed();
        return speed;
    }
}
