package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.View;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMoveType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Controller implements Observer<PlayerMove> {

    private final View view;
    private final Model model;
    private TurnPart turnPart;
    private GamePart gamePart;
    private PlacePart placePart;
    private GodSetupPart godSetupPart;


    private int godChoiceTimes;
    private ArrayList <String> listOfGods = new ArrayList<>();

    public Controller(Model model, View view) {
        this.view = view;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        turnPart = TurnPart.Select;
        placePart = PlacePart.FirstPlacing;
        gamePart = GamePart.God;
        godSetupPart = GodSetupPart.InitialChoice;
        godChoiceTimes = model.getPlayerList().size();
    }

    public Controller(Model model) {
        this.view = null;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        turnPart = TurnPart.Select;
        placePart = PlacePart.FirstPlacing;
        gamePart = GamePart.God;
        godSetupPart = GodSetupPart.InitialChoice;
        godChoiceTimes = model.getPlayerList().size();
    }

    //"utilities" methods
    public Model getModel() { return model; }

    public View getView() {return view; }

    public ArrayList<String> getListOfGods() {return listOfGods;}

    public TurnPart getTurnPart() {
        return turnPart;
    }

    public void setTurnPart(TurnPart turnPart) {
        this.turnPart = turnPart;
    }


    public GamePart getGamePart() {
        return gamePart;
    }

    public void setGamePart(GamePart gamePart) {
        this.gamePart = gamePart;
    }

    public PlacePart getPlacePart() {
        return placePart;
    }

    public void setPlacePart(PlacePart placePart) {
        this.placePart = placePart;
    }

    public GodSetupPart getGodSetupPart() {
        return godSetupPart;
    }

    public void setGodSetupPart(GodSetupPart godSetupPart) {
        this.godSetupPart = godSetupPart;
    }


    public int getGodChoiceTimes() { return godChoiceTimes; }

    public void setGodChoiceTimes(int godChoiceTimes) { this.godChoiceTimes = godChoiceTimes; }


    public void sendModelMessage(ModelMessageType modelMessageType, String message){
        model.getGameboard().setModelMessage( new ModelMessage(modelMessageType, message) );
    }
    public void updatingTurn(){
        model.updateTurn();
    }
    public void updatePlayersAfterLosing(){
        model.updatePlayersAfterLosing();
    }
    public Turn getCurrentPlayerTurn(){
        return model.getTurnMap().get(model.getCurrentPlayer());
    }
    public String getCurrentPlayerGodName(){
        return getCurrentPlayerTurn().getDivinityCard().getSpecificGodName();
    }
    public Board getGameBoard(){
        return model.getGameboard();
    }



    //important methods

    public boolean checkGodExistence(PlayerMove message){
        if(GodLookUpTable.lookUp( message.getGenericMessage() ) != null )
            return true;
        else
            return false;
    }

    public boolean chooseGods (PlayerMove message) {
        if(message.getType() != PlayerMoveType.GodName) {return false;}
        String Godname = message.getGenericMessage();
        Player player;
        //part where the younger player chooses a number of gods equal to the number of players
        if (godSetupPart == GodSetupPart.InitialChoice) {
            sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+ " you are the youngest. Choose " + getGodChoiceTimes() + " Gods."+ Global.godsYouCanChoseFrom);
            //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+ " you are the youngest. Choose " + getGodChoiceTimes() + " Gods."+ Global.godsYouCanChoseFrom));
            if (!model.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (checkGodExistence(message) ) {
                String sameGod = listOfGods.stream().filter(s -> s.equals(Godname)).collect(Collectors.joining());
                if(sameGod.equals("")) {
                    listOfGods.add(Godname);
                    setGodChoiceTimes(getGodChoiceTimes() - 1);
                    sendModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName() + ", you have chosen " + Godname + ". Remaining Gods are " + getGodChoiceTimes() + "." +Global.godsYouCanChoseFrom );
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName() + ", you have chosen " + Godname + ". Remaining Gods are " + getGodChoiceTimes() + "." +Global.godsYouCanChoseFrom ));
                }
            }
            if (getModel().getPlayerList().size() == listOfGods.size()) {
                setGodSetupPart(GodSetupPart.OlderChooses);
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-1));

                //informing users about the Gods they can choose
                if( listOfGods.size() == 2 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1));
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1)));
                }
                else if( listOfGods.size() == 3 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + ", " + listOfGods.get(1) + " and " + listOfGods.get(2));
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + ", " + listOfGods.get(1) + " and " + listOfGods.get(2)));
                }

            }
        }

        //the oldest player chooses his god
        else if (godSetupPart == GodSetupPart.OlderChooses){
            if (!model.isPlayerTurn(message.getPlayer())) {
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = model.getCurrentPlayer();
                //player.setPersonalTurn(new Turn(player, Color.GREEN, Godname));
                model.getTurnMap().put(player, new Turn(player, Color.GREEN, Godname));
                listOfGods.remove(Godname);

                setGodSetupPart(GodSetupPart.OtherChooses);
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-2));

                //informing users about the Gods they can choose
                if( listOfGods.size() == 1 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+", you have to choose " + listOfGods.get(0) + ".");
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+", you have to choose " + listOfGods.get(0) + "."));
                }
                else if( listOfGods.size() == 2 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1));
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1)));
                }
            }
        }

        else if (godSetupPart == GodSetupPart.OtherChooses){
            if (!model.isPlayerTurn(message.getPlayer())) {
                //eventuale notifica alla view
                return false;
            }
            if (listOfGods.contains(Godname)) {
                player = model.getCurrentPlayer();

                //player.setPersonalTurn(new Turn(player, Color.RED, Godname));
                model.getTurnMap().put(player, new Turn(player, Color.RED, Godname));

                listOfGods.remove(Godname);
                if (listOfGods.size() == 1) {
                    player = model.getPlayerList().get((model.getPlayerList().size()-3));
                    //player.setPersonalTurn(new Turn (player, Color.YELLOW, listOfGods.get(0)));
                    model.getTurnMap().put(player, new Turn (player, Color.YELLOW, listOfGods.get(0)));
                }
                model.setCurrentPlayer(model.getPlayerList().get(0));
                if( listOfGods.size() == 1 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", you have to choose " + listOfGods.get(0) + ".\n" + "Gods have been chosen.\n" + "We are now in the place part.\n");
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", you have to choose " + listOfGods.get(0) + ".\n" + "Gods have been chosen.\n" + "We are now in the place part.\n"));
                }
                else if( listOfGods.isEmpty() ) {
                    sendModelMessage(ModelMessageType.NeedsCoordinates, "Gods have been chosen.\n" + "We are now in the place part.\n");
                    //model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "Gods have been chosen.\n" + "We are now in the place part.\n"));
                }

                return true;

            }
        }
        return false;
    }


    protected synchronized boolean performPlace(PlayerMove message) {
        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        if (placePart == PlacePart.FirstPlacing) {

            sendModelMessage(ModelMessageType.NeedsCoordinates,  model.getCurrentPlayer().getName()+", place your worker A.");
            //getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,  model.getCurrentPlayer().getName()+", place your worker A."));

            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "A")) {
                //System.out.println("Placing worker A");
                setPlacePart(PlacePart.SecondPlacing);
                //placePart = PlacePart.SecondPlacing;
                sendModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+", place your worker B.");
                //getGameboard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+", place your worker B."));
            }

        } else if (placePart == PlacePart.SecondPlacing) {
            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "B")) {
                //System.out.println("Placing worker B");
                //System.out.println("Placing is complete.");
                setPlacePart(PlacePart.FirstPlacing);
                //placePart = PlacePart.FirstPlacing;
                updatingTurn();
                //updatingTurn();
                return true;
            }
        }

        return false;

    }

    protected synchronized void performTurn(PlayerMove message) {
        //if the player who gave input is not currentplayer, returns
        if (!model.isPlayerTurn(message.getPlayer())) {
            //eventuale notifica alla view
            return;
        }

        if (turnPart == TurnPart.Select) {
            //If the player loses, i remove it and return
            if(!getCurrentPlayerTurn().checkIfCanMove(getGameBoard())){
                getCurrentPlayerTurn().clearBoard(getGameBoard());
                updatePlayersAfterLosing();
                return;
            }
            //getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", select the worker to move.");
            sendModelMessage( ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+ ", select the worker to move.");
            //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+ ", select the worker to move."));

            if (getCurrentPlayerTurn().selectWorker(getGameBoard(), message)) {
                //System.out.println("I'm in SelectionState");
                setTurnPart(TurnPart.Move);
                //System.out.println("Changed state in MoveState");
                //getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", select where you want to move.");
                if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectMove(getCurrentPlayerGodName())) {
                    sendModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?"));
                }   else {
                    sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where to move.");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where to move."));
                }

                //model.informView();
            }

        } else if (turnPart == TurnPart.Move) {
            if (getCurrentPlayerTurn().move(getGameBoard(), message)) {
                //System.out.println("I'm in MoveState");
                setTurnPart(TurnPart.Build);

                //checks if the player wins
                if (getCurrentPlayerTurn().isWinner()) {
                    setGamePart(GamePart.Conclusion);
                    //getGameboard().setBoardMessage("Game over.");
                    sendModelMessage(ModelMessageType.GameOver, "Game over.");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GameOver, "Game over."));

                    return;
                }
                //System.out.println("Changed state in BuildState");
                //model.informView();
                if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectBuild(getCurrentPlayerGodName())) {
                    sendModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?"));
                }   else {
                    sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where you want to build.");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where you want to build."));
                }

            }
        } else if (turnPart == TurnPart.Build) {
            //if the player cannot build, he's removed from game
            if(!getCurrentPlayerTurn().checkIfCanBuild(getGameBoard())) {
                getCurrentPlayerTurn().clearBoard(getGameBoard());
                updatePlayersAfterLosing();
                return;
            }
            if (getCurrentPlayerTurn().build(getGameBoard(), message)) {
                //System.out.println("I'm in BuildState");
                //System.out.println("Turn is completed!");

                //Parte di Chrono, da cambiare
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////

                for(Player p : model.getPlayerList()){
                    if(GodLookUpTable.isEffectOnOpponent(model.getTurnMap().get(p).getDivinityCard().getSpecificGodName())){
                        model.getTurnMap().get(p).getDivinityCard().activateEffect(getGameBoard(), getCurrentPlayerTurn(), message);

                        if(model.getTurnMap().get(p).isWinner()) {
                            setGamePart(GamePart.Conclusion);
                            sendModelMessage(ModelMessageType.GameOver, "Game over.");
                            //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GameOver, "Game over."));
                            return;
                        }
                    }
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////77

                setTurnPart(TurnPart.Select);
                updatingTurn();
                //getGameboard().setBoardMessage(model.getCurrentPlayer().getName()+ ", it's your turn, select the worker to move.");
                sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName()+ ", it's your turn, select the worker to move.");
                //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName()+ ", it's your turn, select the worker to move."));
                //model.informView();
            }
        }
    }


    //returning false if controller is waiting the Playermove from an other player
    public boolean checkingCurrentPlayer(PlayerMove message){
        if (!message.getPlayer().equals(model.getCurrentPlayer())) {
            getGameBoard().getModelMessage().addInfo(message.getPlayer().getName()+" it's not your turn, it's "+model.getCurrentPlayer().getName()+"'s");
            model.informView();
            return false;
        }
        else return true;
    }

    @Override
    public void update(PlayerMove message, Object obj) {

        //returning if controller is waiting the Playermove from an other player
        /*if(!checkingCurrentPlayer(message))
            return;*/

        if (!message.getPlayer().equals(model.getCurrentPlayer())) {
            getGameBoard().getModelMessage().addInfo(message.getPlayer().getName()+" it's not your turn, it's "+model.getCurrentPlayer().getName()+"'s");
            model.informView();
        }

        if (model.checkIfOnePlayerRemains()) {
            setGamePart(GamePart.Conclusion);
        }

        //first selects the gods
        if (gamePart == GamePart.God) {
            if(chooseGods(message)) {
                setGamePart(GamePart.Place1);
                sendModelMessage(ModelMessageType.NeedsCoordinates, " we're in the Place Part, the youngest begins");
                //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, " we're in the Place Part, the youngest begins"));
            }
        }

        //place for player 1
        else if (gamePart == GamePart.Place1) {
            if(performPlace(message)) {
                setGamePart(GamePart.Place2);
                sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName()+", it's your turn to place");
                //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName()+", it's your turn to place"));
            }
        }

        //place for player 2
        else if (gamePart == GamePart.Place2) {
            if(performPlace(message)) {
                if (model.getPlayerList().size() == 2) {
                    setGamePart(GamePart.Turn);
                    model.setCurrentPlayer(model.getPlayerList().get(0));
                    sendModelMessage(ModelMessageType.NeedsCoordinates,"We're in the turn part. You start, " + model.getCurrentPlayer().getName());
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,"We're in the turn part. You start, " + model.getCurrentPlayer().getName()));
                }
                if (model.getPlayerList().size() == 3) {
                    setGamePart(GamePart.Place3);
                    sendModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+", it's your turn to place ");
                    //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+", it's your turn to place "));
                }
            }
        }

        //place for player 3 if needed
        else if (gamePart == GamePart.Place3) {
            if(performPlace(message)) {
                setGamePart(GamePart.Turn);
                sendModelMessage(ModelMessageType.NeedsCoordinates,"We're in the turn part. You start "+model.getCurrentPlayer().getName());
                //getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,"We're in the turn part. You start "+model.getCurrentPlayer().getName()));
                model.setCurrentPlayer(model.getPlayerList().get(0));
            }
        }
        //iterates on the turns until one player winners
        else if (gamePart == GamePart.Turn){
            performTurn(message);
        }

        if(gamePart == GamePart.Conclusion){
            sendModelMessage(ModelMessageType.GameOver, "Game over : "+model.getCurrentPlayer()+" is the winner!");
            //getModel().getGameboard().setModelMessage(new ModelMessage(ModelMessageType.GameOver, "Game over : "+model.getCurrentPlayer()+" is the winner!"));
        }



        model.informView();
    }

}




