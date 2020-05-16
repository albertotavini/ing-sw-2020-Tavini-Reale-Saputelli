package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

public enum TypeOfSetupMessage implements Serializable {

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
