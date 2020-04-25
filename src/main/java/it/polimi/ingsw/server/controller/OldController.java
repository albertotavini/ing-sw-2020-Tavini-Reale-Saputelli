package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.observers.ObserverVC;

import it.polimi.ingsw.server.view.playerMove.playerMove;

public class OldController implements ObserverVC <playerMove> {

    private final Model model;
    private int turnStep = 0;
    private int placeStep = 0;
    private int gameStep = 0;
    private boolean gameEnded = false;

    public OldController(Model model){
        this.model = model;
    }


    private synchronized void performTurn(playerMove message){


        //if the player who gave input is not currentplayer, returns
        if(!model.isPlayerTurn(message.getPlayer())){
            //eventuale notifica alla view
            return;
        }

        if(turnStep == 0 ){
            if (model.getCurrentPlayer().getPersonalTurn().selectWorker(model.getGameboard(), message)) {
                turnStep = 1;
            }
        }
        if (turnStep == 1) {
            if (model.getCurrentPlayer().getPersonalTurn().move(model.getGameboard(), message)) {
                turnStep = 2;
            }
        }
        if (turnStep ==2) {
            if (model.getCurrentPlayer().getPersonalTurn().build(model.getGameboard(), message)) {
                turnStep = 0;
                model.updateTurn();
            }
        }

    }

    private synchronized void performPlace(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();
        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return;
        }

        if (placeStep == 0) {
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "A")) {
                placeStep = 1;
            }
        }
        if (placeStep == 1){
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "B")) {
                placeStep = 0;
                model.updateTurn();
                gameStep++;
            }
        }

    }


    @Override
    public void update(playerMove message) {

        if (gameStep >= 0 && gameStep <2){
            //faccio la selezione dei god
            gameStep++;
        }

        if (gameStep>2 && gameStep <=4) {
            performPlace(message);
        }
        if (gameStep == 5) {
            //itero sui turni finché qualcuno non perde, non si incrementa più gameStep
            performTurn(message);
        }

        //se qualcuno ha vinto lo dichiaro

    }

}
