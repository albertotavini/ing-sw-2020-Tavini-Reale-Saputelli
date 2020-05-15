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

    ArrayList<String> getListOfGods() {return listOfGods;}

    TurnPart getTurnPart() {
        return turnPart;
    }

    private void setTurnPart(TurnPart turnPart) {
        this.turnPart = turnPart;
    }


    GamePart getGamePart() {
        return gamePart;
    }

    private void setGamePart(GamePart gamePart) {
        this.gamePart = gamePart;
    }

    PlacePart getPlacePart() {
        return placePart;
    }

    private void setPlacePart(PlacePart placePart) {
        this.placePart = placePart;
    }

    GodSetupPart getGodSetupPart() {
        return godSetupPart;
    }

    private void setGodSetupPart(GodSetupPart godSetupPart) {
        this.godSetupPart = godSetupPart;
    }


    int getGodChoiceTimes() { return godChoiceTimes; }

    private void setGodChoiceTimes(int godChoiceTimes) { this.godChoiceTimes = godChoiceTimes; }


    private void sendModelMessage(ModelMessageType modelMessageType, String message){
        model.getGameboard().setModelMessage( new ModelMessage(modelMessageType, message) );
        model.getGameboard().getModelMessage().setReceivingPlayer(model.getCurrentPlayer().getName());
    }
    private void updatingTurn(){
        model.updateTurn();
    }
    private void updatePlayersAfterLosing(){
        model.updatePlayersAfterLosing();
    }
    private Turn getCurrentPlayerTurn(){
        return model.getTurnMap().get(model.getCurrentPlayer());
    }
    private String getCurrentPlayerGodName(){
        return getCurrentPlayerTurn().getDivinityCard().getSpecificGodName();
    }
    private Board getGameBoard(){
        return model.getGameboard();
    }



    //methods for chooseGods
    boolean checkGodExistence(PlayerMove message){
        return GodLookUpTable.lookUp(message.getGenericMessage()) != null;
    }

    void addSelectedGod(String Godname) {

        String sameGod = listOfGods.stream().filter(s -> s.equals(Godname)).collect(Collectors.joining());
        if (sameGod.equals("")) {
            listOfGods.add(Godname);
            setGodChoiceTimes(getGodChoiceTimes() - 1);
            sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", you have chosen " + Godname + ". Remaining Gods are " + getGodChoiceTimes() + "." + Global.godsYouCanChoseFrom);
        }

    }

    //informing users about the Gods they can choose depending on the current GodSetupPart
    void informPlayersAboutGodChoice(GodSetupPart currentGodSetupPart){

        if(currentGodSetupPart == GodSetupPart.InitialChoice) {

            if (listOfGods.size() == 2) {
                sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1));
            } else if (listOfGods.size() == 3) {
                sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", choose your God between " + listOfGods.get(0) + ", " + listOfGods.get(1) + " and " + listOfGods.get(2));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OlderChooses){

            if( listOfGods.size() == 1 ) {
                sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+", you have to choose " + listOfGods.get(0) + ".");
            }
            else if( listOfGods.size() == 2 ) {
                sendModelMessage(ModelMessageType.NeedsGodName,model.getCurrentPlayer().getName()+", choose your God between " + listOfGods.get(0) + " and " + listOfGods.get(1));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OtherChooses){

            if( listOfGods.size() == 1 ) {
                    sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName() + ", you have to choose " + listOfGods.get(0) + ".\n" + "Gods have been chosen.\n" + "We are now in the place part.\n" + "The youngest begins.\n");
            }
            else if( listOfGods.isEmpty() ) {
                    sendModelMessage(ModelMessageType.NeedsCoordinates, "Gods have been chosen.\n" + "We are now in the place part.\n" + "The youngest begins.\n");
            }

        }

    }

    void youngestPlayerTurnInizialization(){
        Player player = model.getPlayerList().get((model.getPlayerList().size()-3));

        //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
        model.getTurnMap().put(player, new Turn (player, Color.YELLOW, listOfGods.get(0)));
    }


    boolean chooseGods(PlayerMove message) {
        if(message.getType() != PlayerMoveType.GodName) {return false;}

        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        String Godname = message.getGenericMessage();
        Player player;

        //part where the younger player chooses a number of gods equal to the number of players
        if (godSetupPart == GodSetupPart.InitialChoice) {

            sendModelMessage(ModelMessageType.NeedsGodName, model.getCurrentPlayer().getName()+ " you are the youngest. Choose " + getGodChoiceTimes() + " Gods."+ Global.godsYouCanChoseFrom);


            //checking validity of the input
            if (checkGodExistence(message) ) {
                addSelectedGod(Godname);
            }

            //checking if the youngest player inserted all the gods for the match
            if (getModel().getPlayerList().size() == listOfGods.size()) {

                //setting the oldest player as current player
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-1));

                //informing users about the Gods they can choose
                informPlayersAboutGodChoice(getGodSetupPart());

                setGodSetupPart(GodSetupPart.OlderChooses);

            }
        }

        //the oldest player chooses his god
        else if (godSetupPart == GodSetupPart.OlderChooses){

            if (listOfGods.contains(Godname)) {

                player = model.getCurrentPlayer();

                //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
                model.getTurnMap().put(player, new Turn(player, Color.GREEN, Godname));
                listOfGods.remove(Godname);
                //now the first God has been chosen


                //setting an other current player
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-2));

                //informing users about the Gods they can choose
                informPlayersAboutGodChoice(getGodSetupPart());

                setGodSetupPart(GodSetupPart.OtherChooses);

            }
        }

        else if (godSetupPart == GodSetupPart.OtherChooses){


            if (listOfGods.contains(Godname)) {

                player = model.getCurrentPlayer();

                //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
                model.getTurnMap().put(player, new Turn(player, Color.RED, Godname));
                listOfGods.remove(Godname);

                //this happens when the match is 3 players type
                if (listOfGods.size() == 1) {
                    youngestPlayerTurnInizialization();
                }

                //setting the youngest player as current player
                model.setCurrentPlayer(model.getPlayerList().get(0));
                informPlayersAboutGodChoice(getGodSetupPart());

                return true;

            }

        }
        return false;
    }



    boolean performPlace(PlayerMove message) {

        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        //in this part the player will place worker A
        if (placePart == PlacePart.FirstPlacing) {

            sendModelMessage(ModelMessageType.NeedsCoordinates,  model.getCurrentPlayer().getName()+", place your worker A.");

            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "A")) {

                setPlacePart(PlacePart.SecondPlacing);
                sendModelMessage(ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+", place your worker B.");

            }

        }
        //in this part the player will place worker B
        else if (placePart == PlacePart.SecondPlacing) {

            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "B")) {

                //setting FirstPlacing as PlacePart to make the next player placing his workers
                setPlacePart(PlacePart.FirstPlacing);

                //updating the turn to make the other players place their workers
                updatingTurn();

                return true;
            }
        }

        return false;

    }

    //method for PerformTurn
    void checkIfGodNeedsConfirmation(TurnPart currentTurnPart){

        if(currentTurnPart == TurnPart.Move){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectMove(getCurrentPlayerGodName())) {
                    sendModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?");
            }
            else {
                    sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where to move.");
            }

        }
        else if(currentTurnPart == TurnPart.Build){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectBuild(getCurrentPlayerGodName())) {
                sendModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use your god's effect?");
            }
            else {
                sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", select where you want to build.");
            }

        }

    }


    void performTurn(PlayerMove message) {

        //if the player who gave input is not currentplayer, returns
        if (!model.isPlayerTurn(message.getPlayer())) {
            //eventuale notifica alla view
            return;
        }

        //in this part the player will select the worker to move
        if (turnPart == TurnPart.Select) {

            //if the player loses, i remove it and return
            if(!getCurrentPlayerTurn().checkIfCanMove(getGameBoard())){

                getCurrentPlayerTurn().clearBoard(getGameBoard());
                updatePlayersAfterLosing();
                return;

            }


            sendModelMessage( ModelMessageType.NeedsCoordinates,model.getCurrentPlayer().getName()+ ", select the worker to move.");

            if (getCurrentPlayerTurn().selectWorker(getGameBoard(), message)) {

                setTurnPart(TurnPart.Move);

                checkIfGodNeedsConfirmation(getTurnPart());
            }

        }

        //in this part the player will move the selected worker
        else if (turnPart == TurnPart.Move) {

            if (getCurrentPlayerTurn().move(getGameBoard(), message)) {

                //checks if the player wins after the move
                if (getCurrentPlayerTurn().isWinner()) {

                    //if he wins, the game is ended
                    setGamePart(GamePart.Conclusion);
                    sendModelMessage(ModelMessageType.GameOver, "Game over.");
                    return;

                }

                //if the player didn't win after the move, he has to build with the selected worker
                setTurnPart(TurnPart.Build);

                checkIfGodNeedsConfirmation(getTurnPart());

            }

        //in this part the player will build with the selected worker
        } else if (turnPart == TurnPart.Build) {

            //if the player cannot build, he's removed from game
            if(!getCurrentPlayerTurn().checkIfCanBuild(getGameBoard())) {
                getCurrentPlayerTurn().clearBoard(getGameBoard());
                updatePlayersAfterLosing();
                return;
            }

            if (getCurrentPlayerTurn().build(getGameBoard(), message)) {

                //Parte di Chrono, da cambiare (?)
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////

                for(Player p : model.getPlayerList()){
                    if(GodLookUpTable.isEffectOnOpponent(model.getTurnMap().get(p).getDivinityCard().getSpecificGodName())){
                        model.getTurnMap().get(p).getDivinityCard().activateEffect(getGameBoard(), getCurrentPlayerTurn(), message);

                        if(model.getTurnMap().get(p).isWinner()) {
                            setGamePart(GamePart.Conclusion);
                            sendModelMessage(ModelMessageType.GameOver, "Game over.");
                            return;
                        }

                    }
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////77

                //setting TurnPart as Select to make the next player selecting his worker
                setTurnPart(TurnPart.Select);
                updatingTurn();

                //sending message to the next player
                sendModelMessage(ModelMessageType.NeedsCoordinates, model.getCurrentPlayer().getName() + ", it's your turn, select the worker to move.");
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
                //sendModelMessage(ModelMessageType.NeedsCoordinates, "we're in the Place Part, the youngest begins");
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




