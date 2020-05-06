package it.polimi.ingsw.server.model.god;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericGodTest {

    @Test
    void setGod() {
        //the constructor calls the setGod
        GenericGod g1 = new GenericGod("athena");
        //the string is inside a God instance that is created in the class, not a simple string
        assertEquals(g1.getSpecificGodName(), "ATHENA");
        g1.setGod("ATLAS");
        assertEquals(g1.getSpecificGodName(), "ATLAS");
    }

    @Test
    void setGodFailed() {
        GenericGod g1 = new GenericGod("arturo");
        try {g1.getSpecificGodName();}
        catch (NullPointerException e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    void activateEffect() {
        //this method is tested in all the calls of GodEffectsTest
    }
    
}