package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.server.controller.enums.GamePart;
import it.polimi.ingsw.server.controller.enums.GodSetupPart;
import it.polimi.ingsw.server.controller.enums.PlacePart;
import it.polimi.ingsw.server.controller.enums.TurnPart;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.view.ViewOffline;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMoveType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Controller implements Observer<PlayerMove> {

    private final ViewOffline viewOffline;
    private final Model model;
    private TurnPart turnPart;
    private GamePart gamePart;
    private PlacePart placePart;
    private GodSetupPart godSetupPart;


    private int godChoiceTimes;
    private ArrayList <String> listOfGods = new ArrayList<>();

    public Controller(Model model, ViewOffline viewOffline) {
        this.viewOffline = viewOffline;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        turnPart = TurnPart.SELECT;
        placePart = PlacePart.FIRST;
        gamePart = GamePart.GOD;
        godSetupPart = GodSetupPart.INITIALCHOICE;
        godChoiceTimes = model.getPlayerList().size();
    }

    public Controller(Model model) {
        this.viewOffline = null;
        this.model = model;
        model.setCurrentPlayer(model.getPlayerList().get(0));
        turnPart = TurnPart.SELECT;
        placePart = PlacePart.FIRST;
        gamePart = GamePart.GOD;
        godSetupPart = GodSetupPart.INITIALCHOICE;
        godChoiceTimes = model.getPlayerList().size();
    }


    //"utilities" methods
    public Model getModel() { return model; }

    public ViewOffline getViewOffline() {return viewOffline; }

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



    public void initialMessage(){
        model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.WAIT, ModelError.NONE, "Welcome to Santorini", true, " "));
        model.informView();
        sendModelMessage(ModelMessageType.GODNAME, "You're the youngest select " +godChoiceTimes +" gods");
        model.informView();
    }
    void sendModelMessage(ModelMessageType modelMessageType, String message){
        model.getGameboard().setModelMessage( new ModelMessage(modelMessageType, message) );
        model.getGameboard().setModelMessage(model.getGameboard().getModelMessage().copyAndAddPlayer(model.getCurrentPlayer().getName()));
    }
    void updatingTurn(){
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
        boolean exists =  ( GodLookUpTable.lookUp(message.getGenericMessage()) != null );
        if (!exists) {
            getGameBoard().setModelMessage(getGameBoard().getModelMessage().copyAndAddError(ModelError.INCORRECTGODNAME));
        }
        return exists;
    }

    void addSelectedGod(String godname) {

        //parse of the string is done outside this method
        //checks if there is already an occurrence of that god in the list, if not it adds it
        String sameGod = listOfGods.stream().filter(s -> s.equals(godname)).collect(Collectors.joining());
        if (sameGod.equals("")) {
            listOfGods.add(godname);
            setGodChoiceTimes(getGodChoiceTimes() - 1);
            sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + ", you have chosen " + godname + ". Remaining Gods are " + getGodChoiceTimes() + "." + Global.GODS_YOU_CAN_CHOSE_FROM);
        }

    }

    //informing users about the Gods they can choose depending on the current GodSetupPart
    void informPlayersAboutGodChoice(GodSetupPart currentGodSetupPart){

        if(currentGodSetupPart == GodSetupPart.INITIALCHOICE) {

            if (listOfGods.size() == 2) {
                sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.CHOOSEYOURGOD + listOfGods.get(0) + Global.AND + listOfGods.get(1));

            } else if (listOfGods.size() == 3) {
                sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.CHOOSEYOURGOD + listOfGods.get(0) + ", " + listOfGods.get(1) + Global.AND + listOfGods.get(2));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OLDERCHOOSES){

            if( listOfGods.size() == 1 ) {
                sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.YOUHAVETOCHOOSE + listOfGods.get(0) + ".");
            }
            else if( listOfGods.size() == 2 ) {
                sendModelMessage(ModelMessageType.GODNAME,model.getCurrentPlayer().getName()+ Global.CHOOSEYOURGOD + listOfGods.get(0) + Global.AND + listOfGods.get(1));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OTHERCHOOSES){

            if( listOfGods.size() == 1 ) {
                    sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.YOUHAVETOCHOOSE + listOfGods.get(0) + ".\n" + Global.GODSHAVEBEENCHOSEN);
            }
            else if( listOfGods.isEmpty() ) {
                    sendModelMessage(ModelMessageType.COORDINATES, Global.GODSHAVEBEENCHOSEN);
            }

        }

    }

    void youngestPlayerTurnInizialization(){
        Player player = model.getPlayerList().get((model.getPlayerList().size()-3));

        //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
        model.getTurnMap().put(player, new Turn (player, Color.YELLOW, listOfGods.get(0)));
        tellWhatGodHasBeenChose(listOfGods.get(0), player);
    }

    void checkIfOneWon() {
        for (Player p: model.getPlayerList()) {
            if (model.getTurnMap().get(p).isWinner()) {
                model.setCurrentPlayer(p);
                setGamePart(GamePart.CONCLUSION);
            }
        }
    }

    void tellWhatGodHasBeenChose(String godname, Player player){
        getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GODHASBEENCHOSEN, ModelError.NONE, godname, true, player.getName(), model.getTurnMap().get(player).getColor()));
        model.informView();
    }


    boolean chooseGods(PlayerMove message) {
        if(message.getType() != PlayerMoveType.GOD_NAME) {return false;}

        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        String godname = message.getGenericMessage();
        Player player;

        //part where the younger player chooses a number of gods equal to the number of players
        if (godSetupPart == GodSetupPart.INITIALCHOICE) {

            sendModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName()+ " you are the youngest. Choose " + getGodChoiceTimes() + " Gods."+ Global.GODS_YOU_CAN_CHOSE_FROM);


            //checking validity of the input
            if (checkGodExistence(message) ) {
                addSelectedGod(godname);
            }

            //checking if the youngest player inserted all the gods for the match
            if (getModel().getPlayerList().size() == listOfGods.size()) {

                //setting the oldest player as current player
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-1));

                //informing users about the Gods they can choose
                informPlayersAboutGodChoice(getGodSetupPart());

                setGodSetupPart(GodSetupPart.OLDERCHOOSES);

            }
        }

        //the oldest player chooses his god
        else if (godSetupPart == GodSetupPart.OLDERCHOOSES && listOfGods.contains(godname)){

                player = model.getCurrentPlayer();

                //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
                model.getTurnMap().put(player, new Turn(player, Color.GREEN, godname));
                tellWhatGodHasBeenChose(godname, player);
                listOfGods.remove(godname);
                //now the first God has been chosen


                //setting an other current player
                model.setCurrentPlayer(model.getPlayerList().get(model.getPlayerList().size()-2));

                //informing users about the Gods they can choose
                informPlayersAboutGodChoice(getGodSetupPart());

                setGodSetupPart(GodSetupPart.OTHERCHOOSES);

            }

        else if (godSetupPart == GodSetupPart.OTHERCHOOSES && listOfGods.contains(godname)){



                player = model.getCurrentPlayer();

                //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
                model.getTurnMap().put(player, new Turn(player, Color.RED, godname));
                tellWhatGodHasBeenChose(godname, player);
                listOfGods.remove(godname);

                //this happens when the match is 3 players type
                if (listOfGods.size() == 1) {
                    youngestPlayerTurnInizialization();
                }

                //setting the youngest player as current player
                model.setCurrentPlayer(model.getPlayerList().get(0));
                informPlayersAboutGodChoice(getGodSetupPart());

                return true;

        }
        else getGameBoard().setModelMessage(getGameBoard().getModelMessage().copyAndAddError(ModelError.INCORRECTGODNAME));

        return false;

    }



    boolean performPlace(PlayerMove message) {

        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        //in this part the player will place worker A
        if (placePart == PlacePart.FIRST) {

            sendModelMessage(ModelMessageType.COORDINATES,  model.getCurrentPlayer().getName()+", place your worker A.");

            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "A")) {

                setPlacePart(PlacePart.SECOND);
                sendModelMessage(ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+", place your worker B.");

            }

        }
        //in this part the player will place worker B
        else if (placePart == PlacePart.SECOND && getCurrentPlayerTurn().placeWorker(getGameBoard(), message, "B")) {

                //setting FirstPlacing as PlacePart to make the next player placing his workers
                setPlacePart(PlacePart.FIRST);

                //updating the turn to make the other players place their workers
                updatingTurn();

                return true;

        }
        return false;
    }

    //method for PerformTurn
    void checkIfGodNeedsConfirmation(TurnPart currentTurnPart){

        if(currentTurnPart == TurnPart.MOVE){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectMove(getCurrentPlayerGodName())) {
                    sendModelMessage(ModelMessageType.CONFIRMATION, "do you want to use your god's effect?");
            }
            else {
                    sendModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + ", select where to move.");
            }

        }
        else if(currentTurnPart == TurnPart.BUILD){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectBuild(getCurrentPlayerGodName())) {
                sendModelMessage(ModelMessageType.CONFIRMATION, "do you want to use your god's effect?");
            }
            else {
                sendModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + ", select where you want to build.");
            }

        }

    }

    boolean checkIfCanMove(){
        if(!getCurrentPlayerTurn().checkIfCanMove(getGameBoard())) {

            getCurrentPlayerTurn().clearBoard(getGameBoard());
            getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GAMEOVER, ModelError.NONE, "Your workers couldn't move, you lost", false, model.getCurrentPlayer().getName()));
            model.informView();
            updatePlayersAfterLosing();
            return false;
        }

        else { return true; }
    }

    boolean checkIfCanBuild(){
        if(!getCurrentPlayerTurn().checkIfCanBuild(getGameBoard())) {

            getCurrentPlayerTurn().clearBoard(getGameBoard());
            getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GAMEOVER, ModelError.NONE, "Your workers couldn't build, you lost", false, model.getCurrentPlayer().getName()));
            model.informView();
            updatePlayersAfterLosing();
            return false;

        }
        else { return true; }
    }

    void eventualOnOpponentEffect(PlayerMove message){
        for(Player p : model.getPlayerList()){
            if(GodLookUpTable.isEffectOnOpponent(model.getTurnMap().get(p).getDivinityCard().getSpecificGodName())){
                model.getTurnMap().get(p).getDivinityCard().activateEffect(getGameBoard(), getCurrentPlayerTurn(), message);
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
        if (turnPart == TurnPart.SELECT) {

            //if the player loses, i remove it and return
            if(!checkIfCanMove()){ return; }

            sendModelMessage( ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+ ", select the worker to move.");

            if (getCurrentPlayerTurn().selectWorker(getGameBoard(), message)) {

                setTurnPart(TurnPart.MOVE);

                checkIfGodNeedsConfirmation(getTurnPart());
            }

        }

        //in this part the player will move the selected worker
        else if (turnPart == TurnPart.MOVE) {

            if (getCurrentPlayerTurn().move(getGameBoard(), message)) {

                //if the player didn't win after the move, he has to build with the selected worker
                setTurnPart(TurnPart.BUILD);

                checkIfGodNeedsConfirmation(getTurnPart());

            }

        //in this part the player will build with the selected worker
        } else if (turnPart == TurnPart.BUILD) {

            //if the player cannot build, he's removed from game
            if(!checkIfCanBuild()){ return; }

            if (getCurrentPlayerTurn().build(getGameBoard(), message)) {

                //check if one of the players's God has an effect activating on the opponent's turn, like Chronus
                eventualOnOpponentEffect(message);

                //setting TurnPart as Select to make the next player selecting his worker
                setTurnPart(TurnPart.SELECT);
                updatingTurn();

                //sending message to the next player
                sendModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + ", it's your turn, select the worker to move.");
            }
        }
    }


    //method for update
    //updating place depending on the current placing gamePart
    void updatePlace(){

        if(getGamePart() == GamePart.PLACE1){

            setGamePart(GamePart.PLACE2);
            sendModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName()+", it's your turn to place");

        }

        else if(getGamePart() == GamePart.PLACE2){

            if (model.getPlayerList().size() == 2) {
                setGamePart(GamePart.TURN);
                model.setCurrentPlayer(model.getPlayerList().get(0));
                sendModelMessage(ModelMessageType.COORDINATES,"We're in the turn part. You start, " + model.getCurrentPlayer().getName());
            }
            if (model.getPlayerList().size() == 3) {
                setGamePart(GamePart.PLACE3);
                sendModelMessage(ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+", it's your turn to place ");
            }

        }

        else if(getGamePart() == GamePart.PLACE3){

            setGamePart(GamePart.TURN);
            sendModelMessage(ModelMessageType.COORDINATES,"We're in the turn part. You start "+model.getCurrentPlayer().getName());
            model.setCurrentPlayer(model.getPlayerList().get(0));

        }

    }

    @Override
    public synchronized void update(PlayerMove message, Object obj) {

        if (!message.getPlayer().equals(model.getCurrentPlayer())) {
            getGameBoard().setModelMessage(getGameBoard().getModelMessage().copyAndAddInfo(message.getPlayer().getName()+" it's not your turn, it's "+model.getCurrentPlayer().getName()+"'s"));
            model.informView();
        }

        if (model.checkIfOnePlayerRemains()) {
            setGamePart(GamePart.CONCLUSION);
        }

        //first selects the gods
        if (gamePart == GamePart.GOD) {
            if(chooseGods(message)) {
                setGamePart(GamePart.PLACE1);
                //sendModelMessage(ModelMessageType.NEEDSCOORDINATES, "we're in the Place Part, the youngest begins"); ERRATO, NON STAMPA CIO' CHE E' SCRITTO PRIMA
            }
        }

        //place for players
        else if (gamePart == GamePart.PLACE1 || gamePart == GamePart.PLACE2 || gamePart == GamePart.PLACE3) {
            if(performPlace(message)) {
                updatePlace();
            }
        }

        //iterates on the turns until one player winners
        else if (gamePart == GamePart.TURN){
            performTurn(message);
            checkIfOneWon();
        }



        if(gamePart == GamePart.CONCLUSION){
            model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.GAMEOVER, ModelError.NONE, "Game over : "+model.getCurrentPlayer()+" is the winner!", true, " "));
        }


        model.getGameboard().setModelMessage(model.getGameboard().getModelMessage().copyAndAddPlayer(model.getCurrentPlayer().getName()));
        model.informView();
    }

}






