package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

public enum TypeOfSetupMessage implements Serializable {

    //le enum hanno una serializzazione tutta loro, andate a vedere come funziona


    //messaggi sia da parte server che da parte client
    FAIL,
    SET_NAME_STATE_COMPLETED,
    CREATE_OR_PARTICIPATE_STATE_COMPLETED,
    WAITING_IN_LOBBY_STATE_COMPLETED,


    //messaggi solo client
    GO_BACK_FSM,


    //messaggi create o partecipate lobby
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
