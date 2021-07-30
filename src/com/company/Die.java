package com.company;

public class Die {
    private final int numberOfSides;
    public int faceUpValue;

    public Die() {
        numberOfSides = 6;
    }

    public Die(int numberOfSides) {
        this.numberOfSides = numberOfSides;
    }

    public void roll(){
        faceUpValue = (int) ((Math.random() * numberOfSides) + 1);
    }

    @Override
    public String toString() {
        return "Die{" +
                "faceUpValue=" + faceUpValue +
                '}';
    }
}
