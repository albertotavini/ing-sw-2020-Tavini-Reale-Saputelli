package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.utils.ColorAnsi;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public interface MenuUserInterface {

    String askForName();
    boolean askForConfirm(String message);
    Date askForDate();
    boolean askIfPlayerWantsToCreate();
    void printMessage(String message);
    MenuMessages askForInfoToCreateLobby(String creator);
    boolean askIfWantsToParticipateLobbyPublic();
    MenuMessages askForInfoToParticipateLobby(boolean isPublic, String namePlayer);


}


class MenuCli implements MenuUserInterface {

    @Override
    public String askForName() {
        System.out.println(ColorAnsi.RED +"Inserisci nome:" +ColorAnsi.RESET);
        String nome = ClientMain.scannerIn.nextLine();
        return nome;
    }

    @Override
    public boolean askForConfirm(String message) {
        String conferma = null;

        do{

                System.out.println(ColorAnsi.RED +message +"\nConfermi y/n:" +ColorAnsi.RESET);
                conferma = ClientMain.scannerIn.nextLine();
                conferma = conferma.toUpperCase();

        }while(!(conferma.equals("Y") || conferma.equals("N")));

        if(conferma.equals("Y")) return true;

        else return false;
    }

    @Override
    public Date askForDate() {

        Date data = null;

        do{

            System.out.println(ColorAnsi.RED +"Inserisci giorno di nascita in formato gg/mm/aaaa:" +ColorAnsi.RESET);
            String dataInput = ClientMain.scannerIn.nextLine();

            //forse troppo naif?

            String regexData = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/([0-9]{4}$)";
            Pattern datePattern = Pattern.compile(regexData);
            Matcher matcherDate = datePattern.matcher(dataInput);

            if(matcherDate.find()) {

                //System.out.println(matcherDate.group(0) +" # " +matcherDate.group(1) +" # " +matcherDate.group(2) +" # " +matcherDate.group(3));

                int giorno = Integer.parseInt(matcherDate.group(1));
                int mese = Integer.parseInt(matcherDate.group(2));
                int anno = Integer.parseInt(matcherDate.group(3));


                try {

                    data = new Date(giorno, mese, anno);

                } catch (DataFormatException e) {
                    //da vedere
                }
            }

        }while(data == null);

        return data;

    }

    @Override
    public boolean askIfPlayerWantsToCreate() {
        String wantsToCreate = null;

        do{

            System.out.println(ColorAnsi.RED +"Vuoi creare una lobby o partecipare? c/p " +ColorAnsi.RESET);
            wantsToCreate = ClientMain.scannerIn.nextLine();
            wantsToCreate = wantsToCreate.toUpperCase();

        }while(!(wantsToCreate.equals("C") || wantsToCreate.equals("P")));

        if(wantsToCreate.equals("C")) return true;

        else return false;
    }

    @Override
    public void printMessage(String message) {
        System.out.println(message);
    }

    @Override
    public MenuMessages askForInfoToCreateLobby(String nameCreator) {

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;

        String nomeLobby;

        String passwordLobby;
        String ripetizionePassword;

        String isPublic;


        //Color set
        System.out.println(ColorAnsi.RED);

        System.out.println("Inserisci nome lobby:");
        nomeLobby = ClientMain.scannerIn.nextLine();



        do {

            System.out.println("Inserisci capienza lobby:");
            capienzaLobby = ClientMain.scannerIn.nextLine();
            String regexData = "^([2|3])$";

            capacityLobbyPattern = Pattern.compile(regexData);
            matcherCapacity = capacityLobbyPattern.matcher(capienzaLobby);
            correctInput = matcherCapacity.find();

            if (correctInput) {

                capacity = Integer.parseInt(matcherCapacity.group(1));

            }

        }while(!correctInput);



        do{

            System.out.printf("Preferisci una lobby pubblica o privata? pu/pr\n");
            isPublic = ClientMain.scannerIn.nextLine();
            isPublic = isPublic.toUpperCase();

        }while(!(isPublic.equals("PU") || isPublic.equals("PR")));



        MenuMessages createLobbyInfo;

        //creo una lobby privata
        if(isPublic.equals("PR")) {

            do {

                System.out.println("Inserisci password lobby:");
                passwordLobby = ClientMain.scannerIn.nextLine();
                System.out.println("Inserisci di nuovo password lobby:");
                ripetizionePassword = ClientMain.scannerIn.nextLine();

            } while (!(passwordLobby.equals(ripetizionePassword)));

            //costruttore di create per la lobby private
            createLobbyInfo = new MenuMessages(nomeLobby, capacity, passwordLobby, nameCreator);
        }


        //creo una lobby pubblica
        else {

            //costruttore di create per la lobby pubblica
            createLobbyInfo = new MenuMessages(nomeLobby, capacity, nameCreator);

        }

        //Color reset
        System.out.println(ColorAnsi.RESET);

        return createLobbyInfo;
    }

    @Override
    public boolean askIfWantsToParticipateLobbyPublic() {
        String wantsToParticipateLobbyPublic = null;

        do{

            System.out.println(ColorAnsi.RED +"Vuoi partecipare ad una lobby pubblica o privata? pu/pr" +ColorAnsi.RESET);
            wantsToParticipateLobbyPublic = ClientMain.scannerIn.nextLine();
            wantsToParticipateLobbyPublic = wantsToParticipateLobbyPublic.toUpperCase();

        }while(!(wantsToParticipateLobbyPublic.equals("PU") || wantsToParticipateLobbyPublic.equals("PR")));

        if(wantsToParticipateLobbyPublic.equals("PU")) return true;

        else return false;
    }

    @Override
    public MenuMessages askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        String nomeLobby;
        String passwordLobby;

        System.out.println(ColorAnsi.RED);
        System.out.println("Inserisci nome lobby");
        nomeLobby = ClientMain.scannerIn.nextLine();


        MenuMessages createLobbyInfoToParticipate = null;

        if(isPublic == false) {

            System.out.println("Inserisci password lobby:");
            passwordLobby = ClientMain.scannerIn.nextLine();
            //creo un messaggio utilizzando il costruttore per messaggi di participate privata
            createLobbyInfoToParticipate = new MenuMessages(nomeLobby, passwordLobby, namePlayer);
        }


        else {

            //creo un messaggio utilizzando il costruttore per messaggi di participate pubblica
            createLobbyInfoToParticipate = new MenuMessages(nomeLobby, namePlayer);

        }


        System.out.println(ColorAnsi.RESET);

        return createLobbyInfoToParticipate;
    }


}


class MenuGui extends JFrame implements MenuUserInterface {

    @Override
    public String askForName() {
        //ricordarsi di aggiornare playerName nel client main, importante!!!!!
        return null;
    }

    @Override
    public boolean askForConfirm(String message) {
        return false;
    }

    @Override
    public Date askForDate() {
        //ricordarsi di aggiornare player birthday nel client main, importante!!!!!
        return null;
    }

    @Override
    public boolean askIfPlayerWantsToCreate() {
        return false;
    }

    @Override
    public void printMessage(String message) {

    }

    @Override
    public MenuMessages askForInfoToCreateLobby(String creator) {
        return null;
    }

    @Override
    public boolean askIfWantsToParticipateLobbyPublic() {
        return false;
    }

    @Override
    public MenuMessages askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {
        return null;
    }
}
