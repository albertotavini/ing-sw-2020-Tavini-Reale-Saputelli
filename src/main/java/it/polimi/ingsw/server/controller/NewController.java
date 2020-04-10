package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.view.View;
import it.polimi.ingsw.server.view.playerMove;
import it.polimi.ingsw.server.model.Match;

import java.util.ArrayList;

public class NewController implements Observer<playerMove> {

    private final View view;
    private final Match match;
    private TurnState currentTurnState;
    private PlaceState currentPlaceState;
    private int placingTimes;
    private GodSetupStep godStep;
    private GameParts part;
    private ArrayList <String> listOfGods = new ArrayList<>();

    public NewController(Match match, View view) {
        this.view = view;
        this.match = match;
        match.setCurrentPlayer(match.getPlayerList().get(0));
        if (getCurrentTurnState() == null)
            //starting from the first state
            setCurrentTurnState(SelectionState.getInstance());
        if (getCurrentPlaceState() == null)
            //starting from the first state
            setCurrentPlaceState(FirstPlacingState.getInstance());
        godStep = GodSetupStep.InitialChoice;
        part = GameParts.GodPart;
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
            //IF the player loses, i remove it and return
            if(!match.getCurrentPlayer().getPersonalTurn().checkIfCanMove(match.getGameboard())){
                match.updatePlayersAfterLosing();
                return;
            }
            match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+ " seleziona il worker da muovere");
            if (match.getCurrentPlayer().getPersonalTurn().selectWorker(match.getGameboard(), message)) {
                System.out.println("I'm in SelectionState");
                setCurrentTurnState(MoveState.getInstance());
                System.out.println("Changed state in MoveState");
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+ " seleziona dove muovere");
                match.informView();
            }
        } else if (getCurrentTurnState() instanceof MoveState) {
            if (match.getCurrentPlayer().getPersonalTurn().move(match.getGameboard(), message)) {
                System.out.println("I'm in MoveState");
                setCurrentTurnState(BuildState.getInstance());
                //checks if the player wins
                if (match.getCurrentPlayer().getPersonalTurn().isWinner() == true) {
                    part = GameParts.WinnerPart;
                    match.getGameboard().setBoardMessage("partita finita");
                    return;
                }
                System.out.println("Changed state in BuildState");
                match.informView();
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+ " seleziona dove costruire");

            }
        } else if (getCurrentTurnState() instanceof BuildState) {
            if(!match.getCurrentPlayer().getPersonalTurn().checkIfCanBuild(match.getGameboard())) {

                match.updatePlayersAfterLosing();
                return;
            }
            if (match.getCurrentPlayer().getPersonalTurn().build(match.getGameboard(), message)) {
                System.out.println("I'm in BuildState");
                System.out.println("Turn is completed!");
                setCurrentTurnState(SelectionState.getInstance());
                match.updateTurn();
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+ " tocca a te, seleziona chi muovere");
                match.informView();
            }
        }
    }


    private synchronized boolean performPlace(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();

        //if the player is not the current one, doesn't consider the input given
        if (!match.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        if (getCurrentPlaceState() instanceof FirstPlacingState) {
            match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" piazza il tuo worker A");
            if (match.getCurrentPlayer().getPersonalTurn().placeWorker(match.getGameboard(), message, "A")) {
                System.out.println("Placing worker A");
                setCurrentPlaceState(SecondPlacingState.getInstance());
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" piazza il tuo worker B");
                match.informView();
            }
        } else if (getCurrentPlaceState() instanceof SecondPlacingState) {
            if (match.getCurrentPlayer().getPersonalTurn().placeWorker(match.getGameboard(), message, "B")) {
                System.out.println("Placing worker B");
                System.out.println("Placing is completed");
                setCurrentPlaceState(FirstPlacingState.getInstance());
                match.updateTurn();
                match.informView();
                return true;
            }
        }
        return false;
    }


    public boolean checkGodExistence(playerMove message){
        if(GodLookUpTable.lookUp( message.getGenericMessage() ) != null )
            return true;
        else
            return false;
    }

    public boolean chooseGods (playerMove message) {
        String Godname = message.getGenericMessage();
        Player player;
        //part where the younger player chooses a number of gods equal to the number of players
        if (godStep == GodSetupStep.InitialChoice) {
            match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" tu che sei il più giovane, dimmi i nomi delle divinità");
            if (!match.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (checkGodExistence(message)) {
                listOfGods.add(Godname);
            }
            if (getMatch().getPlayerList().size() == listOfGods.size()) {
                godStep = GodSetupStep.OlderChooses;
                match.setCurrentPlayer(match.getPlayerList().get(match.getPlayerList().size()-1));
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" scelga una divinità tra quelle proposte");
            }
        }

        //the oldest player chooses his god
        else if (godStep == GodSetupStep.OlderChooses){
            if (!match.isPlayerTurn(message.getPlayer())) {
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = match.getCurrentPlayer();
                player.setPersonalTurn(new Turn(player, Color.GREEN, Godname));
                listOfGods.remove(Godname);
                godStep = GodSetupStep.OtherChooses;
                match.setCurrentPlayer(match.getPlayerList().get(match.getPlayerList().size()-2));
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" scelga la divinità");
            }
        }

        else if (godStep == GodSetupStep.OtherChooses){
            if (!match.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = match.getCurrentPlayer();
                player.setPersonalTurn(new Turn(player, Color.RED, Godname));
                listOfGods.remove(Godname);
               if (listOfGods.size() == 1) {
                   player = match.getPlayerList().get((match.getPlayerList().size()-3));
                   player.setPersonalTurn(new Turn (player, Color.YELLOW, listOfGods.get(0)));
               }
               match.getGameboard().setBoardMessage("le divinità sono state scelte");
               match.setCurrentPlayer(match.getPlayerList().get(0));
               return true;

            }
        }
        return false;
    }

    @Override
    public void update(playerMove message) {

        if (match.checkIfOnePlayerRemains()) {
            part = GameParts.WinnerPart;
        }
        //first selects the gods
        if (part == GameParts.GodPart) {
            if(chooseGods(message)) {
                part = GameParts.PlacePart1;
                match.getGameboard().setBoardMessage("siamo nella fase di place");
            }
        }
        //place for player 1
        else if (part == GameParts.PlacePart1) {
            if(performPlace(message)) {
                part = GameParts.PlacePart2;
                match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" tocca a te fare place");
            }
        }
        //place for player 2
        else if (part == GameParts.PlacePart2) {
            if(performPlace(message)) {
                if (match.getPlayerList().size() == 2) {
                    part = GameParts.TurnPart;
                    match.setCurrentPlayer(match.getPlayerList().get(0));
                    match.getGameboard().setBoardMessage("siamo nella fase di turn, inizia " +match.getCurrentPlayer().getName());
                }
                if (match.getPlayerList().size() == 3) {
                    part = GameParts.PlacePart3;
                    match.getGameboard().setBoardMessage(match.getCurrentPlayer().getName()+" tocca a te fare place");
                }
            }
        }
        //place for player 3 if needed
        else if (part == GameParts.PlacePart3) {
            if(performPlace(message)) {
                part = GameParts.TurnPart;
                match.getGameboard().setBoardMessage("siamo nella fase di turn");
                match.setCurrentPlayer(match.getPlayerList().get(0));
            }
        }
        //iterates on the turns until one player winners
        else if (part == GameParts.TurnPart){
            performTurn(message);
        }

        if(part == GameParts.WinnerPart){
            System.out.println(match.getCurrentPlayer()+" is the winner");
        }
        match.informView();
    }

}



