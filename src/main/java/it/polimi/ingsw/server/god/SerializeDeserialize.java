package it.polimi.ingsw.server.god;

import java.io.*;
import java.util.ArrayList;

public final class SerializeDeserialize {

    //Non faccio overloading per evitare confusione!

    //******************************************************************************************************************************
    //******************************************************************************************************************************
    //******************** L'appendOrNot deve stare (quasi) sempre a false altrimenti sono problemi! *******************************
    //******************** infatti va ad appendere due liste! non appende gli dei singolarmente      *******************************
    //******************************************************************************************************************************
    //******************************************************************************************************************************

    public static void serializeListWithOrWithoutAppend(ArrayList<God> dio, String outputPath, boolean AppendOrNot) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(outputPath, AppendOrNot);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dio);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("\n***Errore nella serializzazione del file***\n");
        } finally {
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
        }
    }

    public static void serializeSingleGodWithOrWithoutAppend(God dio, String outputPath, boolean AppendOrNot) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputPath, AppendOrNot);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dio);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("\n***Errore nella serializzazione del file***\n");
        } finally {
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
        }
    }


    public static ArrayList<God> deserializeListOfGods(String inputPath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectinputstream = null;


        try {

            FileInputStream streamIn = new FileInputStream(inputPath);
            objectinputstream = new ObjectInputStream(streamIn);
            ArrayList<God> readGod = (ArrayList<God>) objectinputstream.readObject();
            return readGod;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n***Errore nella deserializzazione del file***\n");
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }

        return null;

    }

    public static God deserializeSingleGod(String inputPath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectinputstream = null;


        try {

            FileInputStream streamIn = new FileInputStream(inputPath);
            objectinputstream = new ObjectInputStream(streamIn);
            God readGod = (God) objectinputstream.readObject();
            return readGod;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n***Errore nella deserializzazione del file***\n");
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }

        return null;

    }
}