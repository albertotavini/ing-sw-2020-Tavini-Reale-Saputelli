package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import java.io.Serializable;

public class ModelMessage implements Serializable {


    private final String message;
    private final ModelMessageType modelMessageType;
    private final String currentPlayer;
    private final boolean broadcast;
    private final ModelError modelError;

    public ModelMessage (ModelMessageType modelMessageType, String message) {
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = " ";
        this.broadcast = false;
        this.modelError = ModelError.NONE;
    }



    public ModelMessage (ModelMessageType modelMessageType, ModelError error, String message, boolean broadcast, String player){
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.broadcast = broadcast;
        this.currentPlayer = player;
        this.modelError = error;
    }

    public ModelMessage copyAndAddPlayer(String receivingPlayer) {
        return new ModelMessage(this.modelMessageType, this.modelError, this.message, this.broadcast, receivingPlayer);
    }

    public ModelMessage copyAndAddError (ModelError error) {
        return new ModelMessage(this.modelMessageType, error, this.message, this.broadcast, this.currentPlayer);
    }

    public ModelMessage copyAndAddInfo (String info) {
        return new ModelMessage(this.modelMessageType, this.modelError, this.message.concat(info), this.broadcast, this.currentPlayer);
    }

    public ModelError getModelError() {
        return modelError;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }


    public boolean isBroadcast() {
        return broadcast;
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
            return modelMessageType + " broadcast: " + broadcast + " tocca a " + currentPlayer;
        }
        else return modelMessageType + " broadcast: " + broadcast + " tocca a " + currentPlayer + "\nERROR:"+modelError.toString();
    }
}


