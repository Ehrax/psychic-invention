package de.in.uulm.map.quartett.multiplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by maxka on 02.02.2017.
 */

public class TurnData implements Serializable {

    private byte[] firstPlayerDeck;
    private byte[] secondPlayerDeck;
    private byte[] comparedCards;
    private String firstPlayerID, secondPlayerID;
    private long chosenAttributeID;
    private byte currentTurn;
    private long deckID;
    private byte firstPlayerPoints;
    private byte secondPlayerPoints;
    private boolean sawCompare;
    private MultiplayerTurnWinner currentTurnWinner;

    public TurnData(long deckID, byte[] firstPlayerDeck, byte[]
            secondPlayerDeck,
                    String
                            firstPlayerID, String secondPlayerID) {

        this.deckID = deckID;
        this.firstPlayerDeck = firstPlayerDeck;
        this.secondPlayerDeck = secondPlayerDeck;
        this.firstPlayerID = firstPlayerID;
        this.secondPlayerID = secondPlayerID;
        currentTurn = 1;
        chosenAttributeID = -1;
        firstPlayerPoints = 0;
        secondPlayerPoints = 0;
        comparedCards = new byte[2];
        sawCompare=true;

    }

    public MultiplayerTurnWinner getCurrentTurnWinner() {

        return currentTurnWinner;
    }

    public void setCurrentTurnWinner(MultiplayerTurnWinner currentTurnWinner) {

        this.currentTurnWinner = currentTurnWinner;
    }

    public static byte[] convertToBytes(TurnData turnData) {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(turnData);
            return bos.toByteArray();
        } catch (IOException ioe) {
            return null;
        }
    }

    public void setSawCompare(boolean sawCompare){
        this.sawCompare=sawCompare;
    }

    public boolean getSawCompare(){
        return sawCompare;
    }

    public static TurnData convertFromBytes(byte[] bytes)  {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            return (TurnData) in.readObject();
        }catch(IOException|ClassNotFoundException e){
            return null;
        }
    }

    public void incrementFirstPlayerPoints(){
        firstPlayerPoints++;
    }

    public void incrementSecondPlayerPoints(){
        secondPlayerPoints++;
    }

    public void incrementCurrentTurn(){
        currentTurn++;
    }

    public byte[] getComparedCards() {

        return comparedCards;
    }

    public void setComparedCards(byte[] comparedCards) {

        this.comparedCards = comparedCards;
    }

    public byte getFirstPlayerPoints() {

        return firstPlayerPoints;
    }

    public void setFirstPlayerPoints(byte firstPlayerPoints) {

        this.firstPlayerPoints = firstPlayerPoints;
    }

    public byte getSecondPlayerPoints() {

        return secondPlayerPoints;
    }

    public void setSecondPlayerPoints(byte secondPlayerPoints) {

        this.secondPlayerPoints = secondPlayerPoints;
    }

    public long getDeckID() {

        return deckID;
    }

    public void setDeckID(long deckID) {

        this.deckID = deckID;
    }

    public void setFirstPlayerDeck(byte[] firstPlayerDeck) {

        this.firstPlayerDeck = firstPlayerDeck;
    }

    public void setSecondPlayerDeck(byte[] secondPlayerDeck) {

        this.secondPlayerDeck = secondPlayerDeck;
    }

    public void setCurrentTurn(byte currentTurn) {

        this.currentTurn = currentTurn;
    }

    public void setSecondPlayerID(String secondPlayerID) {

        this.secondPlayerID = secondPlayerID;
    }

    public void setChosenAttributeID(long chosenAttributeID) {

        this.chosenAttributeID = chosenAttributeID;
    }

    public byte[] getFirstPlayerDeck() {

        return firstPlayerDeck;
    }

    public byte[] getSecondPlayerDeck() {

        return secondPlayerDeck;
    }

    public String getFirstPlayerID() {

        return firstPlayerID;
    }

    public String getSecondPlayerID() {

        return secondPlayerID;
    }

    public long getChosenAttributeID() {

        return chosenAttributeID;
    }

    public byte getCurrentTurn() {

        return currentTurn;
    }

    @Override
    public String toString() {

        return "Round: " + currentTurn + ", ChosenAttribute: " +
                "" + chosenAttributeID + ", FirstPlayerID: " + firstPlayerID;
    }
}
