package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Console {
    private static final int CLEARER = 25;
    private static final Scanner scanner = new Scanner(System.in);

    public static int getNumberInput(String message) {
        displayMessage(message);
        return scanner.nextInt();
    }
    public static String getStringInput(String message) {
        displayMessage(message);
        return scanner.nextLine();
    }
    public static void nextLine(){
        scanner.nextLine();
    }
    public static void displayMessage(String message) {
        System.out.print(message);
    }

    public static int[] parseBid(String result) {
        int[] arrayInt = new  int[2];
        String[] array = result.split(" x");
        for (int i = 0 ; i < array.length; i++) {
            arrayInt[i] = Integer.parseInt(array[i]);
        }
        return arrayInt;
    }

    public static void clearBoard(){
        for(int i=0; i < CLEARER; i++)
            System.out.println();


    }
}
