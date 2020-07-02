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

/**
 * this class processes PlayerMoves and utilises them to modify model, in particular Board
 * it works as a sort of State Machine
 * every moment it knows what is needed, and if an input which doesn't fit the current situation is received,
 * or from a player which is not the current one, it is simply discarded
 *
 */
public class Controller implements Observer<PlayerMove> {

    private final ViewOffline viewOffline;
    private final Model model;
    private TurnPart turnPart;
    private GamePart gamePart;
    private PlacePart placePart;
    private GodSetupPart godSetupPart;


    private int godChoiceTimes;
    private ArrayList <String> listOfGods = new ArrayList<>();

    /**
     * constructor used for offline usage and testing
     *
     * @param model model
     * @param viewOffline offline usage
     */
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

    /**
     * sets the current player as the first of the list (model will make sure it's the youngest)
     * sets the enums that regulate the "state" of the controller to their first part
     * sets an int that helps in the choice of gods to the number of players playing
     * @param model that will be modified
     */
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


    /**
     * method used to send a first "welcome" notify to the remote views and to tell
     * the youngest player he has to choose the gods that will be used int the game
     */
    public void initialMessage(){
        model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.WAIT, ModelError.NONE, Global.WELCOME, Global.SPACE));
        model.informView();
        defineModelMessage(ModelMessageType.GODNAME, Global.YOUARETHEYOUNGESTSELECT +godChoiceTimes +Global.SPACE+Global.GODS);
        model.informView();
    }

    /**
     * this method will modify the modelMessage object which is held in the Board so that it can be modified also by
     * god's powers, which have no access to controller and model
     * (modelMessage it is sent only when model.informView() is called)
     *
     *
     * @param modelMessageType to be inserted
     * @param message info to be shown
     */
    void defineModelMessage(ModelMessageType modelMessageType, String message){
        model.getGameboard().setModelMessage( new ModelMessage(modelMessageType, message) );
        model.getGameboard().setModelMessage(model.getGameboard().getModelMessage().copyAndAddPlayer(model.getCurrentPlayer().getName()));
    }

    /**
     * method that changes the current player with the next one
     */
    void updatingTurn(){
        model.updateTurn();
    }

    /**
     * removes the current player and readjusts the list of players
     */
    private void updatePlayersAfterLosing(){
        model.updatePlayersAfterLosing();
    }

    /**
     * @return turn of the current player
     */
    private Turn getCurrentPlayerTurn(){
        return model.getTurnMap().get(model.getCurrentPlayer());
    }

    /**
     * @return the name of the god held by the currentplayer
     */
    private String getCurrentPlayerGodName(){
        return getCurrentPlayerTurn().getDivinityCard().getSpecificGodName();
    }

    /**
     * @return the object board from mdoel
     */
    private Board getGameBoard(){
        return model.getGameboard();
    }


    /**
     * @param message the input received from currentplayer
     * @return true if the message in there contains the name of one of the gods
     */
    boolean checkGodExistence(PlayerMove message){
        boolean exists =  ( GodLookUpTable.lookUp(message.getGenericMessage()) != null );
        if (!exists) {
            getGameBoard().setModelMessage(getGameBoard().getModelMessage().copyAndAddError(ModelError.INCORRECTGODNAME));
        }
        return exists;
    }

    /**
     * parse of the string is done outside this method
     * checks if there is already an occurrence of that god in the list (god already chosen), if not it adds it
     * @param godname string in the playermove received
     */
    void addSelectedGod(String godname) {


        String sameGod = listOfGods.stream().filter(s -> s.equals(godname)).collect(Collectors.joining());
        if (sameGod.equals("")) {
            listOfGods.add(godname);
            setGodChoiceTimes(getGodChoiceTimes() - 1);
            defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.YOUHAVECHOSEN + godname +  Global.REMAININGGODSARE  + getGodChoiceTimes() + Global.DOT);
        }

    }

    /**
     * the method handles the definition of ModelMessages during the phase of god's choice
     *
     * @param currentGodSetupPart part of the phase of choosing gods in which the game is
     */

    void informPlayersAboutGodChoice(GodSetupPart currentGodSetupPart){

        if(currentGodSetupPart == GodSetupPart.INITIALCHOICE) {

            if (listOfGods.size() == 2) {
                defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.CHOOSEYOURGOD + listOfGods.get(0) + Global.AND + listOfGods.get(1));

            } else if (listOfGods.size() == 3) {
                defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.CHOOSEYOURGOD + listOfGods.get(0) + Global.COMMA + listOfGods.get(1) + Global.AND + listOfGods.get(2));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OLDERCHOOSES){

            if( listOfGods.size() == 1 ) {
                defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.YOUHAVETOCHOOSE + listOfGods.get(0) + Global.DOT);
            }
            else if( listOfGods.size() == 2 ) {
                defineModelMessage(ModelMessageType.GODNAME,model.getCurrentPlayer().getName()+ Global.CHOOSEYOURGOD + listOfGods.get(0) + Global.AND + listOfGods.get(1));
            }

        }


        else if(currentGodSetupPart == GodSetupPart.OTHERCHOOSES){

            if( listOfGods.size() == 1 ) {
                    defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName() + Global.YOUHAVETOCHOOSE + listOfGods.get(0) + Global.BACKSLASHN + Global.GODSHAVEBEENCHOSEN);
            }
            else if( listOfGods.isEmpty() ) {
                    defineModelMessage(ModelMessageType.COORDINATES, Global.GODSHAVEBEENCHOSEN);
            }

        }

    }

    /**
     * this method is called in a three player match and automatically sets the god and creates the turn for the youngest player
     * which is the one who will not get to pick which god but will be left with the remaining one
     */
    void youngestPlayerTurnInizialization(){
        Player player = model.getPlayerList().get((model.getPlayerList().size()-3));

        //initializing player's turn with his chosen God, then removing that God from the list of chosen Gods
        model.getTurnMap().put(player, new Turn (player, Color.YELLOW, listOfGods.get(0)));
        tellWhatGodHasBeenChose(listOfGods.get(0), player);
    }

    /**
     * this method is used to control if one the players in game has turn.winner set to true,
     * meaning he has reached one of the conditions that make him win the game
     * and consequently sets GamePart to CONCLUSION
     */
    void checkIfOneWon() {
        for (Player p: model.getPlayerList()) {
            if (model.getTurnMap().get(p).isWinner()) {
                model.setCurrentPlayer(p);
                setGamePart(GamePart.CONCLUSION);
            }
        }
    }

    /**
     * this method is used to notify to the remoteviews what gods have been chosen
     * in particular it is useful to allow the GUIs to display which player has which god
     *
     *
     * @param godname that has been chosen
     * @param player current player passed from method calling
     */
    void tellWhatGodHasBeenChose(String godname, Player player){
        getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.GODHASBEENCHOSEN, ModelError.NONE, godname, player.getName(), model.getTurnMap().get(player).getColor()));
        model.informView();
    }


    /**
     * this method is called from update while GamePart is GOD
     * it will filter the different playermoves to allow the gods choice
     * another enum will regulate is functioning, GodSetupPart
     * when in .INITIALCHOICE it will expect a number of godnames from the youngest player which is equivalent to the number of players in the game
     * when it has received them it will set GodSetupPart to .OLDERCHOOSES
     * in this part, it will expect a name among the 2/3 chosen from the oldest player, who is the first one to pick
     * after receiving correct input it will set GodSetupPart to .OTHERCHOOSES
     * that works differently related to the number of players in the game
     * TWO: it will expect the name of the remaining god from the youngest player, and after receiving true will end the procedure by returning true
     * THREE: it will expect the name of one of the two remaining gods from the player whose age is "in the middle" and after it has been received, it will
     * set the remaining god to the third and youngest player and will end the procedure by returning true;
     *
     * the method will immediately return false if an input which is not PlayerMoveType.GodName is received
     *
     *
     * @param message playermove received
     * @return true only when the entire setting of gods has been completed
     */
    boolean chooseGods(PlayerMove message) {
        if(message.getType() != PlayerMoveType.GOD_NAME) {return false;}

        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        String godname = message.getGenericMessage();
        Player player;

        //part where the younger player chooses a number of gods equal to the number of players
        if (godSetupPart == GodSetupPart.INITIALCHOICE) {

            defineModelMessage(ModelMessageType.GODNAME, model.getCurrentPlayer().getName()+ Global.SPACE+Global.YOUARETHEYOUNGESTSELECT + getGodChoiceTimes() + Global.GODS/*+ Global.GODS_YOU_CAN_CHOSE_FROM*/);


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


    /**
     * the method is called by update when GamePart is in one of the three PLACEPART
     * it will wait coordinate input from the currentplayer to place its workers at the beginning of the game
     * if PlacePart is FIRST it will know that it is placing the first worker for that player
     * after getting an input that is correct it will update the enum to SECOND
     * in that part it will waiting for a feasibile position to place the second worker
     * when a playermove with a correct input will be received the method will return true, comunicating Update that this player has placed its workers
     *
     * the method will immediately return false if an input which is not PlayerMoveType.Coordinates is received
     *
     * @param message playermove received, presumably containing coordinates
     * @return true if current player has placed his workers successfully
     */
    boolean performPlace(PlayerMove message) {

        //if the player is not the current one, doesn't consider the input given
        if (!model.isPlayerTurn(message.getPlayer())) {
            return false;
        }

        //in this part the player will place worker A
        if (placePart == PlacePart.FIRST) {

            defineModelMessage(ModelMessageType.COORDINATES,  model.getCurrentPlayer().getName()+Global.PLACEWORKERA);

            if (getCurrentPlayerTurn().placeWorker(getGameBoard(), message, Global.A)) {

                setPlacePart(PlacePart.SECOND);
                defineModelMessage(ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+Global.PLACEWORKERB);

            }

        }
        //in this part the player will place worker B
        else if (placePart == PlacePart.SECOND && getCurrentPlayerTurn().placeWorker(getGameBoard(), message, Global.B)) {

                //setting FirstPlacing as PlacePart to make the next player placing his workers
                setPlacePart(PlacePart.FIRST);

                //updating the turn to make the other players place their workers
                updatingTurn();

                return true;

        }
        return false;
    }

    /**
     * this method is used to control if the player who is gonna move or build needs a confirmation at the beginning of the act of move/build for it's god's effect
     *it will set the ModelMessage with the proper type basing on the control, so that client will be able to insert correct input
     *
     * @param currentTurnPart the part of the current turn we are in
     */
    //method for PerformTurn
    void checkIfGodNeedsConfirmation(TurnPart currentTurnPart){

        if(currentTurnPart == TurnPart.MOVE){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectMove(getCurrentPlayerGodName())) {
                    defineModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT);
            }
            else {
                    defineModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + Global.SELECTWHERETOMOVE);
            }

        }
        else if(currentTurnPart == TurnPart.BUILD){

            if (GodLookUpTable.isEffectNeedConfirmation(getCurrentPlayerGodName()) && GodLookUpTable.isEffectBuild(getCurrentPlayerGodName())) {
                defineModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT);
            }
            else {
                defineModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + Global.SELECTWHERETOBUILD);
            }

        }

    }

    /**
     * this method calls turn.checkIfCanMove() and if the player cannot move, it will remove its workers from the board and tell him he has lost
     * then will call the method that adjusts the currentPlayer and the list of players
     *
     * @return true if the player can move, false if not and has lost the game
     */
    boolean checkIfCanMove(){
        if(!getCurrentPlayerTurn().checkIfCanMove(getGameBoard())) {

            getCurrentPlayerTurn().clearBoard(getGameBoard());
            getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.YOULOST, ModelError.NONE, Global.WORKERSCANTMOVE, model.getCurrentPlayer().getName()));
            model.informView();
            updatePlayersAfterLosing();
            return false;
        }

        else { return true; }
    }

     /**
     * this method calls turn.checkIfCanBuild() and if the player cannot build, it will remove its workers from the board and tell him he has lost
     * then will call the method that adjusts the currentPlayer and the list of players
     *
     * @return true if the player can move, false if not and has lost the game
     */
    boolean checkIfCanBuild(){
        if(!getCurrentPlayerTurn().checkIfCanBuild(getGameBoard())) {

            getCurrentPlayerTurn().clearBoard(getGameBoard());
            getGameBoard().setModelMessage(new ModelMessage(ModelMessageType.YOULOST, ModelError.NONE, Global.WORKERSCANTBUILD, model.getCurrentPlayer().getName()));
            model.informView();
            updatePlayersAfterLosing();
            return false;

        }
        else { return true; }
    }

    /**
     * method that eventually checks if one of the players has a god with an OnOpponent type of effect and if yes, it activates it
     *
     * @param message playermove received
     */
    void eventualOnOpponentEffect(PlayerMove message){
        for(Player p : model.getPlayerList()){
            if(GodLookUpTable.isEffectOnOpponent(model.getTurnMap().get(p).getDivinityCard().getSpecificGodName())){
                model.getTurnMap().get(p).getDivinityCard().activateEffect(getGameBoard(), getCurrentPlayerTurn(), message);
            }
        }
    }

    /**
     * this method will be called every time a turn needs to happen
     * it returns void contrary to perfomPlace and ChooseGods because the game will continue to call it iterating on players until one wins and GamePart is set to CONCLUSION
     * the enum that regulates its functioning is TurnPart
     * the first playermove received is used in .SELECTWORKER to chose the worker with which the current player is gonna move and build
     * when a correct input is received TurnPart will be set to .MOVE and the controller will wait for a pair of coordinates to be used to move the chosen worker
     * when correct input is given the player will be moved and TurnPart will be set to .BUILD
     * next input will be used to let that worker build
     * when correct input is given, the worker will build and the currentPlayer will switch to the next one, while TurnPart is brought back to SELECTWORKER
     *
     * the method immediately returns if the playermove processed is not from currentPlayer
     *
     * if the build or move act is composed of different parts (example is prometheus effect) the corresponding part will modify the board multiple times before switching TurnPart
     *
     * @param message playermove received
     */
    void performTurn(PlayerMove message) {




/*


        //backdooorrrrrrrrr
        if(message.getRow() == 0 && message.getColumn() == 0) {


            getCurrentPlayerTurn().setWinner(true);

        }


*/



        //if the player who gave input is not currentplayer, returns
        if (!model.isPlayerTurn(message.getPlayer())) {
            //eventuale notifica alla view
            return;
        }

        //in this part the player will select the worker to move
        if (turnPart == TurnPart.SELECT) {

            //if the player loses, i remove it and return
            if(!checkIfCanMove()){ return; }

            defineModelMessage( ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+ Global.SELECTWORKER);

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
                defineModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName() + Global.SELECTWORKER);
            }
        }
    }


    /**
     * method that basing on the number of players and the current GamePart knows which of the three PLACE needs to be set,
     * or if it is time to switch to TURN
     */
    void updatePlace(){

        if(getGamePart() == GamePart.PLACE1){

            setGamePart(GamePart.PLACE2);
            defineModelMessage(ModelMessageType.COORDINATES, model.getCurrentPlayer().getName()+ Global.PLACEWORKERA);

        }

        else if(getGamePart() == GamePart.PLACE2){

            if (model.getPlayerList().size() == 2) {
                setGamePart(GamePart.TURN);
                model.setCurrentPlayer(model.getPlayerList().get(0));
                defineModelMessage(ModelMessageType.COORDINATES,Global.YOUSTART + model.getCurrentPlayer().getName());
            }
            if (model.getPlayerList().size() == 3) {
                setGamePart(GamePart.PLACE3);
                defineModelMessage(ModelMessageType.COORDINATES,model.getCurrentPlayer().getName()+Global.PLACEWORKERA);
            }

        }

        else if(getGamePart() == GamePart.PLACE3){

            setGamePart(GamePart.TURN);
            defineModelMessage(ModelMessageType.COORDINATES,Global.YOUSTART +model.getCurrentPlayer().getName());
            model.setCurrentPlayer(model.getPlayerList().get(0));

        }

    }

    /**
     * the update processes input through the game calling methods basing on which GamePart we are in
     * if we are in .GOD it will call chooseGods
     * once that method returns true it will switch to PLACE1
     * and playermoves will be passed to perform place
     * when this returns true it will switch to PLACE2
     * and when perform place returns true it will switch to PLACE3 (and repeat the above) if there is a third player
     * then it sets TURN
     * while in this part it will continue to call performTurn until one of the player wins and GamePart is set to CONCLUSION
     * where the partecipants are informed that the game as ended
     *
     * after one player move has been processed, it adds the currentplayer to modelMessage and calls model.InformVIew()
     *
     *
     * @param message playermove received from one of the remoteview
     * @param obj is always null
     */
    @Override
    public synchronized void update(PlayerMove message, Object obj) {

        if (!message.getPlayer().equals(model.getCurrentPlayer())) {
            getGameBoard().setModelMessage(getGameBoard().getModelMessage().copyAndAddInfo(message.getPlayer().getName() + Global.NOTYOURTURN + model.getCurrentPlayer().getName()));
            model.informView();
        }

        if (model.checkIfOnePlayerRemains()) {
            setGamePart(GamePart.CONCLUSION);
        }

        //first selects the gods
        if (gamePart == GamePart.GOD) {
            if(chooseGods(message)) {
                setGamePart(GamePart.PLACE1);
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


        //when game ends sends the correct message
        if(gamePart == GamePart.CONCLUSION){
            model.getGameboard().setModelMessage(new ModelMessage(ModelMessageType.GAMEOVER, ModelError.NONE, Global.GAMEOVER + model.getCurrentPlayer()+ Global.COLOR+model.getTurnMap().get(model.getCurrentPlayer()).getColor()+Global.ISTHEWINNER, Global.SPACE ));
        }


        model.getGameboard().setModelMessage(model.getGameboard().getModelMessage().copyAndAddPlayer(model.getCurrentPlayer().getName()));
        model.informView();
    }

}






