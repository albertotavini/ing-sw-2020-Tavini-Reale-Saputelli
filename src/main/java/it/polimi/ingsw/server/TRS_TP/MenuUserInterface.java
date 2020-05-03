package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;

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
        System.out.println("Inserisci nome:");
        String nome = ClientMain.scannerIn.nextLine();
        return nome;
    }

    @Override
    public boolean askForConfirm(String message) {
        String conferma = null;

        do{

                System.out.println(message +"\nConfermi y/n:");
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

            System.out.println("Inserisci giorno di nascita in formato gg/mm/aaaa:");
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

            System.out.println("Vuoi creare una lobby o partecipare? crea/partecipa ");
            wantsToCreate = ClientMain.scannerIn.nextLine();
            wantsToCreate = wantsToCreate.toUpperCase();

        }while(!(wantsToCreate.equals("CREA") || wantsToCreate.equals("PARTECIPA")));

        if(wantsToCreate.equals("CREA")) return true;

        else return false;
    }

    @Override
    public void printMessage(String message) {
        System.out.println(message);
    }

    @Override
    public MenuMessages askForInfoToCreateLobby(String nameCreator) {

        int capienzaLobby;
        String nomeLobby;
        String passwordLobby;
        String ripetizionePassword;
        String isPublic;

        System.out.println("Inserisci nome lobby");
        nomeLobby = ClientMain.scannerIn.nextLine();

        do {

            System.out.println("Inserisci capienza lobby:");
            capienzaLobby = Integer.parseInt(ClientMain.scannerIn.nextLine());

        }while(!(capienzaLobby > 1 && capienzaLobby < 4));


        do{

            System.out.printf("Preferisci una lobby pubblica o privata? pubblica/privata\n");
            isPublic = ClientMain.scannerIn.nextLine();
            isPublic = isPublic.toUpperCase();

        }while(!(isPublic.equals("PUBBLICA") || isPublic.equals("PRIVATA")));



        MenuMessages createLobbyInfo;

        //creo una lobby privata
        if(isPublic.equals("PRIVATA")) {

            do {

                System.out.println("Inserisci password lobby:");
                passwordLobby = ClientMain.scannerIn.nextLine();
                System.out.println("Inserisci di nuovo password lobby:");
                ripetizionePassword = ClientMain.scannerIn.nextLine();

            } while (!(passwordLobby.equals(ripetizionePassword)));

            createLobbyInfo = new MenuMessages(nomeLobby, capienzaLobby, passwordLobby, nameCreator);
        }


        //creo una lobby pubblica
        else {

            createLobbyInfo = new MenuMessages(nomeLobby, capienzaLobby, nameCreator);

        }

        return createLobbyInfo;
    }

    @Override
    public boolean askIfWantsToParticipateLobbyPublic() {
        String wantsToParticipateLobbyPublic = null;

        do{

            System.out.println("Vuoi partecipare ad una lobby pubblica o privata? pubblica/privata");
            wantsToParticipateLobbyPublic = ClientMain.scannerIn.nextLine();
            wantsToParticipateLobbyPublic = wantsToParticipateLobbyPublic.toUpperCase();

        }while(!(wantsToParticipateLobbyPublic.equals("PUBBLICA") || wantsToParticipateLobbyPublic.equals("PRIVATA")));

        if(wantsToParticipateLobbyPublic.equals("PUBBLICA")) return true;

        else return false;
    }

    @Override
    public MenuMessages askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        String nomeLobby;
        String passwordLobby;

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
