package it.polimi.ingsw.bothsides.utils;

public enum ColorAnsi {


    YELLOW("\u001B[33m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    RESET("\u001B[0m"),
    BRIGHTBLUE("\u001B[94m");

    private String escape;

    ColorAnsi(String escape){this.escape = escape;}

    public String getEscape() {
        return escape;
    }


    @Override
    public String toString() {
        return escape;
    }
}
