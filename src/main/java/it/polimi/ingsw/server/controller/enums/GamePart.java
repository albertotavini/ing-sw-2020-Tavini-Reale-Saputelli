package it.polimi.ingsw.server.controller.enums;

/**
 * this enum regulates the functioning of update in controller
 * basing on the GamePart, the update knows in which part of the game it is
 * GOD is used for setting divinity cards
 * PLACE1/2/3 is used for letting the 2/3 players place their workers
 * the game then remains in TURN until someone wins, then it becomes CONCLUSION
 *
 */
public enum GamePart {
    GOD,
    PLACE1,
    PLACE2,
    PLACE3,
    TURN,
    CONCLUSION
}
