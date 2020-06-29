package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.utils.Global;

/**
 * This enum represents all the possible Worker's colors: green, red and yellow.
 * Every player will have a different worker color in both the types of the matches.
 */
public enum Color {
    GREEN(Global.GREEN),
    RED ( Global.RED),
    YELLOW( Global.YELLOW),
    NONE (Global.NONE);

    private String abbreviation;

    Color(String abbrev) {
        abbreviation = abbrev;
    }

    /**
     * @return the abbreviation of the current Color
     */
    public String abbrev() {
        return abbreviation;
    }

}
