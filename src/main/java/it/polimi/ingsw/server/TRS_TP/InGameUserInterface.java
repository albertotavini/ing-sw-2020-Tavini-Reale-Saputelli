package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public interface InGameUserInterface {


    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);

}


class InGameCli implements InGameUserInterface {


    @Override
    public PlayerMove askForCoordinates(String message) {

        int row, column;
        PlayerMove coordinates = null;


        System.out.println(message +"\nInsert coordinates (x,y)");
        String s = ClientMain.scannerIn.nextLine();

        if (s.length() == 3 && s.charAt(1) == ',')
        {
            String[] inputs = s.split(",");
            row = Integer.parseInt(inputs[0]);
            column = Integer.parseInt(inputs[1]);

            //coordinates = new PlayerMove(row, column, ....);

            return coordinates;

        }

        return coordinates;




    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {


        String conferma = null;

        do{

            System.out.println(message +" y/n:");
            conferma = ClientMain.scannerIn.nextLine();
            conferma = conferma.toUpperCase();

        }while(!(conferma.equals("Y") || conferma.equals("N")));

        PlayerMove confirmation = null;


        if(conferma.equals("Y"))
        {
            //confirmation = new PlayerMove(ConfirmationEnum.Yes, ...);
            return confirmation;
        }

        else{
            //confirmation = new PlayerMove(ConfirmationEnum.No,...);
            return confirmation;
        }

    }

    @Override
    public PlayerMove askForGodName(String message) {
        System.out.println(message +"Insert god name");

        String godName = ClientMain.scannerIn.nextLine();

        PlayerMove playerMoveGodName = null;

        //playerMoveGodName = new PlayerMove(godName,...);

        return playerMoveGodName;

    }

}


class InGameGui extends JFrame implements InGameUserInterface {

    @Override
    public PlayerMove askForCoordinates(String message) {
        return null;
    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {
        return null;
    }

    @Override
    public PlayerMove askForGodName(String message) {
        return null;
    }
}
