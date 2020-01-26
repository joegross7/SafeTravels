package com.example.myapplication;

public class BlueLight {
    private int number;
    private double lat;
    private double longi;

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

    public double longi(){
        return longi;
    }
}
