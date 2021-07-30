package com.company;

public class Bid {
    public int faceUpValue;
    public int frequency;

    public Bid(int faceUpValue, int frequency) {
        this.faceUpValue = faceUpValue;
        this.frequency = frequency;
    }

    public void displayBid(){
        System.out.println(String.format("die value: %s | frequency: %s", faceUpValue, frequency));
    }
}
