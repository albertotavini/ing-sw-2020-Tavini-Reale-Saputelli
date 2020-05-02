package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.utils.ColorAnsi;

public enum Color {
    GREEN(ColorAnsi.GREEN +"G" +ColorAnsi.RESET),
    RED (ColorAnsi.RED +"R" +ColorAnsi.RESET),
    YELLOW(ColorAnsi.YELLOW +"Y" +ColorAnsi.RESET);
    private String abbreviation;

    private Color(String abbrev) {
        abbreviation = abbrev;
    }
    public String abbrev() {
        return abbreviation;
    }

}
