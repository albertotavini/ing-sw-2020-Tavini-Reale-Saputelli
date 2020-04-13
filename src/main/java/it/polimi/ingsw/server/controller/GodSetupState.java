package it.polimi.ingsw.server.controller;

public interface GodSetupState {

}

//every state is singleton
class InitialChoice implements GodSetupState {

    private static InitialChoice instance = new InitialChoice();

    public static InitialChoice getInstance(){ return instance; }

}

class OlderChooses implements GodSetupState {

    private static OlderChooses instance = new OlderChooses();

    public static OlderChooses getInstance(){ return instance; }

}

class OtherChooses implements GodSetupState {

    private static OtherChooses instance = new OtherChooses();

    public static OtherChooses getInstance(){ return instance; }

}


