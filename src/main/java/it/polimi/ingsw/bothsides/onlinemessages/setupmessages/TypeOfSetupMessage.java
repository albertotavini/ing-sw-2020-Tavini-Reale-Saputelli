package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;


/**
 * this enum will tell the type of setup message to allow to understand which kind of lobby the player wants join or create
 */
public enum TypeOfSetupMessage implements Serializable {


    FAIL,
    SET_NAME_STATE_COMPLETED,
    CREATE_OR_PARTICIPATE_STATE_COMPLETED,
    WAITING_IN_LOBBY_STATE_COMPLETED,


    GO_BACK_FSM,


    //messages for create or partecipate
    SET_PLAYER_NAME_AND_BIRTHDAY,
    CHOOSE_CREATE_LOBBY_PUBLIC,
    CHOOSE_CREATE_LOBBY_PRIVATE,
    CHOOSE_PARTECIPATE_LOBBY_PUBLIC,
    CHOOSE_PARTECIPATE_LOBBY_PRIVATE,
    CHOOSE_LOBBY_CASUAL,
    CHOOSE_PARTECIPATE_CAN_JUMP_TO_IN_GAME_STATE,
    WAITING_IN_LOBBY_DISCONNECTED,
    WAITING_IN_LOBBY_PLAYER_JOINED,
    WAITING_IN_LOBBY_PLAYER_DISCONNECTED,
    PING_AND_ERROR_MESSAGE_PING,
    PING_AND_ERROR_MESSAGE_CLOSING,
    PING_AND_ERROR_MESSAGE_CLOSING_ACK,
    FINAL_STATE_MESSAGE_OFFER,
    FINAL_STATE_MESSAGE_ANSWER

}
