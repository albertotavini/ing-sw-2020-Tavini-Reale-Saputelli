package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.view.playerMove;
import it.polimi.ingsw.server.model.Match;

public class NewController implements Observer<playerMove> {

    private final Match match;
    private TurnState currentTurnState;
    private PlaceState currentPlaceState;
    private int placingTimes;

    public NewController(Match match) {
        this.match = match;
        if (getCurrentTurnState() == null)
            //starting from the first state
            setCurrentTurnState(SelectionState.getInstance());
        if (getCurrentPlaceState() == null)
            //starting from the first state
            setCurrentPlaceState(FirstPlacingState.getInstance());
        placingTimes = match.getPlayerList().size();
    }

    public Match getMatch() {
        return match;
    }

    public TurnState getCurrentTurnState() {
        return currentTurnState;
    }

    public void setCurrentTurnState(TurnState currentTurnState) {
        this.currentTurnState = currentTurnState;
    }

    public PlaceState getCurrentPlaceState() {
        return currentPlaceState;
    }

    public void setCurrentPlaceState(PlaceState currentPlaceState) {
        this.currentPlaceState = currentPlaceState;
    }


    private synchronized void performTurn(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();

        //if the player who gave input is not currentplayer, returns
        if (!match.isPlayerTurn(message.getPlayer())) {
            //eventuale notifica alla view
            return;
        }

        if (getCurrentTurnState() instanceof SelectionState) {
            if (match.getCurrentPlayer().getPersonalTurn().selectWorker(match.getGameboard(), row, column)) {
                System.out.println("I'm in SelectionState");
                setCurrentTurnState(MoveState.getInstance());
                System.out.println("Changed state in MoveState");
            }
        } else if (getCurrentTurnState() instanceof MoveState) {
            if (match.getCurrentPlayer().getPersonalTurn().move(match.getGameboard(), row, column)) {
                System.out.println("I'm in MoveState");
                setCurrentTurnState(BuildState.getInstance());
                System.out.println("Changed state in BuildState");
            }
        } else if (getCurrentTurnState() instanceof BuildState) {
            if (match.getCurrentPlayer().getPersonalTurn().build(match.getGameboard(), message.getRow(), message.getColumn())) {
                System.out.println("I'm in BuildState");
                System.out.println("Turn is completed!");
                //devo rimettere a SelectionState l'attributo currentTurnState?
                match.updateTurn();
            }
        }
    }


    private synchronized void performPlace(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();

        //if the player is not the current one, doesn't consider the input given
        if (!match.isPlayerTurn(message.getPlayer())) {
            return;
        }

        if (getCurrentPlaceState() instanceof FirstPlacingState) {
            if (match.getCurrentPlayer().getPersonalTurn().placeWorker(match.getGameboard(), row, column, "A")) {
                System.out.println("Placing worker A");
                setCurrentPlaceState(SecondPlacingState.getInstance());
            }
        } else if (getCurrentPlaceState() instanceof SecondPlacingState) {
            if (match.getCurrentPlayer().getPersonalTurn().placeWorker(match.getGameboard(), row, column, "B")) {
                System.out.println("Placing worker B");
                System.out.println("Placing is completed");
                //devo rimettere a PlaceFirstWorker l'attributo currentPlaceState?
                match.updateTurn();
            }
        }
    }


    @Override
    public void update(playerMove message) {

        if(placingTimes == 0) {
            performTurn(message);
        }

        else {
            performPlace(message);
            placingTimes--;
        }

    }

}



