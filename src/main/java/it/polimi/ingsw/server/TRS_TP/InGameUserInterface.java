package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.BoxPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMoveType;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public interface InGameUserInterface {


    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);

    void showBoard(BoardPhotography boardPhotography);

}


class InGameCli implements InGameUserInterface {


    @Override
    public PlayerMove askForCoordinates(String message) {

        int row, column;
        PlayerMove coordinates = null;
        String s = " ";


        System.out.println("\nInsert coordinates (x,y)");
        s = ClientMain.scannerIn.nextLine();


        if (s.length() == 3 && s.charAt(1) == ',') {
            String[] inputs = s.split(",");
            row = Integer.parseInt(inputs[0]);
            column = Integer.parseInt(inputs[1]);

            coordinates = new PlayerMove(row, column, null);

            return coordinates;

        } else {
            row = 7;
            column = 7;
            coordinates = new PlayerMove(row, column, null);
        }

        return coordinates;




    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {


        String conferma = null;
        do{

            System.out.println("y/n:");
            conferma = ClientMain.scannerIn.nextLine();
            conferma = conferma.toUpperCase();

        }while(!(conferma.equals("Y") || conferma.equals("N") ));

        PlayerMove confirmation = new PlayerMove(ConfirmationEnum.NotDef, null);

        if(conferma.equals("Y"))
        {
            confirmation = new PlayerMove(ConfirmationEnum.Yes, null);
            return confirmation;
        }

        else{
            confirmation = new PlayerMove(ConfirmationEnum.No, null);
            return confirmation;
        }


    }

    @Override
    public PlayerMove askForGodName(String message) {
        System.out.println("Insert god name");

        String godName = ClientMain.scannerIn.nextLine();

        PlayerMove playerMoveGodName = null;

        playerMoveGodName = new PlayerMove(godName, null);

        return playerMoveGodName;

    }

    @Override
    public void showBoard(BoardPhotography boardPhotography) {

            BoxPhotography[][] matrix = boardPhotography.getMatrixPhotograph();

            System.out.println("       0       1       2       3       4 ");
            int rowIndex = 0;
            for (BoxPhotography[] line : matrix) {
                System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
                rowIndex++;
            }

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

    @Override
    public void showBoard(BoardPhotography boardPhotography) {

    }
}
