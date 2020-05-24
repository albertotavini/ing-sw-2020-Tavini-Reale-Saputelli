package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;


public class TestMain {




    public static void main(String[] args) {





        MenuGui menuGui = new MenuGui();

        /*System.out.println(menuGui.askForName());

        System.out.println(menuGui.askForDate().toString());

        MenuMessage create = menuGui.askForInfoToCreateLobby("spinny");

        System.out.println(create.getLobbyName() +"\n" +create.getNumberOfPlayers() +"\n" +create.getLobbyPassword());


        System.out.println("puerco");*/
        menuGui.askForInfoToCreateLobby("poppo");
        //menuGui.askForInfoToParticipateLobby(false, "peppe");




    }



}
