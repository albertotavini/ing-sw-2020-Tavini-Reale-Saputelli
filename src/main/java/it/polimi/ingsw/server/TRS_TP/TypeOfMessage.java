package it.polimi.ingsw.server.TRS_TP;

import java.io.Serializable;

public enum TypeOfMessage implements Serializable {

    //le enum hanno una serializzazione tutta loro, andate a vedere come funziona


    //messaggi sia da parte server che da parte client
    Fail,
    SetNameStateCompleted,
    CreateOrParticipateStateCompleted,
    WaitingInLobbyStateCompleted,


    //messaggi solo client
    GoBackFsm,


    //messaggi create o partecipate lobby
    SetPlayerNameAndBirthday,
    ChooseCreateLobbyPublic,
    ChooseCreateLobbyPrivate,
    ChoosePartecipateLobbyPublic,
    ChoosePartecipateLobbyPrivate,
    ChooseLobbyCasual,
    ChoosePartecipateCanJumpToInGameState,
    WaitingInLobbyDisconnected,
    WaitingInLobbyPlayerJoined,
    WaitingInLobbyPlayerDisconnected,
    PingAndErrorMessagePing,
    PingAndErrorMessageError

}
