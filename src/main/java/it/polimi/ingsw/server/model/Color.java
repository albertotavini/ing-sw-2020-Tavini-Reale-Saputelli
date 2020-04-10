package it.polimi.ingsw.server.model;

public enum Color {
    GREEN("G"),
    RED ("R"),
    YELLOW ("Y");
    private String abbreviation;

    private Color(String abbrev) {
        abbreviation = abbrev;
    }
    public String abbrev() {
        return abbreviation;
    }

}
