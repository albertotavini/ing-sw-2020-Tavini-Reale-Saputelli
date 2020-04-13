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

public class NewController implements Observer<playerMove> {

    private final View view;
    private final Model model;
    private TurnState currentTurnState;
    private PlaceState currentPlaceState;
    private int placingTimes;
    private GodSetupStep godStep;
    private GameParts part;
    private ArrayList <String> listOfGods = new ArrayList<>();

    public NewController(Model model, View view) {
        this.view = view;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        if (getCurrentTurnState() == null)
            //starting from the first state
            setCurrentTurnState(SelectionState.getInstance());
        if (getCurrentPlaceState() == null)
            //starting from the first state
            setCurrentPlaceState(FirstPlacingState.getInstance());
        godStep = GodSetupStep.InitialChoice;
        part = GameParts.GodPart;
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


    private synchronized void performTurn(playerMove message) {

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
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ " seleziona il worker da muovere");
            if (model.getCurrentPlayer().getPersonalTurn().selectWorker(model.getGameboard(), message)) {
                System.out.println("I'm in SelectionState");
                setCurrentTurnState(MoveState.getInstance());
                System.out.println("Changed state in MoveState");
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ " seleziona dove muovere");
                model.informView();
            }
        } else if (getCurrentTurnState() instanceof MoveState) {
            if (model.getCurrentPlayer().getPersonalTurn().move(model.getGameboard(), message)) {
                System.out.println("I'm in MoveState");
                setCurrentTurnState(BuildState.getInstance());
                //checks if the player wins
                if (model.getCurrentPlayer().getPersonalTurn().isWinner()) {
                    part = GameParts.WinnerPart;
                    model.getGameboard().setBoardMessage("partita finita");
                    return;
                }
                System.out.println("Changed state in BuildState");
                model.informView();
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ " seleziona dove costruire");

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
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ " tocca a te, seleziona chi muovere");
                model.informView();
            }
        }
    }


    private synchronized boolean performPlace(playerMove message) {

        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        if (getCurrentPlaceState() instanceof FirstPlacingState) {
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" piazza il tuo worker A");
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "A")) {
                System.out.println("Placing worker A");
                setCurrentPlaceState(SecondPlacingState.getInstance());
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" piazza il tuo worker B");
                model.informView();
            }
        } else if (getCurrentPlaceState() instanceof SecondPlacingState) {
            if (model.getCurrentPlayer().getPersonalTurn().placeWorker(model.getGameboard(), message, "B")) {
                System.out.println("Placing worker B");
                System.out.println("Placing is completed");
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
            model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" tu che sei il più giovane, dimmi i nomi delle divinità");
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
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" scelga una divinità tra quelle proposte");
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
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" scelga la divinità");
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
               model.getGameboard().setBoardMessage("le divinità sono state scelte");
               model.setCurrentPlayer(model.getPlayerList().get(0));
               return true;

            }
        }
        return false;
    }

    @Override
    public void update(playerMove message) {

        if (model.checkIfOnePlayerRemains()) {
            part = GameParts.WinnerPart;
        }
        //first selects the gods
        if (part == GameParts.GodPart) {
            if(chooseGods(message)) {
                part = GameParts.PlacePart1;
                model.getGameboard().setBoardMessage("siamo nella fase di place");
            }
        }
        //place for player 1
        else if (part == GameParts.PlacePart1) {
            if(performPlace(message)) {
                part = GameParts.PlacePart2;
                model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" tocca a te fare place");
            }
        }
        //place for player 2
        else if (part == GameParts.PlacePart2) {
            if(performPlace(message)) {
                if (model.getPlayerList().size() == 2) {
                    part = GameParts.TurnPart;
                    model.setCurrentPlayer(model.getPlayerList().get(0));
                    model.getGameboard().setBoardMessage("siamo nella fase di turn, inizia " + model.getCurrentPlayer().getName());
                }
                if (model.getPlayerList().size() == 3) {
                    part = GameParts.PlacePart3;
                    model.getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+" tocca a te fare place");
                }
            }
        }
        //place for player 3 if needed
        else if (part == GameParts.PlacePart3) {
            if(performPlace(message)) {
                part = GameParts.TurnPart;
                model.getGameboard().setBoardMessage("siamo nella fase di turn");
                model.setCurrentPlayer(model.getPlayerList().get(0));
            }
        }
        //iterates on the turns until one player winners
        else if (part == GameParts.TurnPart){
            performTurn(message);
        }

        if(part == GameParts.WinnerPart){
            System.out.println(model.getCurrentPlayer()+" is the winner");
        }
        model.informView();
    }

}



