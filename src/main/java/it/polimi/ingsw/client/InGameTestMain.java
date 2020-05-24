package it.polimi.ingsw.client;

        import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
        import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
        import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
        import it.polimi.ingsw.server.model.Color;

public class InGameTestMain {

    public static void main(String[] args){

        InGameGui GUI = new InGameGui();

        GUI.printInGameMessage("sonia");
        GUI.printSecondaryInGameMessage("raffi");
        GUI.showChosenGods(new ModelMessage(ModelMessageType.GODHASBEENCHOSEN, ModelError.NONE, "demeter", true, "sonia", Color.GREEN), true);
        GUI.showChosenGods(new ModelMessage(ModelMessageType.GODHASBEENCHOSEN, ModelError.NONE, "athena", true, "sonia", Color.GREEN), false);
        GUI.showChosenGods(new ModelMessage(ModelMessageType.GODHASBEENCHOSEN, ModelError.NONE, "atlas", true, "gigio", Color.RED), false);
        GUI.setInGameGuiVisible(true);

    }

}
