package com.example.myapplication;

public class BlueLight {
    public int number;
    public double lat;
    public double longi;

    public BlueLight(int number,double lat,double longi){
        this.number = number;
        this.lat = lat;
        this.longi = longi;
    }

    public int getNumber(){
        return number;
    }
    public double getLat(){
        return lat;
    }

    public double getLongi(){
        return longi;
    }
}
