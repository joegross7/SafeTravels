package com.example.myapplication;

public class HighestIndex {
    private static int indexOfClosest;
    public HighestIndex(int indexOfClosest){
        this.indexOfClosest = indexOfClosest;
    }
    public static int getIndexOfClosest(){
        return indexOfClosest;
    }
}
