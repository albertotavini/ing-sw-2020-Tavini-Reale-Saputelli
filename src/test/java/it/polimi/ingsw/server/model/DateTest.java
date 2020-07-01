package it.polimi.ingsw.server.model;

import org.junit.Test;

import javax.xml.crypto.Data;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

//all methods have been tested

public class DateTest {

    @Test
    public void incorrectFormatDateTest() throws DataFormatException {
        boolean failed = false;
        try {
            Date date1 = new Date(40, 12, 1990);

        } catch (DataFormatException e) {
         failed = true;
        }
        assertTrue(failed);
        failed = false;
        try {
            Date date2 = new Date(30, -2, 1985);

        } catch (DataFormatException e) {
            failed = true;
        }
        assertTrue(failed);
        failed = false;

        try {

            Date date3 = new Date(31, 6, 1980);

        } catch (DataFormatException e) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void youngerTestDifferentYears() throws DataFormatException{

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

    @Test
    public void youngerTestDifferentMonth() throws DataFormatException{

        Date date1 = new Date(22,12,1990);
        Date date2 = new Date(11,6,1990);
        Date date3 = new Date(5,3,1990);

        assertTrue( date1.younger(date2) );
        assertTrue( date1.younger(date3) );

        assertFalse( date2.younger(date1) );
        assertTrue( date2.younger(date3) );

        assertFalse( date3.younger(date1) );
        assertFalse( date3.younger(date1) );


    }

    @Test
    public void youngerTestDifferentDay() throws DataFormatException{

        Date date1 = new Date(22,6,1990);
        Date date2 = new Date(11,6,1990);
        Date date3 = new Date(5,6,1990);

        assertTrue( date1.younger(date2) );
        assertTrue( date1.younger(date3) );

        assertFalse( date2.younger(date1) );
        assertTrue( date2.younger(date3) );

        assertFalse( date3.younger(date1) );
        assertFalse( date3.younger(date1) );


    }

    @Test
    public void equalsTest() throws DataFormatException{
        Date date1 = new Date(22,6,1990);
        Date date2 = new Date(11,6,1990);
        Date date3 = new Date(22,6,1990);
        int c = 71;

        assertEquals(date1, date3);
        assertNotEquals(date1, date2);
        assertNotEquals(date3, c);
    }

    @Test
    public void othersTest() throws DataFormatException{
        Date date1 = new Date(22,6,1990);

        assertNotEquals(date1.hashCode(), -3);
    }


}