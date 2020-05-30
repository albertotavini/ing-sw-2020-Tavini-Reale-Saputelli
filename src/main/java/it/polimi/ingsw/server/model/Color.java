package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.utils.ColorAnsi;

/**
 * This enum represents all the possible Worker's colors: green, red and yellow.
 * Every player will have a different worker color in both the types of the matches.
 */
public enum Color {
    GREEN(ColorAnsi.GREEN +"G" +ColorAnsi.RESET),
    RED (ColorAnsi.RED +"R" +ColorAnsi.RESET),
    YELLOW(ColorAnsi.YELLOW +"Y" +ColorAnsi.RESET),
    NONE ("N");

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
