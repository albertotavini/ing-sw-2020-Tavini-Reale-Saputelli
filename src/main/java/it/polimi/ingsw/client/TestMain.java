package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;


public class TestMain {


    public static void main(String[] args) {


        MenuGui menuGui = new MenuGui();

        System.out.println(menuGui.askBooleanQuestion("Do you want to create a new lobby?"));

        System.out.println(menuGui.askForName());

        System.out.println(menuGui.askForDate().toString());

        MenuMessage create = menuGui.askForInfoToCreateLobby("sjjj");

        System.out.println(create.getLobbyName() +"\n" +create.getNumberOfPlayers() +"\n" +create.getLobbyPassword());


        System.out.println("mm");
        //menuGui.askForInfoToCreateLobby("poppo");
        //menuGui.askForInfoToParticipateLobby(false, "peppe");

       InGameGui in = new InGameGui();



         /*
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.setSize(new Dimension(1000,1000));
        frame.setBackground(Color.WHITE);
        frame.setLayout(new BorderLayout());
       frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(panel);
        IconPersonalized icon = new IconPersonalized(320, 80);
        JLabel label = new JLabel(" ");
        label.setIcon(icon);
        label.setSize(320, 80);
        label.setText("kkk");
        panel.add(label);
        panel.setVisible(true);
        frame.setVisible(true);
            */



    }
}


