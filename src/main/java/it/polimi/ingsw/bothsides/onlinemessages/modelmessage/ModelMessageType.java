package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

/**
 * and enum to describe what is happening to the game
 * coordinates, confirmation and godname tell the next type of input needed
 * gameover tells that somebody has one and game has ended
 * youlost informs that the player suggested in the ModelMessage has lost
 * godhasbeenchosen is used to show images of gods of players (in particular for GUI
 * wait is used for message that convey information
 * chat_message is used for gui's chat
 * disconnected is used when something goes wrong in lobby or in the server
 *
 */
public enum ModelMessageType {
    GAMEOVER,
    YOULOST,
    COORDINATES,
    CONFIRMATION,
    GODNAME,
    GODHASBEENCHOSEN,
    DISCONNECTED,
    WAIT,
    CHAT_MESSAGE
}
