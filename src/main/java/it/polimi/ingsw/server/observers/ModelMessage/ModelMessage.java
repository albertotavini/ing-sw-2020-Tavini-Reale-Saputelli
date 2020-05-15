package it.polimi.ingsw.server.observers.ModelMessage;

import java.io.Serializable;

public class ModelMessage implements Serializable {


    private String message;
    private ModelMessageType modelMessageType;
    private String receivingPlayer;
    private boolean broadcast;

    public ModelMessage (ModelMessageType modelMessageType, String message) {
        this.message = message;
        this.modelMessageType = modelMessageType;
        receivingPlayer = " ";
        broadcast = true;
    }


    public void addInfo (String adding){
        if (!message.contains(adding)) {
            message = message.concat("\n" + adding);
        }
    }


    public String getReceivingPlayer() {
        return receivingPlayer;
    }

    public void setReceivingPlayer(String addresseePlayer) {
        this.receivingPlayer = addresseePlayer;
    }


    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }



    public String getMessage() {
        return message;
    }

    public ModelMessageType getModelMessageType() {
        return modelMessageType;
    }
}


