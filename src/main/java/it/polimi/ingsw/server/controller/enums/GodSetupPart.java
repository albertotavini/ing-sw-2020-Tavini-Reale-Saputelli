package it.polimi.ingsw.server.controller.enums;

/**
 * this enum is used to divide the selection of god's cards in the method ChooseGods
 * INITIALCHOICE is the part where the youngest selects 2/3 gods cards that will be used in the game
 * OLDERCHOOSES is the part where the oldest player picks his card among the 2/3 chosen
 * OTHERCHOOSES is the part where the the youngest in a 2 player match or the middel one in a 3 player match picks is card
 * in this part, if we are in a three player match, the last player doesn't need to say anything and his god is automatically assigned
 */
public enum GodSetupPart {
    INITIALCHOICE,
    OLDERCHOOSES,
    OTHERCHOOSES
}
