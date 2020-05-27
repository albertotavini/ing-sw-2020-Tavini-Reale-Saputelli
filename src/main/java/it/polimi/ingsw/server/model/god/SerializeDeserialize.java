package it.polimi.ingsw.server.model.god;

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

    public static void serializeListWithOrWithoutAppend(ListOfGodContainer godsContainer, String outputPath, boolean AppendOrNot) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;

        try {

            fileOutputStream = new FileOutputStream(outputPath, AppendOrNot);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(godsContainer);

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


    public static ListOfGodContainer deserializeListOfGods(String inputPath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectinputstream = null;

        ListOfGodContainer readGods = null;

        try {

            FileInputStream streamIn = new FileInputStream(inputPath);
            objectinputstream = new ObjectInputStream(streamIn);
            readGods = (ListOfGodContainer) objectinputstream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n***Errore nella deserializzazione del file***\n");
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }

        return readGods;

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



class ListOfGodContainer implements Serializable{

    private final String nameOfDeck;

    private final ArrayList<God> godArrayList;

    private final String stringOfGods;


    public String getNameOfDeck() {
        return nameOfDeck;
    }

    public ArrayList<God> getGodArrayList() {
        return godArrayList;
    }

    public String getStringOfGods() {
        return stringOfGods;
    }



    ListOfGodContainer(String nameOfDeck, ArrayList<God> godArrayList, String stringOfGods) {
        this.nameOfDeck = nameOfDeck;
        this.godArrayList = godArrayList;
        this.stringOfGods = stringOfGods;
    }


}