package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.utils.ColorAnsi;

public enum Color {
    GREEN(ColorAnsi.GREEN +"G"),
    RED (ColorAnsi.RED +"R"),
    YELLOW (ColorAnsi.YELLOW +"Y");
    private String abbreviation;

    private Color(String abbrev) {
        abbreviation = abbrev;
    }
    public String abbrev() {
        return abbreviation;
    }

}
