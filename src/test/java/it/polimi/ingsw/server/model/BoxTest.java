package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class BoxTest {

    @Test
    void placeDomeTest() {
    }

    @Test
    void increaseLevelTest() {
    }

    @Test
    void decreaseLevelTest() {
    }

    @Test
    void getOccupierTest() {
    }

    @Test
    void setOccupierTest() {
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
        assertEquals(b1.getTower().get(2).getLevel(), b2.getTower().get(2).getLevel());
        assertEquals(b1.getTowerSize(), b2.getTowerSize());

    }
}