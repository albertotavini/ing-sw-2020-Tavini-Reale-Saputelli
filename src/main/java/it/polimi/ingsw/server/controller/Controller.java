package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.view.View;
import it.polimi.ingsw.server.view.playerMove;
import it.polimi.ingsw.server.model.Model;

import java.util.ArrayList;

public class Controller implements Observer<playerMove> {

    private final View view;
    private final Model model;
    private TurnState currentTurnState;
    private PlaceState currentPlaceState;


    private GameState currentGameState;
    private int placingTimes;
    private GodSetupStep godStep;
    private ArrayList <String> listOfGods = new ArrayList<>();

    public Controller(Model model, View view) {
        this.view = view;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        if (getCurrentTurnState() == null)
            //starting from the first state
            setCurrentTurnState(SelectionState.getInstance());
        if (getCurrentPlaceState() == null)
            //starting from the first state
            setCurrentPlaceState(FirstPlacingState.getInstance());
        if (getCurrentGameState() == null)
            //starting from the first state
            setCurrentGameState(GodPart.getInstance());
        godStep = GodSetupStep.InitialChoice;
        placingTimes = model.getPlayerList().size();
    }

    public Model getModel() {
        return model;
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

    public GameState getCurrentGameState() { return currentGameState; }

    public void setCurrentGameState(GameState currentGameState) { this.currentGameState = currentGameState; }


    private synchronized void performTurn(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();

        //if the player who gave input is not currentplayer, returns
        if (!model.isPlayerTurn(message.getPlayer())) {
            //eventuale notifica alla view
            return;
        }

        if (getCurrentTurnState() instanceof SelectionState) {
            //IF the player loses, i remove it and return
            if(!model.getCurrentPlayer().getPersonalTurn().checkIfCanMove(model.getGameboard())){
                model.getCurrentPlayer().getPersonalTurn().clearBoard(model.getGameboard());
                model.updatePlayersAfterLosing();
                return;
            }
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", select the worker to move.");
            if (model.getCurrentPlayer().getPersonalTurn().selectWorker(model.getGameboard(), message)) {
                System.out.println("I'm in SelectionState");
                setCurrentTurnState(MoveState.getInstance());
                System.out.println("Changed state in MoveState");
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", select where you want to move.");
                model.informView();
            }
        } else if (getCurrentTurnState() instanceof MoveState) {
            if (model.getCurrentPlayer().getPersonalTurn().move(model.getGameboard(), message)) {
                System.out.println("I'm in MoveState");
                setCurrentTurnState(BuildState.getInstance());
                //checks if the player wins
                if (model.getCurrentPlayer().getPersonalTurn().isWinner()) {
                    setCurrentGameState(WinnerPart.getInstance());
                    model.getGameboard().setBoardMessage("Game over.");
                    return;
                }
                System.out.println("Changed state in BuildState");
                model.informView();
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", select where you want to build.");

            }
        } else if (getCurrentTurnState() instanceof BuildState) {
            //if the player cannot build, he's removed from game
            if(!model.getCurrentPlayer().getPersonalTurn().checkIfCanBuild(model.getGameboard())) {
                model.getCurrentPlayer().getPersonalTurn().clearBoard(model.getGameboard());
                model.updatePlayersAfterLosing();
                return;
            }
            if (model.getCurrentPlayer().getPersonalTurn().build(model.getGameboard(), message)) {
                System.out.println("I'm in BuildState");
                System.out.println("Turn is completed!");
                setCurrentTurnState(SelectionState.getInstance());
                model.updateTurn();
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", it's your turn, select the worker to move.");
                model.informView();
            }
        }
    }


    private synchronized boolean performPlace(playerMove message) {
        int row = message.getRow();
        int column = message.getColumn();

        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        if (getCurrentPlaceState() instanceof FirstPlacingState) {
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", place your worker A.");
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "A")) {
                System.out.println("Placing worker A");
                setCurrentPlaceState(SecondPlacingState.getInstance());
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", place your worker B.");
                model.informView();
            }
        } else if (getCurrentPlaceState() instanceof SecondPlacingState) {
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "B")) {
                System.out.println("Placing worker B");
                System.out.println("Placing is complete.");
                setCurrentPlaceState(FirstPlacingState.getInstance());
                model.updateTurn();
                model.informView();
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
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ " you are the youngest. Choose " +model.getPlayerList().size() + " Gods.");
            if (!model.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (checkGodExistence(message)) {
                listOfGods.add(Godname);
            }
            if (getModel().getPlayerList().size() == listOfGods.size()) {
                godStep = GodSetupStep.OlderChooses;
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-1));
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", choose your God.");
            }
        }

        //the oldest player chooses his god
        else if (godStep == GodSetupStep.OlderChooses){
            if (!model.isPlayerTurn(message.getPlayer())) {
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = model.getCurrentPlayer();
                player.setPersonalTurn(new Turn(player, Color.GREEN, Godname));
                listOfGods.remove(Godname);
                godStep = GodSetupStep.OtherChooses;
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-2));
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", choose your God.");
            }
        }

        else if (godStep == GodSetupStep.OtherChooses){
            if (!model.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = model.getCurrentPlayer();
                player.setPersonalTurn(new Turn(player, Color.RED, Godname));
                listOfGods.remove(Godname);
               if (listOfGods.size() == 1) {
                   player = model.getPlayerList().get((model.getPlayerList().size()-3));
                   player.setPersonalTurn(new Turn (player, Color.YELLOW, listOfGods.get(0)));
               }
               model.getGameboard().setBoardMessage("Gods have been chosen.");
               model.setCurrentPlayer(model.getPlayerList().get(0));
               return true;

            }
        }
        return false;
    }

    @Override
    public void update(playerMove message) {

        if (model.checkIfOnePlayerRemains()) {
            setCurrentGameState(WinnerPart.getInstance());
        }

        //first selects the gods
        if (getCurrentGameState() instanceof GodPart) {
            if(chooseGods(message)) {
                setCurrentGameState(PlacePart1.getInstance());
                model.getGameboard().setBoardMessage("We're in the place part");
            }
        }

        //place for player 1
        else if (getCurrentGameState() instanceof PlacePart1) {
            if(performPlace(message)) {
                setCurrentGameState(PlacePart2.getInstance());
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", it's your turn to place");
            }
        }

        //place for player 2
        else if (getCurrentGameState() instanceof PlacePart2) {
            if(performPlace(message)) {
                if (model.getPlayerList().size() == 2) {
                    setCurrentGameState(TurnPart.getInstance());
                    model.setCurrentPlayer(model.getPlayerList().get(0));
                    model.getGameboard().setBoardMessage("We're in the turn part. You start, " + model.getCurrentPlayer().getName());
                }
                if (model.getPlayerList().size() == 3) {
                    setCurrentGameState(PlacePart3.getInstance());
                    model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+", it's your turn to place ");
                }
            }
        }

        //place for player 3 if needed
        else if (getCurrentGameState() instanceof PlacePart3) {
            if(performPlace(message)) {
                setCurrentGameState(TurnPart.getInstance());
                model.getGameboard().setBoardMessage("We're in the turn part.");
                model.setCurrentPlayer(model.getPlayerList().get(0));
            }
        }
        //iterates on the turns until one player winners
        else if (getCurrentGameState() instanceof TurnPart){
            performTurn(message);
        }

        if(getCurrentGameState() instanceof WinnerPart){
            System.out.println(model.getCurrentPlayer()+" is the winner!");
        }
        model.informView();
    }

}



