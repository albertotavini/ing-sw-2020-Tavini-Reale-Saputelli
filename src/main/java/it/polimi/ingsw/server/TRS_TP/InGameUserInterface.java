package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.utils.ColorAnsi;
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

        int row = 7;
        int column = 7;
        PlayerMove coordinates = null;
        String dataInput = null;
        boolean correctInput = false;

        do {

            System.out.println(ColorAnsi.RED +"\nInsert coordinates (x,y)" +ColorAnsi.RESET);
            dataInput = ClientMain.scannerIn.nextLine();


            String regexData = "^([0-4]),([0-4])$";
            Pattern playerMovePattern = Pattern.compile(regexData);
            Matcher matcherPlayerMove = playerMovePattern.matcher(dataInput);
            correctInput = matcherPlayerMove.find();


            if (correctInput) {


                row = Integer.parseInt(matcherPlayerMove.group(1));
                column = Integer.parseInt(matcherPlayerMove.group(2));


            }

        }while(!correctInput);


        coordinates = new PlayerMove(row, column, null);
        return coordinates;



    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {


        String conferma = null;
        boolean correctInput = false;
        String regexData = "^(y|n)$";
        Pattern confirmationPattern = Pattern.compile(regexData);
        Matcher matcherConfirmation;



        do{

            System.out.println(ColorAnsi.RED +"y/n:" +ColorAnsi.RESET);
            conferma = ClientMain.scannerIn.nextLine();

            matcherConfirmation = confirmationPattern.matcher(conferma);
            correctInput = matcherConfirmation.find();


        }while(!correctInput);




        PlayerMove playerMoveConfirmation;


        if(conferma.equals("y"))
        {
            playerMoveConfirmation = new PlayerMove(ConfirmationEnum.Yes, null);

        }

        else{

            playerMoveConfirmation = new PlayerMove(ConfirmationEnum.No, null);

        }

        return playerMoveConfirmation;

    }

    @Override
    public PlayerMove askForGodName(String message) {

        System.out.println(ColorAnsi.RED +"Insert god name:" +ColorAnsi.RESET);

        String godName = ClientMain.scannerIn.nextLine();

        PlayerMove playerMoveGodName = new PlayerMove(godName, null);

        return playerMoveGodName;

    }

    @Override
    public void showBoard(BoardPhotography boardPhotography) {

            BoxPhotography[][] matrix = boardPhotography.getMatrixPhotograph();

            System.out.println("\n" +"       0       1       2       3       4 ");

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
