package it.polimi.ingsw.server.controller;

public interface PlaceState {

}

//every state is singleton
class FirstPlacingState implements PlaceState {

    private static FirstPlacingState instance = new FirstPlacingState();

    public static FirstPlacingState getInstance(){
        return instance;
    }

}

class SecondPlacingState implements PlaceState {

    private static SecondPlacingState instance = new SecondPlacingState();

    public static SecondPlacingState getInstance(){
        return instance;
    }


}
