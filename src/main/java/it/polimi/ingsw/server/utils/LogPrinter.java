package it.polimi.ingsw.server.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogPrinter {

    private static boolean fileAlreadyCreated = false;
    private static Configuration configuration = new Configuration();
    private static String serverLogPath;

    static {
        try {
            serverLogPath = configuration.getServerLogPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printOnLog(String things){


        if(!fileAlreadyCreated){

            CreaFile.createfile(serverLogPath);
            fileAlreadyCreated = true;
        }

        ScriviFile.scrivi(serverLogPath, things);

    }


}


class CreaFile {

    public static void createfile(String percorso) {
        try {

            File myObj = new File(percorso);
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

            FileWriter myWriter = new FileWriter(percorso);
            myWriter.write(roba);
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
