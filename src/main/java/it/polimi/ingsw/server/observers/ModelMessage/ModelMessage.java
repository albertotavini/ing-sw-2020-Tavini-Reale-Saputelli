package it.polimi.ingsw.server.observers.ModelMessage;

import java.io.Serializable;

public class ModelMessage implements Serializable {
    private String message;
    private ModelMessageType modelMessageType;


    public ModelMessage (ModelMessageType modelMessageType, String string) {
        this.message = string;
        this.modelMessageType = modelMessageType;
    }

    public String getMessage() {
        return message;
    }

    public ModelMessageType getModelMessageType() {
        return modelMessageType;
    }
}


