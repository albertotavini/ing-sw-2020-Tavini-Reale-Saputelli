package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelMessageTest {

    @Test
    void addInfo() {
        ModelMessage modelMessage = new ModelMessage (ModelMessageType.NEEDSGODNAME, "hello");
        modelMessage.addInfo("world");
        assertEquals("hello\nworld", modelMessage.getMessage());
        modelMessage.addInfo("world");
        assertEquals("hello\nworld", modelMessage.getMessage());
        modelMessage.addInfo("worl");
        assertEquals("hello\nworld", modelMessage.getMessage());
    }
}