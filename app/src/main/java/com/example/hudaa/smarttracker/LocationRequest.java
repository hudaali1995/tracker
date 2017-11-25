package com.example.hudaa.smarttracker;

/**
 * Created by hudaa on 25/11/17.
 */

class LocationRequest {
    public Object setPriority;
    private int interval;
    private int fastestInterval;

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setFastestInterval(int fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    public void setPriority(int priorityHighAccuracy) {
    }

    public class PRIORITY_HIGH_ACCURACY {
        private int fastestInterval;
    }
}
