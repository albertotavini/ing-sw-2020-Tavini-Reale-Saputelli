package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import it.polimi.ingsw.server.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelMessageTest {

    @Test
    void copyAndAddErrorTest() {
        ModelMessage one;
        ModelMessage two;

        one = new ModelMessage(ModelMessageType.COORDINATES, ModelError.NONE, "game begins", "mark");
        two = one.copyAndAddError(ModelError.SAMEBOX);

        assertEquals(ModelMessageType.COORDINATES, two.getModelMessageType());
        assertEquals(ModelError.SAMEBOX, two.getModelError());
        assertEquals("game begins", two.getMessage());
        assertEquals("mark", two.getCurrentPlayer());
    }

    @Test
    void copyAndAddPlayerTest() {
        ModelMessage one;
        ModelMessage two;

        one = new ModelMessage(ModelMessageType.COORDINATES, ModelError.NONE, "game begins", " ");
        two = one.copyAndAddPlayer("john");

        assertEquals(ModelMessageType.COORDINATES, two.getModelMessageType());
        assertEquals(ModelError.NONE, two.getModelError());
        assertEquals("game begins", two.getMessage());
        assertEquals("john", two.getCurrentPlayer());

    }


}