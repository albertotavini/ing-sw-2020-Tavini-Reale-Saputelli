package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import it.polimi.ingsw.server.model.Color;

import java.io.Serializable;

/**
 *Message brought by model when notifying view of what has happened after the last update of the controller
 *conveys information about the last operation and hints on the next
 */
public class ModelMessage implements Serializable {


    private final String message;
    private final ModelMessageType modelMessageType;

    private final String currentPlayer;
    //private final boolean broadcast;
    private final ModelError modelError;
    private final Color colorOfTheCurrent;


    /**
     * The basic constuctor, that conveys just the type and an eventual
     * @param modelMessageType hints on game status
     * @param message text message to show to the players
     */
    public ModelMessage (ModelMessageType modelMessageType, String message) {
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = " ";
        this.modelError = ModelError.NONE;
        colorOfTheCurrent = Color.NONE;
    }


    /**
     * A more complicated constructor
     * @param modelMessageType hints on game status
     * @param error eventual error last input brought up
     * @param message text message to show to the players
     * @param player used to tell the current player
     */
    public ModelMessage (ModelMessageType modelMessageType, ModelError error, String message, String player){
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = player;
        this.modelError = error;
        colorOfTheCurrent = Color.NONE;
    }


    /**
     * A particular constructor
     * @param modelMessageType hints on game status
     * @param error eventual error last input brought up
     * @param message text message to show to the players
     * @param player used to tell the current player
     * @param color used when gods are chose to associate with colors of worker
     */
    public ModelMessage (ModelMessageType modelMessageType, ModelError error, String message, String player, Color color){
        this.message = message;
        this.modelMessageType = modelMessageType;
        this.currentPlayer = player;
        this.modelError = error;
        this.colorOfTheCurrent = color;
    }

    /**
     * @param currentPlayer the name that needs to be inserted
     * @return a copy of the message with current player changed
     */
    public ModelMessage copyAndAddPlayer(String currentPlayer) {
        return new ModelMessage(this.modelMessageType, this.modelError, this.message, currentPlayer);
    }

    /**
     * @param error when the input given is not accepted the related error is added through modelmessage with this
     * @return a copy of the message with error changed
     */
    public ModelMessage copyAndAddError (ModelError error) {
        return new ModelMessage(this.modelMessageType, error, this.message, this.currentPlayer);
    }

    /**
     * @param info other text that needs to be concatenated to the already present message
     * @return a copy of the ModelMessage with message extended with info
     */
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


