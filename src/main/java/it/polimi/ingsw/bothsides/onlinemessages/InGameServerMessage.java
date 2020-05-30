package it.polimi.ingsw.bothsides.onlinemessages;

import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;

import java.io.Serializable;


/**
 * this class is the envelope through which both BoardPhotography and ModelMessage are sent from one remoteView to its client
 * after model has notified the processing of a PlayerMove
 */
public class InGameServerMessage implements Serializable {


    private final BoardPhotography boardPhotography;
    private final ModelMessage modelMessage;


    public InGameServerMessage(BoardPhotography boardPhotography, ModelMessage modelMessage) {
        this.boardPhotography = boardPhotography;
        this.modelMessage = modelMessage;
    }


    public ModelMessage getModelMessage() {
        return modelMessage;
    }

    public BoardPhotography getBoardPhotography() {
        return boardPhotography;
    }
}
