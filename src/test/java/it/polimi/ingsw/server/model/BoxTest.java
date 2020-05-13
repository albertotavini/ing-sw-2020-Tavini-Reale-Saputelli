package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class BoxTest {

    //I use this method to generate random numbers to test boxes inside the board
    int generateInsideDimension(){
        Random random = new Random();
        return random.nextInt(4);
    }

    @Test
    void placeDomeTest() {
        int row = generateInsideDimension();
        int column = generateInsideDimension();

        Box box = new Box(row, column);

        //box has level 0 and it is not domed
        assertEquals( box.getTower().size(), 0);
        assertFalse( box.isDomed() );
        //placing a dome
        box.placeDome();
        //box has level 1 and it is domed
        assertEquals( box.getTower().size(), 1);
        assertTrue( box.isDomed() );
    }

    @Test
    void increaseLevelTest() {
        int row = generateInsideDimension();
        int column = generateInsideDimension();

        Box box = new Box(row, column);

        //box has level 0
        assertEquals( box.getTower().size(), 0);
        assertFalse( box.isDomed() );
        box.increaseLevel();
        //box has level 1
        assertEquals( box.getTower().size(), 1);
        assertFalse( box.isDomed() );
        box.increaseLevel();
        //box has level 2
        assertEquals( box.getTower().size(), 2);
        assertFalse( box.isDomed() );
        box.increaseLevel();
        //box has level 3
        assertEquals( box.getTower().size(), 3);
        assertFalse( box.isDomed() );
        box.increaseLevel();
        //box has level 4
        assertEquals( box.getTower().size(), 4);
        assertTrue( box.isDomed() );
        box.increaseLevel();
        //box doesn't increase its level anymore
        //box is still level 4
        assertEquals( box.getTower().size(), 4);
        assertTrue( box.isDomed() );
    }

    @Test
    void decreaseLevelTest() {
        int row = generateInsideDimension();
        int column = generateInsideDimension();

        Box box = new Box(row, column);

        box.increaseLevel();
        box.increaseLevel();
        box.increaseLevel();
        box.increaseLevel();

        //now box has level 4
        assertEquals( box.getTower().size(), 4 );
        assertTrue( box.isDomed() );
        box.decreaseLevel();
        //now box has level 3
        assertEquals( box.getTower().size(), 3 );
        assertFalse( box.isDomed() );
        box.decreaseLevel();
        //now box has level 2
        assertEquals( box.getTower().size(), 2 );
        assertFalse( box.isDomed() );
        box.decreaseLevel();
        //now box has level 1
        assertEquals( box.getTower().size(), 1 );
        assertFalse( box.isDomed() );
        box.decreaseLevel();
        //now box has level 0
        assertEquals( box.getTower().size(), 0 );
        assertFalse( box.isDomed() );
        box.decreaseLevel();
        //box doesn't decrease its level anymore
        //box is still level 0
        assertEquals( box.getTower().size(), 0 );
        assertFalse( box.isDomed() );
    }

    @Test
    void cloneTest() throws DataFormatException {
        //this method test at the same time bot the clone methods in Box
        Player p1 = new Player("Marco", 1,2 ,2000);
        Box b1 = new Box(0,0);
        b1.increaseLevel();
        b1.increaseLevel();
        b1.setOccupier(new Worker(p1, Color.YELLOW, "B"));

        Box b2 = b1.cloneBox();

        assertEquals(b1.getOccupier(), b2.getOccupier());
        assertEquals(b1.getTower().get(0).getLevel(), b2.getTower().get(0).getLevel());
        assertEquals(b1.getTower().get(1).getLevel(), b2.getTower().get(1).getLevel());
        //assertEquals(b1.getTower().get(2).getLevel(), b2.getTower().get(2).getLevel());
        assertEquals(b1.getTower().size(), b2.getTower().size());
    }

}