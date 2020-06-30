package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

/**
 * this is the type of message the client and server exchange when it needs to be decided if a new match is going to start
 * after concluding the previous one
 *
 * methods are simply to build the corresponding messages
 */
public class FinalStateMessage extends SetupMessage implements Serializable {

    private final boolean answer;

    private FinalStateMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage, boolean answer) {
        super(typeOfSetupMessage, errorMessage);
        this.answer = answer;
    }

    private FinalStateMessage(TypeOfSetupMessage typeOfSetupMessage, boolean answer) {
        super(typeOfSetupMessage);
        this.answer = answer;
    }
    public static FinalStateMessage newFinalStateMessageOffer() {

        return new FinalStateMessage(TypeOfSetupMessage.FINAL_STATE_MESSAGE_OFFER, false);

    }
    public static FinalStateMessage newFinalStateMessageAnswer(boolean answer) {

        return new FinalStateMessage(TypeOfSetupMessage.FINAL_STATE_MESSAGE_ANSWER, answer);

    }

    public boolean getAnswer() {
        return answer;
    }
}
