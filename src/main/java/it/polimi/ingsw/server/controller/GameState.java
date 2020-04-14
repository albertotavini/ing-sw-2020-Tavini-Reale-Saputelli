package it.polimi.ingsw.server.controller;

public interface GameState {

}

//every state is singleton
class GodPart implements GameState{

    private static GodPart instance = new GodPart();

    public static GodPart getInstance(){
        return instance;
    }

}


class PlacePart1 implements GameState{

    private static PlacePart1 instance = new PlacePart1();

    public static PlacePart1 getInstance(){
        return instance;
    }

}


class PlacePart2 implements GameState{

    private static PlacePart2 instance = new PlacePart2();

    public static PlacePart2 getInstance(){
        return instance;
    }

}


class PlacePart3 implements GameState{

    private static PlacePart3 instance = new PlacePart3();

    public static PlacePart3 getInstance(){ return instance; }

}


class TurnPart implements GameState{

    private static TurnPart instance = new TurnPart();

    public static TurnPart getInstance(){ return instance; }

}


class WinnerPart implements GameState{

    private static WinnerPart instance = new WinnerPart();

    public static WinnerPart getInstance(){ return instance; }

}
