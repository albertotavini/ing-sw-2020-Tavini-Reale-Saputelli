package it.polimi.ingsw.bothsides.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class LogPrinter {

    //ogni sessione crea un file nuovo


    private static boolean fileAlreadyCreated = false;
    private static Configuration configuration = new Configuration();
    private static String serverLogPath;
    private static int orario = Calendar.getInstance().getTime().getSeconds();
    private static int orarioMinuto = Calendar.getInstance().getTime().getMinutes();
    private static int orarioOra = Calendar.getInstance().getTime().getHours();

    private static String log = "";


    static {
        try {
            serverLogPath = configuration.getServerLogPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printOnLog(String things){

        synchronized (log) {
            log = log + things;
        }

    }


    public static void printLogOnFile(){

        String percorso = serverLogPath +"Log_" +orarioOra +orarioMinuto +orario +".txt";

        if(!fileAlreadyCreated){

            CreaFile.createfile(percorso);
            fileAlreadyCreated = true;
        }

        ScriviFile.scrivi(percorso, log);

    }


}


class CreaFile {

    public static void createfile(String percorso) {
        try {

            File myObj = new File(percorso );
            myObj.createNewFile();

        } catch (IOException e) {
            System.out.println("Oddio, c'è un errore nella creazione file.");
            e.printStackTrace();
        }
    }
}


class ScriviFile {
    public static void scrivi(String percorso, String roba) {
        try {

            FileWriter myWriter = new FileWriter(percorso, true);
            myWriter.write(roba);
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
