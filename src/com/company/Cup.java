package com.company;

import java.util.ArrayList;
import java.util.List;

public class Cup {
    public List<Die> dice = new ArrayList<>();

    public Cup() {
        while(dice.size() < 5){
            dice.add(new Die());
        }
    }

    public Cup(int amountOfDice) {
        for(int i = 0; i < amountOfDice; i++){
            dice.add(new Die());
        }
    }
    public Cup(List<Die> dice) {
        this.dice = dice;
    }
    public void  roll(){
        for(Die die : dice)
            die.roll();

    }
    public void displayDice(){
        String output = "";
        int[] array = new int[dice.size()];
        int counter = 0;
        for(Die die : dice) {
            array[counter] = die.faceUpValue;
            counter++;
        }
        Console.displayMessage(buildDiceVisual(array));

    }
     String buildDiceVisual(int[] values){
        String top = "┌───┐";
        String mid = "│ %s │";
        String bottom = "└───┘";
        String tempTop = "";
        String tempMid = "";
        String tempBottom = "";
        for(int i = 0; i < (values.length); i++){
            tempTop += top + " ";
            if(i ==0){
                tempMid = String.format(mid, values[i]) + " ";
            }else{
                tempMid += String.format(mid, values[i]) + " ";
            }
            tempBottom += bottom + " ";

        }
        return tempTop + "\n" + tempMid + "\n" + tempBottom;
    }
}
