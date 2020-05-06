package it.polimi.ingsw.server.observers.ModelMessage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelMessageTest {

    @Test
    void addInfo() {
        ModelMessage modelMessage = new ModelMessage (ModelMessageType.NeedsGodName, "hello");
        modelMessage.addInfo("world");
        assertEquals(modelMessage.getMessage(), "hello\nworld");
        modelMessage.addInfo("world");
        assertEquals(modelMessage.getMessage(), "hello\nworld");
        modelMessage.addInfo("worl");
        assertEquals(modelMessage.getMessage(), "hello\nworld");

    }
}