package it.polimi.ingsw.server.TRS_TP;

import java.util.Scanner;

public interface UserInterface {

    String getName();
    boolean askForConfirm();
}



class cli implements UserInterface{

    @Override
    public String getName() {
        Scanner in = new Scanner(System.in);
        System.out.println("Inserisci nome: ");
        String nome = in.nextLine();
        return nome;
    }

    @Override
    public boolean askForConfirm() {

        return false;
    }


}


class gui implements UserInterface {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean askForConfirm() {
        return false;
    }
}
