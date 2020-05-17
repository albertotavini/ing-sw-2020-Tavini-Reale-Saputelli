package it.polimi.ingsw.client;


import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public interface MenuUserInterface {

    String askForName();
    boolean askBooleanQuestion(String message);
    Date askForDate();
    void printMessage(String message);
    MenuMessage askForInfoToCreateLobby(String creator);
    MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer);


}


class MenuCli implements MenuUserInterface {

    @Override
    public String askForName() {
        System.out.println(ColorAnsi.RED +"Inserisci nome:" +ColorAnsi.RESET);
        String nome = ClientMain.scannerIn.nextLine();
        return nome;
    }

    @Override
    public boolean askBooleanQuestion(String message) {
        String conferma = null;

        do{

                System.out.println(ColorAnsi.RED +message +ColorAnsi.RESET);
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
    public void printMessage(String message) {
        System.out.println(message);
    }

    @Override
    public MenuMessage askForInfoToCreateLobby(String nameCreator) {

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

            System.out.printf("%s", "Preferisci una lobby pubblica o privata? pu/pr\n");
            isPublic = ClientMain.scannerIn.nextLine();
            isPublic = isPublic.toUpperCase();

        }while(!(isPublic.equals("PU") || isPublic.equals("PR")));

        MenuMessage createLobbyInfo;

        //creo una lobby privata
        if(isPublic.equals("PR")) {

            do {

                System.out.println("Inserisci password lobby:");
                passwordLobby = ClientMain.scannerIn.nextLine();
                System.out.println("Inserisci di nuovo password lobby:");
                ripetizionePassword = ClientMain.scannerIn.nextLine();

            } while (!(passwordLobby.equals(ripetizionePassword)));

            //costruttore di create per la lobby private
            createLobbyInfo = MenuMessage.newMenuMessageCreatePrivate(nomeLobby, capacity, passwordLobby, nameCreator);
        }


        //creo una lobby pubblica
        else {

            //costruttore di create per la lobby pubblica
            createLobbyInfo = MenuMessage.newMenuMessageCreatePublic(nomeLobby, capacity, nameCreator);

        }

        //Color reset
        System.out.printf("%s",ColorAnsi.RESET);


        return createLobbyInfo;
    }

    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        String nomeLobby;
        String passwordLobby;

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;
        System.out.printf("%s", ColorAnsi.RED);
        MenuMessage LobbyInfoToParticipate = null;
        boolean wantsLobbyCasual = false;


        if(isPublic) {

            wantsLobbyCasual = askBooleanQuestion("Vuoi partecipare ad una lobby casual? y/n");

            if (wantsLobbyCasual) {

                do {

                    System.out.println(ColorAnsi.RED + "Inserisci capienza lobby:" + ColorAnsi.RESET);
                    capienzaLobby = ClientMain.scannerIn.nextLine();
                    String regexData = "^([2|3])$";

                    capacityLobbyPattern = Pattern.compile(regexData);
                    matcherCapacity = capacityLobbyPattern.matcher(capienzaLobby);
                    correctInput = matcherCapacity.find();

                    if (correctInput) {

                        capacity = Integer.parseInt(matcherCapacity.group(1));

                    }

                } while (!correctInput);


                LobbyInfoToParticipate = MenuMessage.newMenuMessageCasual(namePlayer, capacity);

            }
        }



      if(!wantsLobbyCasual) {

          System.out.println("Inserisci nome lobby");
          nomeLobby = ClientMain.scannerIn.nextLine();

          if (isPublic) {

              //creo un messaggio utilizzando il costruttore per messaggi di participate pubblica
              LobbyInfoToParticipate = MenuMessage.newMenuMessagePartPublic(nomeLobby, namePlayer);

          }

          if (!isPublic) {

              System.out.println("Inserisci password lobby:");
              passwordLobby = ClientMain.scannerIn.nextLine();
              //creo un messaggio utilizzando il costruttore per messaggi di participate privata
              LobbyInfoToParticipate = MenuMessage.newMenuMessagePartPrivate(nomeLobby, passwordLobby, namePlayer);
          }

      }



        System.out.printf("%s", ColorAnsi.RESET);


        return LobbyInfoToParticipate;
    }


}


class MenuGui extends JFrame implements MenuUserInterface {








    public void setMenuGuiVisible(boolean visible){
        this.setVisible(visible);
    }





    @Override
    public String askForName() {
        //ricordarsi di aggiornare playerName nel client main, importante!!!!!
        return null;
    }

    @Override
    public boolean askBooleanQuestion(String message) {
        return false;
    }

    @Override
    public Date askForDate() {
        //ricordarsi di aggiornare player birthday nel client main, importante!!!!!
        return null;
    }

    @Override
    public void printMessage(String message) {

    }

    @Override
    public MenuMessage askForInfoToCreateLobby(String creator) {
        return null;
    }

    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {
        return null;
    }

}
