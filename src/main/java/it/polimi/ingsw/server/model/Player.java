package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.utils.Global;

import java.io.Serializable;
import java.util.zip.DataFormatException;


/**
 * This class represents the player, with his username and his birthdate.
 */
public class Player implements Serializable {

    private final String name;
    private final Date birthDate;

    /**
     * The constructor assigns his name and birthdate to the Player
     *
     * @param name Player's name
     * @param birthDate Player's birthdate
     */
    public Player(String name, Date birthDate){
        this.name = name;
        this.birthDate = birthDate;
    }

    /**
     * The constructor assigns his name and birthdate to the Player
     *
     * @param name Player's name
     * @param dayOfBirth Player's day of birth
     * @param monthOfBirth Player's month of birth
     * @param yearOfBirth Player's year of birth
     * @throws DataFormatException
     */
    public Player(String name, int dayOfBirth, int monthOfBirth, int yearOfBirth) throws DataFormatException{
        this.name = name;
        this.birthDate = new Date(dayOfBirth, monthOfBirth, yearOfBirth);
    }

     Date getBirthDate() {
        return birthDate;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Player)) return false;
        //faccio casting per evitare problemi
        return ((Player) obj).name.equals(this.name);
    }

    @Override
    public String toString() {
        return Global.PLAYERNAMED +getName()+ Global.BORNON +getBirthDate();
    }

    /**
     * This method is present in this class because it was asked by Sonar.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}



