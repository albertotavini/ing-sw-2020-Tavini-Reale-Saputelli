package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;

import javax.swing.*;

public class TestMain {




    public static void main(String[] args){

        MenuGui menuGui = new MenuGui();

        System.out.println(menuGui.askForName());

        System.out.println(menuGui.askForDate().toString());

        MenuMessage create = menuGui.askForInfoToCreateLobby("peppe");

        System.out.println(create.getLobbyName() +"\n" +create.getNumberOfPlayers() +"\n" +create.getLobbyPassword());



    }



}
