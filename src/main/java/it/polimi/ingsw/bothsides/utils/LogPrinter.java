package it.polimi.ingsw.bothsides.utils;

import it.polimi.ingsw.server.ServerMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * this class is meant to print on a txt file the log of eventual error and problems the server might have
 */
public class LogPrinter {

    private LogPrinter(){
        //hiding the default constructor
    }

    private static boolean fileAlreadyCreated = false;

    private static String serverLogPath = ServerMain.getServerLogPath();

    private static String log = "";


    /**
     * simply appends a string to the log
     *
     * @param things string to append
     */
    public static synchronized void printOnLog(String things){

        log = log + things;

    }


    /**
     * this method effectively prints the log on the txt file
     */
    public static void printLogOnFile(){



        String completePath = serverLogPath +"\\Log_Server.txt";

        if(!fileAlreadyCreated){

            CreateFile.createfile(completePath);
            fileAlreadyCreated = true;
        }

        WriteFile.write(completePath, log);

    }


    private static class CreateFile {

        private CreateFile(){
            //hiding the default constructor
        }


        public static void createfile(String percorso) {
            try {

                File myObj = new File(percorso);
                myObj.createNewFile();

            } catch (IOException e) {

                //System.err.printf("FATAL ERROR WRONG PATH")
                //e.printStackTrace()
            }
        }
    }


    private static class WriteFile {

        private WriteFile(){
            //hiding the private constructor
        }

        public static void write(String path, String things) {

            FileWriter myWriter;

            try {

                myWriter = new FileWriter(path, true);


                myWriter.write(things);
                myWriter.close();

            } catch (IOException e) {
                System.err.printf("FATAL ERROR WHILE WRITING SERVER LOG");
                e.printStackTrace();
            }
        }
    }


}



