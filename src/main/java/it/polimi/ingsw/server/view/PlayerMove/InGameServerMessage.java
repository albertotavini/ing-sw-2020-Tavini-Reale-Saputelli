package it.polimi.ingsw.server.view.PlayerMove;

import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;

import java.io.Serializable;

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
