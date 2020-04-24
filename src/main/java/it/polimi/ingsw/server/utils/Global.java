package it.polimi.ingsw.server.utils;

//this entire class is created to save global constats

import java.io.Serializable;

public class Global implements Serializable {

    //Gods' cathegories
    public static final String on_move = "ON_MOVE";
    public static final String on_build = "ON_BUILD";
    public static final String on_opponent = "ON_OPPONENT";
    public static final String on_setup = "ON_SETUP";
    public static final String on_needconfirmation = "ON_NEEDCONFIRMATION";             //on_need... doesn't make sense, but coherence is more important

    //Gods' names
    public static final String apollo = "APOLLO";
    public static final String artemis = "ARTEMIS";
    public static final String athena = "ATHENA";
    public static final String atlas = "ATLAS";
    public static final String demeter = "DEMETER";
    public static final String hephaestus = "HEPHAESTUS";
    public static final String minotaur = "MINOTAUR";
    public static final String pan = "PAN";
    public static final String prometheus = "PROMETHEUS";


    //Gods' descriptions
    public static final String apolloDescription = "Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.";
    public static final String artemisDescription = "Your Worker may move one additional time, but not back to its initial space.";
    public static final String athenaDescription = "If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.";
    public static final String atlasDescription = "Your Worker may build a dome at any level.";
    public static final String demeterDescription = "Your Worker may build one additional time, but not on the same space.";
    public static final String hephaestusDescription = "Your Worker may build one additional block (not dome) on top of your first block.";
    public static final String minotaurDescription = "Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.";
    public static final String panDescription = "You also win if your Worker moves down two or more levels.";
    public static final String prometheusDescription = "If your Worker does not move up, it may build both before and after moving.";

    public static final String godsYouCanChoseFrom =
                    "\n"+apollo+": "+apolloDescription+"\n"+artemis+": "+artemisDescription+"\n"+athena+": "+athenaDescription+"\n"
                    +atlas+": "+atlasDescription+"\n"+demeter+": "+demeterDescription+"\n"+hephaestus+": "+hephaestusDescription+"\n"
                    +minotaur+": "+minotaurDescription+"\n"+pan+": "+panDescription+"\n"+prometheus+": "+prometheusDescription+"\n";



    public static final String santorini =
        "\n\n\n\n" +
        "███████╗ █████╗ ███╗   ██╗████████╗ ██████╗ ██████╗ ██╗███╗   ██╗██╗    \n" +
        "██╔════╝██╔══██╗████╗  ██║╚══██╔══╝██╔═══██╗██╔══██╗██║████╗  ██║██║    \n" +
        "███████╗███████║██╔██╗ ██║   ██║   ██║   ██║██████╔╝██║██╔██╗ ██║██║    \n" +
        "╚════██║██╔══██║██║╚██╗██║   ██║   ██║   ██║██╔══██╗██║██║╚██╗██║██║    \n" +
        "███████║██║  ██║██║ ╚████║   ██║   ╚██████╔╝██║  ██║██║██║ ╚████║██║    \n" +
        "╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝╚═╝    \n" +
        "un gioco Cranio Creations adattato da TRS                               \n" +
        "\n";





}
