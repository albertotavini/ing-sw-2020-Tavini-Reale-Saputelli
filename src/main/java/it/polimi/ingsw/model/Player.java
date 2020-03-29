package it.polimi.ingsw.model;

import java.util.zip.DataFormatException;


public class Player {

    //attributi finchè final private o public cambia poco
    private final String name;
    private final Date birthDate;
    private Turn personalTurn;

    public Player(String name, Date birthDate){

        this.name = name;
        this.birthDate = birthDate;
    }

    public Turn getPersonalTurn() {
        return personalTurn;
    }

    public void setPersonalTurn(Turn personalTurn) {
        this.personalTurn = personalTurn;
    }

    public Player(String name, int dayOfBirth, int monthOfBirth, int yearOfBirth) throws DataFormatException{

        this.name = name;
        this.birthDate = new Date(dayOfBirth, monthOfBirth, yearOfBirth);

    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Player)) return false;
        //faccio casting per evitare problemi
        if( ((Player) obj).name.equals(this.name) ) return true;
        else return false;
    }

    @Override
    public String toString() {
        return "il giocatore di nome " +getName()+ " nato il " +getBirthDate();
    }
}


class Date{

    //ho aggiunto la classe date per chiarezza e per delegargli i controlli
    //non ho messo le variabili locali short per questioni di praticità (non si risparmia poi molto)
    //il costruttore si può fare friendly?

    public final int dayOfBirth;
    public final int monthOfBirth;
    public final int yearOfBirth;



    public Date(int dayOfBirth, int monthOfBirth, int yearOfBirth) throws DataFormatException{

        //non uso il costrutto switch in java visto che non sono possibili i multi casi (o meglio, si, ma in maniera "forzata")
        //nel primo controllo non ho messo l'anno, interessa poco, magari uno vuole impersonare ramses 2, non conto anni bisestili poi

        if( monthOfBirth > 12 || monthOfBirth < 1 || dayOfBirth > 31 || dayOfBirth < 1 ) throw new DataFormatException("Data non valida");

        if( monthOfBirth == 2 && dayOfBirth > 28 ) throw new DataFormatException("Data non valida");

        if(( monthOfBirth == 4 || monthOfBirth == 6 || monthOfBirth == 9 || monthOfBirth == 11 ) && dayOfBirth > 30 ) throw new DataFormatException("Data non valida");

        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.yearOfBirth = yearOfBirth;


    }


    public boolean younger (Date other) {
        if (this.yearOfBirth > other.yearOfBirth) return true;
        else if (this.yearOfBirth == other.yearOfBirth && this.monthOfBirth > other.monthOfBirth) return true;
        else if ( this.yearOfBirth == other.yearOfBirth && this.monthOfBirth == other.monthOfBirth && this.dayOfBirth > other.dayOfBirth) return true;
        else return false;
        //if same birthdate returns false
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Date)) return false;

        if(((Date) obj).dayOfBirth == this.dayOfBirth && ((Date) obj).monthOfBirth == this.monthOfBirth && ((Date) obj).yearOfBirth == this.yearOfBirth) return true;
        else return false;
    }

    @Override
    public String toString() {
        return +dayOfBirth+"/"+monthOfBirth+"/"+yearOfBirth;
    }
}




