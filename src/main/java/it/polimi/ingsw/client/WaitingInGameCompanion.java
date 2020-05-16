package it.polimi.ingsw.client;


interface WaitingInGameCompanion{}

class WaitingInGameCompanionCli implements Runnable, WaitingInGameCompanion{


    @Override
    public void run() {

    }

}


class WaitingInLobbyCompanionGui implements Runnable, WaitingInGameCompanion{


    @Override
    public void run() {

    }

}
