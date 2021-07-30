package com.company;

import java.util.*;

public class LiarsDice {
    private List<Player> playerList = new ArrayList<>();
    private LinkedList<Player> turnOrder = null;
    private Bid currentBid = new Bid(0, 0);
    private Player playerWithCurrentBid;
    private Map<Integer, Integer> freq = new HashMap<>();

    public LiarsDice() {
        setup();
    }

    private void setup() {
        int numberOfPlayers;
        int amountOfDice;
        int MAX_PLAYERS = 10;
        int MIN_PLAYERS = 2;
        do {
            numberOfPlayers = Console.getNumberInput("How many players are playing? ");
            Console.nextLine();
        } while (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS);

        do {
            amountOfDice = Console.getNumberInput("\nHow many dice per player ?");
            Console.nextLine();
            if (amountOfDice > 0 && amountOfDice <= 20) {
                break;
            }
            Console.displayMessage("\nEnter a number between 1 and 20");
        } while (true);

        for (int i = 0; i < numberOfPlayers; i++) {
            String name = Console.getStringInput("\nPlayer" + (i + 1) + " name: ");
            playerList.add(new Player(name, new Cup(amountOfDice)));
        }
        determineOrder();
        showTurnOrder();
    }//25

    private void reOrderTurnList(Player firstPlayer) {
        turnOrder.remove(firstPlayer);
        turnOrder.addFirst(firstPlayer);
    }//4

    private void setTurnOrder(LinkedHashMap<Player, Integer> map) {
        if (turnOrder == null) turnOrder = new LinkedList<>();
        for (Map.Entry<Player, Integer> entry : map.entrySet()) {
            turnOrder.add(entry.getKey());
        }
    }//5

    public void runGame() {
        while (playerList.size() > 1) {
            runRound();
        }
        Console.displayMessage(String.format("%s, was the last player standing, They win the game !", playerList.get(0).name));
    }//5

    private void runRound() {
        boolean isRoundDone = false;
        int counter = 0;
        rollAllDice();
        doFrequency();
        while (true) {
            for (int i = 0; i < turnOrder.size(); i++) {
                Console.displayMessage(String.format("%nIt is now %s's turn !%n", turnOrder.get(i).name));
                if (currentBid.faceUpValue > 0) {
                    showCurrentBid();
                }
                Console.displayMessage("\nHere is/are your die/dice:\n");
                turnOrder.get(i).cup.displayDice();
                Console.displayMessage("\n\n");
                showRemainingPlayers();
                showDiceCount();
                if (counter == 0) {
                    isRoundDone = runTurn(turnOrder.get(i), true);
                } else {
                    isRoundDone = runTurn(turnOrder.get(i), false);
                }
                Console.clearBoard();
                if (isRoundDone) break;
                if ((i + 1) == turnOrder.size()) i = 0;
                counter++;
            }
            if (isRoundDone) break;
        }
        resetBid();
    }//29

    private boolean runTurn(Player currentPlayer, boolean isFirstPlayer) {
        String result;
        if (isFirstPlayer) {
            makeBid(currentPlayer);
            return false;
        }
        do {
            result = Console.getStringInput("Would you like to make a bid (bid) or call the previous players bluff (call)?");
            if (result.equals("bid")) {
                makeBid(currentPlayer);
                return false;
            }
            if (result.equals("call")) {
                callLiar(currentPlayer);
                return true;
            }
        } while (true);
    }//17

    private void doFrequency() {
        Map<Integer, Integer> freq = new HashMap<>();
        for (Player player : playerList) {
            for (Die die : player.cup.dice) {
                if (!freq.containsKey(die.faceUpValue)) {
                    freq.put(die.faceUpValue, 0);
                }
                freq.put(die.faceUpValue, freq.get(die.faceUpValue) + 1);
            }
        }
        this.freq = freq;
    }//11

    private void determineOrder() {
        LinkedHashMap<Player, Integer> map = new LinkedHashMap<>();
        int max = 0;
        Die die = new Die();

        for (Player player : playerList) {
            Console.displayMessage(String.format("%n%s, please hit enter to roll your die.", player.name));
            Console.nextLine();
            die.roll();
            int value = die.faceUpValue;
            Console.displayMessage(String.format("%nYou rolled a %s ! %n", value));
            map.put(player, value);
            if (max < value) {
                max = value;
            }
        }
        if (reRollNeeded(max, map)) {
            reRoll(map, max);
        } else {
            map = sortByValue(map);
            turnOrder = new LinkedList<>(map.keySet());
        }
    }//22

    private void makeBid(Player currentPlayer) {
        do {
            String result = Console.getStringInput("Please enter the die face up value and frequency of the bid (Ex. 2 x3 or 6 x5): ");
            boolean isValid = isValidBid(result);
            if (isValid) {
                int[] arrayBid = Console.parseBid(result);
                currentBid.faceUpValue = arrayBid[0];
                currentBid.frequency = arrayBid[1];
                playerWithCurrentBid = currentPlayer;
                return;
            } else {
                Console.displayMessage("\nFormat expected (1 x4) ((Die) x(Frequency))\n\n");
            }
        } while (true);
    }//14

    private void callLiar(Player currentPlayer) {
        int frequency;
        if (freq.containsKey(currentBid.faceUpValue)) {
            frequency = freq.get(currentBid.faceUpValue);
        } else {
            removeDieCurrentBid(currentPlayer);
            return;
        }
        if (currentBid.frequency > frequency) {
            removeDieCurrentBid(currentPlayer);
        } else {
            if (!currentPlayer.canRemoveDie()) {
                playerList.remove(currentPlayer);
                turnOrder.remove(currentPlayer);
                Console.displayMessage(String.format("%n---- %s called %s's bluff and was wrong ! %s was removed from the game ----", currentPlayer.name, playerWithCurrentBid.name, currentPlayer.name));
            } else {
                currentPlayer.removeDie();
                reOrderTurnList(currentPlayer);
                showTurnOrder();
                Console.displayMessage(String.format("%n---- %s called %s's bluff and was wrong ! %s loses a die ----", currentPlayer.name, playerWithCurrentBid.name, currentPlayer.name));
            }
            Console.displayMessage(String.format("%nThe last bid was die: %s | frequency %s",
                    currentBid.faceUpValue, currentBid.frequency));
        }
    }//24

    private void removeDieCurrentBid(Player currentPlayer) {
        if (!playerWithCurrentBid.canRemoveDie()) {
            playerList.remove(playerWithCurrentBid);
            turnOrder.remove(playerWithCurrentBid);
            Console.displayMessage(String.format("%n---- %s called %s's bluff and was right ! %s was removed from the game ----", currentPlayer.name, playerWithCurrentBid.name, playerWithCurrentBid.name));
        } else {
            playerWithCurrentBid.removeDie();
            reOrderTurnList(playerWithCurrentBid);
            showTurnOrder();
            Console.displayMessage(String.format("%n---- %s called %s's bluff and was right ! %s loses a die -----", currentPlayer.name, playerWithCurrentBid.name, playerWithCurrentBid.name));
        }
        Console.displayMessage(String.format("%nThe last bid was die: %s | frequency %s",
                currentBid.faceUpValue, currentBid.frequency));

    }//16

    private LinkedHashMap<Player, Integer> doReRoll(int max, LinkedHashMap<Player, Integer> map) {
        Die die = new Die();
        LinkedHashMap<Player, Integer> reRollers = new LinkedHashMap<>();
        for (Map.Entry<Player, Integer> entry : map.entrySet()) {
            if (entry.getValue() == max) {
                Console.displayMessage(String.format("%n%s, please hit enter to roll your die.", entry.getKey().name));
                Console.nextLine();
                die.roll();
                Console.displayMessage(String.format("%nYou rolled a %s ! %n", die.faceUpValue));
                reRollers.put(entry.getKey(), die.faceUpValue);
            }
        }
        return reRollers;
    }//13

    private void reRoll(LinkedHashMap<Player, Integer> map, int oldMax) {
        LinkedHashMap<Player, Integer> reRollers = new LinkedHashMap<>();
        LinkedHashMap<Player, Integer> nonReRollers = new LinkedHashMap<>(map);
        boolean reRollNeeded = true;
        int counter = 0;
        int newMax = 0;
        while (true) {
            if (reRollNeeded) {
                if (counter == 0) {
                    reRollers = doReRoll(oldMax, map);
                    for (Map.Entry<Player, Integer> entry : reRollers.entrySet()) {
                        nonReRollers.remove(entry.getKey());
                    }

                } else {
                    reRollers = doReRoll(newMax, reRollers);
                }
                newMax = reRollers.values()
                        .stream()
                        .max(Comparator.naturalOrder())
                        .get();

                counter++;
            } else {
                break;
            }
            reRollNeeded = reRollNeeded(newMax, reRollers);
        }
        reRollers = sortByValue(reRollers);
        nonReRollers = sortByValue(nonReRollers);
        setTurnOrder(reRollers);
        setTurnOrder(nonReRollers);
    }//20

    private boolean reRollNeeded(int max, LinkedHashMap<Player, Integer> map) {
        List<String> names = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : map.entrySet()) {
            if (entry.getValue() == max) {
                names.add(entry.getKey().name);
            }
        }
        if (names.size() > 1) {
            Console.displayMessage("\nThere was a tie, these players need to re-roll: \n");
            for (String name : names) {
                Console.displayMessage(name + "\n");
            }
            Console.nextLine();
            return true;
        }
        return false;
    }//16

    private boolean isValidBid(String bid) {
        String[] result = bid.split(" x");
        int frequency;
        int faceUpValue;

        if (result.length != 2) {
            return false;
        }
        frequency = Integer.parseInt(result[1]);
        faceUpValue = Integer.parseInt(result[0]);
        if (frequency < currentBid.frequency) {
            Console.displayMessage("\nThe frequency of the bid needs to be increased\n");
            return false;
        }
        if (faceUpValue <= currentBid.faceUpValue &&
                frequency <= currentBid.frequency) {
            Console.displayMessage("\nThe frequency or the face up value of the bid needs to be increased\n");
            return false;
        }
        return true;
    }//20

    private <K, Integer> LinkedHashMap<K, Integer> sortByValue(LinkedHashMap<K, Integer> map) {
        List<Map.Entry<K, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((Map.Entry<K, Integer> e1, Map.Entry<K, Integer> e2) -> (java.lang.Integer) e2.getValue() - (java.lang.Integer) e1.getValue());

        LinkedHashMap<K, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<K, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }//10

    private void rollAllDice() {
        for (Player player : playerList) {
            player.roll();
        }
    }//4

    private void resetBid() {
        currentBid = new Bid(0, 0);
    }

    private void showTurnOrder() {
        Console.displayMessage("\nTurn order: \n");
        for (Player player : turnOrder) {
            Console.displayMessage(player.name + "\n");
        }

    }//6

    private void showDiceCount() {
        int counter = 0;
        for (Player player : turnOrder) {
            counter += player.cup.dice.size();
        }
        Console.displayMessage(String.format("%nThere are %s dice on the board.%n%n", counter));
    }//6

    private void showRemainingPlayers() {
        Console.displayMessage("Remaining Players: ");
        for (Player player : turnOrder) {
            Console.displayMessage(String.format("[Player: %s | DiceRemaining: %s] \t", player.name, player.cup.dice.size()));
        }
        Console.displayMessage("\n\n");
    }//5

    private void showCurrentBid() {
        Cup temp = new Cup(1);

        Console.displayMessage(String.format("%nCurrent bid is: die[%s], frequency[%s]%n",
                temp.buildDiceVisual(new int[]{currentBid.faceUpValue}), currentBid.frequency));
    }//5
}
