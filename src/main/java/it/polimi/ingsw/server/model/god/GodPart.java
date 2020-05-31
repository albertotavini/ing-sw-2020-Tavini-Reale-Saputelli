package it.polimi.ingsw.server.model.god;

/**
 * this enum is used to implement god's effects which are made of different parts
 * for example artemis or demeter, that can move/build two times
 * generally part One is used for receiving confirmation of the will to use or not use the effect, while two or three are the YES steps and
 * four is used when the player says no
 */
public enum GodPart {
    ONE,
    TWO,
    THREE,
    FOUR
}
