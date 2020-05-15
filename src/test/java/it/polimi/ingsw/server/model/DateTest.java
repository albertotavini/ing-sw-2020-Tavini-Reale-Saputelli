package it.polimi.ingsw.server.model;

import org.junit.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

//all methods have been tested

public class DateTest {

    @Test
    public void youngerTest() throws DataFormatException{

        Date date1 = new Date(22,12,1990);
        Date date2 = new Date(11,6,1985);
        Date date3 = new Date(5,3,1980);

        assertTrue( date1.younger(date2) );
        assertTrue( date1.younger(date3) );

        assertFalse( date2.younger(date1) );
        assertTrue( date2.younger(date3) );

        assertFalse( date3.younger(date1) );
        assertFalse( date3.younger(date1) );

        //creating a new date with the same birthdate of date1
        Date date4 = new Date(22,12,1990);
        //the methods returns false in both ways
        assertFalse( date4.younger(date1) );
        assertFalse( date1.younger(date4) );
    }

}