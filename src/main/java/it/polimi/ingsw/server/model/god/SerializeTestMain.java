package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.bothsides.utils.Global;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import static it.polimi.ingsw.server.model.god.GodLookUpTable.GodsImplementation.*;

public class SerializeTestMain {


    public static void main(String[] args) throws IOException {


        ArrayList<God> arrayListGod = new ArrayList<>();


        athena.addEffectTypes(GodTypeEffect.ON_MOVE);
        arrayListGod.add(athena);

        minotaur.addEffectTypes(GodTypeEffect.ON_MOVE);
        arrayListGod.add(minotaur);

        pan.addEffectTypes(GodTypeEffect.ON_MOVE);
        arrayListGod.add(pan);


        apollo.addEffectTypes(GodTypeEffect.ON_MOVE);
        arrayListGod.add(apollo);


        prometheus.addEffectTypes(GodTypeEffect.ON_MOVE);
        prometheus.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);
        arrayListGod.add(prometheus);


        artemis.addEffectTypes(GodTypeEffect.ON_MOVE);
        artemis.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);
        arrayListGod.add(artemis);


        atlas.addEffectTypes(GodTypeEffect.ON_BUILD);
        atlas.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);
        arrayListGod.add(atlas);


        demeter.addEffectTypes(GodTypeEffect.ON_BUILD);
        demeter.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);
        arrayListGod.add(demeter);


        hephaestus.addEffectTypes(GodTypeEffect.ON_BUILD);
        hephaestus.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);
        arrayListGod.add(hephaestus);


        hestia.addEffectTypes(GodTypeEffect.ON_BUILD);
        arrayListGod.add(hestia);


        triton.addEffectTypes(GodTypeEffect.ON_MOVE);
        arrayListGod.add(triton);


        ares.addEffectTypes(GodTypeEffect.ON_BUILD);
        arrayListGod.add(ares);


        zeus.addEffectTypes(GodTypeEffect.ON_BUILD);
        arrayListGod.add(zeus);


        chronus.addEffectTypes(GodTypeEffect.ON_OPPONENT);
        arrayListGod.add(chronus);




        ListOfGodContainer listOfGodContainer = new ListOfGodContainer("Complete Deck", arrayListGod, Global.GODS_YOU_CAN_CHOSE_FROM);


        ListOfGodContainer read = null;

        /*String path = "C:\\Users\\Simone Reale\\IdeaProjects\\ing-sw-2020-Tavini-Reale-Saputelli\\src\\main\\resources\\GodJsons\\CompleteDeck.json";

        try {

            SerializeDeserialize.serializeListWithOrWithoutAppend(listOfGodContainer, path, false);

        } catch (IOException e) {
            e.printStackTrace();
        }*/



       ClassLoader classLoader = new SerializeTestMain().getClass().getClassLoader();

       InputStream inputStream = classLoader.getResourceAsStream("GodJsons/CompleteDeck.json");


       ObjectInputStream objectinputstream = null;


        try {

            objectinputstream = new ObjectInputStream(inputStream);
            read = (ListOfGodContainer) objectinputstream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n***Errore nella deserializzazione del file***\n");
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }


       /*try {

            read = SerializeDeserialize.deserializeListOfGods(path);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/





        System.out.println(read.getNameOfDeck());

        System.out.println(read.getStringOfGods());





    }


}
