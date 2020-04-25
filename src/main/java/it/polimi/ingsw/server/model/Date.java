package it.polimi.ingsw.server.model;

import java.util.zip.DataFormatException;

public class Date {
        public final int dayOfBirth;
        public final int monthOfBirth;
        public final int yearOfBirth;


        public Date(int dayOfBirth, int monthOfBirth, int yearOfBirth) throws DataFormatException {

            if( monthOfBirth > 12 || monthOfBirth < 1 || dayOfBirth > 31 || dayOfBirth < 1 ) throw new DataFormatException("Data non valida");

            if( monthOfBirth == 2 && dayOfBirth > 28 ) throw new DataFormatException("Data non valida");

            if(( monthOfBirth == 4 || monthOfBirth == 6 || monthOfBirth == 9 || monthOfBirth == 11 ) && dayOfBirth > 30 ) throw new DataFormatException("Data non valida");

            this.dayOfBirth = dayOfBirth;
            this.monthOfBirth = monthOfBirth;
            this.yearOfBirth = yearOfBirth;

        }


        public boolean younger (it.polimi.ingsw.server.model.Date other) {
            if (this.yearOfBirth > other.yearOfBirth) return true;
            else if (this.yearOfBirth == other.yearOfBirth && this.monthOfBirth > other.monthOfBirth) return true;
            else if ( this.yearOfBirth == other.yearOfBirth && this.monthOfBirth == other.monthOfBirth && this.dayOfBirth > other.dayOfBirth) return true;
            else return false;
            //if same birthdate returns false
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof it.polimi.ingsw.server.model.Date)) return false;

            if(((it.polimi.ingsw.server.model.Date) obj).dayOfBirth == this.dayOfBirth && ((it.polimi.ingsw.server.model.Date) obj).monthOfBirth == this.monthOfBirth && ((it.polimi.ingsw.server.model.Date) obj).yearOfBirth == this.yearOfBirth) return true;
            else return false;
        }

        @Override
        public String toString() {
            return +dayOfBirth+"/"+monthOfBirth+"/"+yearOfBirth;
        }
    }
