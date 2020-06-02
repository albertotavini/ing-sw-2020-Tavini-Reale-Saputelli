package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

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
