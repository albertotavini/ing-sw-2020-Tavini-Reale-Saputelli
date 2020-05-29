package it.polimi.ingsw.client;


import com.formdev.flatlaf.FlatDarculaLaf;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;


public class TestMain {




    public static void main(String[] args) {



        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        MenuGui menuGui = new MenuGui();

        MenuGui.setWaitInLobby();

        /*System.out.println(menuGui.askForName());

        System.out.println(menuGui.askForDate().toString());

        MenuMessage create = menuGui.askForInfoToCreateLobby("spinny");

        System.out.println(create.getLobbyName() +"\n" +create.getNumberOfPlayers() +"\n" +create.getLobbyPassword());*/


        System.out.println("puerco");
        //menuGui.askForInfoToCreateLobby("poppo");
        //menuGui.askForInfoToParticipateLobby(false, "peppe");




    }



}
