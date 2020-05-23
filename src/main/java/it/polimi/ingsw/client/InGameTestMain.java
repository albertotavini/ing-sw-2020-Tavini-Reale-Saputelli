package it.polimi.ingsw.client;

public class InGameTestMain {

    public static void main(String[] args){

        InGameGui GUI = new InGameGui();

        GUI.setInGameGuiVisible(true);
        GUI.printInGameMessage("sonia");
        GUI.printSecondaryInGameMessage("raffi");


    }

}
