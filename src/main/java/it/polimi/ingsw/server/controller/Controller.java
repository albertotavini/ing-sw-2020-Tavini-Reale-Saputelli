package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Match;
import it.polimi.ingsw.server.utils.gameMessages;

import it.polimi.ingsw.server.view.playerMove;

public class Controller {

    private final Match match;

    public Controller (Match match){
        this.match = match;
    }


    private synchronized void performTurn(playerMove move){
        if(!match.isPlayerTurn(move.getPlayer())){
            //eventuale notifica alla view
            return;
        }

        if(!match.isFeasibleMove(move.getRow(), move.getColumn())){
            //eventuale notifica alla view
            return;
        }

        match.performMove(move.getRow(), move.getColumn(), move.getPlayer());
        match.updateTurn();
    }


}
