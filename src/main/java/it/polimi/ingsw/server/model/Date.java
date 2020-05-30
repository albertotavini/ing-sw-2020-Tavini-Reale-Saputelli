package it.polimi.ingsw.server.model;

import java.io.Serializable;
import java.util.zip.DataFormatException;

/**
 * This class represents a simple date.
 * At the start of the game, it is asked to every player in order to dispose them by age.
 */
public class Date implements Serializable {
        public final int dayOfBirth;
        public final int monthOfBirth;
        public final int yearOfBirth;


    /**
     * The constructor of the class decides if the given date is valid, depending on
     * this date's existence on the calendar.
     *
     * @param dayOfBirth the day of player's birthdate
     * @param monthOfBirth the month of player's birthdate
     * @param yearOfBirth the year of player's birthdate
     * @throws DataFormatException it is thrown if the date doesn't exist on the calendar.
     */
    public Date(int dayOfBirth, int monthOfBirth, int yearOfBirth) throws DataFormatException {

            if( monthOfBirth > 12 || monthOfBirth < 1 || dayOfBirth > 31 || dayOfBirth < 1 ) throw new DataFormatException("Not valid Date");

            if( monthOfBirth == 2 && dayOfBirth > 28 ) throw new DataFormatException("Incorrect Date");

            if(( monthOfBirth == 4 || monthOfBirth == 6 || monthOfBirth == 9 || monthOfBirth == 11 ) && dayOfBirth > 30 ) throw new DataFormatException("Not format matching Date");

            this.dayOfBirth = dayOfBirth;
            this.monthOfBirth = monthOfBirth;
            this.yearOfBirth = yearOfBirth;

        }


    /**
     * It returns the most recent Date object between two.
     * It's useful to know who, between two players, is the youngest.
     *
     * @param other the date compared to current object Date.
     * @return the most recent Date object
     */
    public boolean younger (it.polimi.ingsw.server.model.Date other) {
        if (this.yearOfBirth > other.yearOfBirth) return true;
        else if (this.yearOfBirth == other.yearOfBirth && this.monthOfBirth > other.monthOfBirth) return true;
        else return this.yearOfBirth == other.yearOfBirth && this.monthOfBirth == other.monthOfBirth && this.dayOfBirth > other.dayOfBirth;
        //if same birthdate returns false
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof it.polimi.ingsw.server.model.Date)) return false;

        return ((Date) obj).dayOfBirth == this.dayOfBirth && ((Date) obj).monthOfBirth == this.monthOfBirth && ((Date) obj).yearOfBirth == this.yearOfBirth;
    }

    @Override
    public String toString() {
            return +dayOfBirth+"/"+monthOfBirth+"/"+yearOfBirth;
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

