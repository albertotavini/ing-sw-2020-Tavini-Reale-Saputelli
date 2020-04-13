package it.polimi.ingsw.server.TRS_TP;

import java.util.zip.DataFormatException;

public class ClientViewAdapter {


    private static UserInterface userInterface;    //può essere una classe cli o gui




    public static String askForName() {

        return "stupido";
    }

    /*
    public static Data askForDate() {

        Data data = null;


        try {
            data = new Date(2,5,1998);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }


        return data;
    }*/

    public static boolean askForConfirm() {

        return userInterface.askForConfirm();

    }

}
