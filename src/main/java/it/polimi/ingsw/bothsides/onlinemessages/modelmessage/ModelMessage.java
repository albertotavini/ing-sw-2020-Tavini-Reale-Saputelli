package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import it.polimi.ingsw.server.model.Color;

import java.io.Serializable;

public class ModelMessage implements Serializable {

    private final String message;
    private final ModelMessageType modelMessageType;

    private final String currentPlayer;
    //private final boolean broadcast;
    private final ModelError modelError;
    private final Color colorOfTheCurrent;

    public ModelMessage (ModelMessageType modelMessageType, String message) {
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = " ";
        this.modelError = ModelError.NONE;
        colorOfTheCurrent = Color.NONE;
    }



    public ModelMessage (ModelMessageType modelMessageType, ModelError error, String message, String player){
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = player;
        this.modelError = error;
        colorOfTheCurrent = Color.NONE;
    }

    public ModelMessage (ModelMessageType modelMessageType, ModelError error, String message, String player, Color color){
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = player;
        this.modelError = error;
        this.colorOfTheCurrent = color;
    }

    public ModelMessage copyAndAddPlayer(String receivingPlayer) {
        return new ModelMessage(this.modelMessageType, this.modelError, this.message, receivingPlayer);
    }

    public ModelMessage copyAndAddError (ModelError error) {
        return new ModelMessage(this.modelMessageType, error, this.message, this.currentPlayer);
    }

    public ModelMessage copyAndAddInfo (String info) {
        return new ModelMessage(this.modelMessageType, this.modelError, this.message.concat(info), this.currentPlayer);
    }

    public ModelError getModelError() {
        return modelError;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }




    public Color getColorOfTheCurrent() {
        return colorOfTheCurrent;
    }


    public String getMessage() {
        return message;
    }

    public ModelMessageType getModelMessageType() {
        return modelMessageType;
    }

    @Override
    public String toString() {
        if (modelError == ModelError.NONE) {
            return modelMessageType + " tocca a " + currentPlayer;
        }
        else return modelMessageType +" tocca a " + currentPlayer + "\nERROR:"+modelError.toString();
    }
}


