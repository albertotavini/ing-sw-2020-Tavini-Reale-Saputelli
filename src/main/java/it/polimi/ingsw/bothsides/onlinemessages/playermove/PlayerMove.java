package it.polimi.ingsw.bothsides.onlinemessages.playermove;
import it.polimi.ingsw.server.model.Player;

import java.io.Serializable;

/**
 * this is the input inserted by the player that will be sent to the remoteview, which will send it to the controller
 * the controller will then parse it and eventually use to modify model
 * it comes in three versions related to the three different possible types of input
 *
 * player is not final because it is set on the corresponding remoteview and not on the client
 */
public class PlayerMove implements Serializable {


    //message tags
    private final PlayerMoveType type;

    private final ConfirmationEnum confirm;
    private final int row;
    private final int column;
    private final String genericMessage;
    //this element is not final because it is not set on the client but on the corresponding remote view on the server
    private Player player;

    /**
     * constructor to send a pair of coordinates
     * @param row row chosen
     * @param column column chosen
     * @param player  who sent
     */
    private PlayerMove(int row, int column, Player player) {
        this.type = PlayerMoveType.COORD;
        this.row = row;
        this.column = column;
        this.player = player;
        this.genericMessage = null;
        this.confirm = ConfirmationEnum.NOTDEF;
    }


    /**
     * constructor to send a text message
     * @param string can be used for any message, but is used to set gods which are identified by their name
     * @param player who sent
     */
    private PlayerMove(String string, Player player) {
        this.type = PlayerMoveType.GOD_NAME;
        this.player = player;
        this.column = 0;
        this.row = 0;
        this.genericMessage = string;
        this.confirm = ConfirmationEnum.NOTDEF;
    }

    /**
     * constructor to send a confirmation
     * @param confirmation will to use or not use god's effect
     * @param player who sent
     */
    private PlayerMove(ConfirmationEnum confirmation, Player player) {
        this.type = PlayerMoveType.CONFIRM;
        this.player = player;
        this.column = 0;
        this.row = 0;
        this.genericMessage = null;
        this.confirm = confirmation;
    }

    /**
     * constructor to send a message to GUI's chat
     * @param chatMessage sent
     */
    private PlayerMove(String chatMessage) {

        this.type = PlayerMoveType.CHAT_MESSAGE;
        this.player = null;
        this.column = 0;
        this.row = 0;
        this.genericMessage = chatMessage;
        this.confirm = null;

    }

    private PlayerMove() {

        this.type = PlayerMoveType.KILL_IN_GAME_CONNECTION;
        this.player = null;
        this.column = 0;
        this.row = 0;
        this.genericMessage = null;
        this.confirm = null;


    }


    /**
     * @param row where you want to act
     * @param column where you want to act
     * @param player sending
     * @return playermove built
     */
    public static PlayerMove buildCoordPlayerMove(int row, int column, Player player) {
        return new PlayerMove(row, column, player);
    }

    /**
     * @param confirm that you want to give
     * @param player sending
     * @return playermove built
     */
    public static PlayerMove buildConfirmPlayerMove (ConfirmationEnum confirm, Player player) {
        return new PlayerMove(confirm, player);
    }

    /**
     * @param string that will be sent
     * @param player that sends
     * @return playermove built
     */
    public static PlayerMove buildStringPlayerMove (String string, Player player) {
        return new PlayerMove(string, player);
    }

    /**
     * @param message for the chat
     * @return playermove built
     */
    public static PlayerMove buildChatPlayerMove (String message) {
        return new PlayerMove(message);
    }

    public static PlayerMove buildKillerPlayerMove(){return new PlayerMove();}



    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getGenericMessage() {
        return genericMessage;
    }

    public ConfirmationEnum getConfirmation () {
        return confirm;
    }


    public PlayerMoveType getType() {
        return type;
    }

    @Override
    public String toString() {
       if (type == PlayerMoveType.GOD_NAME) return "tipo Godname, contenuto "+getGenericMessage();
       else if (type == PlayerMoveType.CONFIRM) return "tipo Confirm, contenuto "+getConfirmation();
       else return "tipo coord, contenuto "+getRow()+","+getColumn();
    }
}
