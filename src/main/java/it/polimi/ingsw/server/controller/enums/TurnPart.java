package it.polimi.ingsw.server.controller.enums;

/**
 * this enum is used in performTurn to let the method understand if the input received is to be used to
 * select a worker
 * move the worker previously selected
 * build with the worker previously moved
 */
public enum TurnPart {
    SELECT,
    MOVE,
    BUILD
}
