package it.polimi.ingsw.server.controller;

public interface TurnState {

}

//every state is singleton
class SelectionState implements TurnState {

    private static SelectionState instance = new SelectionState();

    public static SelectionState getInstance(){
        return instance;
    }

}

class MoveState implements TurnState {

    private static MoveState instance = new MoveState();

    public static MoveState getInstance(){
        return instance;
    }

}

class BuildState implements TurnState {

    private static BuildState instance = new BuildState();

    public static BuildState getInstance() {
        return instance;
    }

}


