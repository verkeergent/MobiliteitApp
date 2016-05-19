package com.stadgent.mobiliteitapp.model;

/**
 * Created by floriangoeteyn on 18-Feb-16.
 */
public class Location {

    private double x=0, y=0;
    private double longitude=0, latitude =0;

    public Location(double x, double y){
        this.x=x;
        this.y=y;
    }

    public double getX() {
        return x+ latitude;
    }

    public double getY() {
        return y+longitude;
    }

    @Override
    public String toString() {
        return "x: "+(x+longitude)+"y: "+(y+ latitude);
    }
}
