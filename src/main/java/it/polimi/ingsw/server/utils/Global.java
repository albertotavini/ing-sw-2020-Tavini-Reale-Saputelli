package it.polimi.ingsw.server.utils;

//this entire class is created to save global constats

import java.io.Serializable;

public class Global implements Serializable {

    public static final int DIM = 5;

    //Gods' cathegories
    public static final String ON_MOVE = "ON_MOVE";
    public static final String ON_BUILD = "ON_BUILD";
    public static final String ON_OPPONENT = "ON_OPPONENT";
    public static final String ON_SETUP = "ON_SETUP";
    public static final String ON_NEEDCONFIRMATION = "ON_NEEDCONFIRMATION";             //on_need... doesn't make sense, but coherence is more important

    //Gods' names
    public static final String APOLLO = "APOLLO";
    public static final String ARTEMIS = "ARTEMIS";
    public static final String ATHENA = "ATHENA";
    public static final String ATLAS = "ATLAS";
    public static final String DEMETER = "DEMETER";
    public static final String HEPHAESTUS = "HEPHAESTUS";
    public static final String MINOTAUR = "MINOTAUR";
    public static final String PAN = "PAN";
    public static final String PROMETHEUS = "PROMETHEUS";
    public static final String TRITON = "TRITON";
    public static final String HESTIA = "HESTIA";
    public static final String ARES = "ARES";
    public static final String ZEUS = "ZEUS";
    public static final String CHRONUS = "CHRONUS";


    //Gods' descriptions
    public static final String APOLLO_DESCRIPTION = "Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.";
    public static final String ARTEMIS_DESCRIPTION = "Your Worker may move one additional time, but not back to its initial space.";
    public static final String ATHENA_DESCRIPTION = "If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.";
    public static final String ATLAS_DESCRIPTION = "Your Worker may build a dome at any level.";
    public static final String DEMETER_DESCRIPTION = "Your Worker may build one additional time, but not on the same space.";
    public static final String HEPHAESTUS_DESCRIPTION = "Your Worker may build one additional block (not dome) on top of your first block.";
    public static final String MINOTAUR_DESCRIPTION = "Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.";
    public static final String PAN_DESCRIPTION = "You also win if your Worker moves down two or more levels.";
    public static final String PROMETHEUS_DESCRIPTION = "If your Worker does not move up, it may build both before and after moving.";
    public static final String TRITON_DESCRIPTION = "Each time your worker moves in a perimeter space it may immediately move again";
    public static final String HESTIA_DESCRIPTION = "Your worker may build one additional time, but this cannot be on perimeter space";
    public static final String ARES_DESCRIPTION = "End of your turn: you may remove an unoccupied block (not dome) neaighboring your unmoved worker";
    public static final String ZEUS_DESCRIPTION = "Your worker may build a block under itself";
    public static final String CHRONUS_DESCRIPTION = "You also win when there are at least five complete towers on the board";



    public static final String GODS_YOU_CAN_CHOSE_FROM =
                    "\n"+ APOLLO +": "+ APOLLO_DESCRIPTION +"\n"+ ARTEMIS +": "+ ARTEMIS_DESCRIPTION +"\n"+ ATHENA +": "+ ATHENA_DESCRIPTION +"\n"
                    + ATLAS +": "+ ATLAS_DESCRIPTION +"\n"+ DEMETER +": "+ DEMETER_DESCRIPTION +"\n"+ HEPHAESTUS +": "+ HEPHAESTUS_DESCRIPTION +"\n"
                    + MINOTAUR +": "+ MINOTAUR_DESCRIPTION +"\n"+ PAN +": "+ PAN_DESCRIPTION +"\n"+ PROMETHEUS +": "+ PROMETHEUS_DESCRIPTION +"\n"
                    + TRITON +": "+ TRITON_DESCRIPTION +"\n"+ HESTIA +": "+ HESTIA_DESCRIPTION +"\n"+ ARES +": "+ ARES_DESCRIPTION +"\n"+ ZEUS +": "+ ZEUS_DESCRIPTION +"\n"+ CHRONUS +": "+ CHRONUS_DESCRIPTION;



    public static final String SANTORINI =
        "\n\n\n\n" +
        "███████╗ █████╗ ███╗   ██╗████████╗ ██████╗ ██████╗ ██╗███╗   ██╗██╗    \n" +
        "██╔════╝██╔══██╗████╗  ██║╚══██╔══╝██╔═══██╗██╔══██╗██║████╗  ██║██║    \n" +
        "███████╗███████║██╔██╗ ██║   ██║   ██║   ██║██████╔╝██║██╔██╗ ██║██║    \n" +
        "╚════██║██╔══██║██║╚██╗██║   ██║   ██║   ██║██╔══██╗██║██║╚██╗██║██║    \n" +
        "███████║██║  ██║██║ ╚████║   ██║   ╚██████╔╝██║  ██║██║██║ ╚████║██║    \n" +
        "╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝╚═╝    \n" +
        "un gioco Cranio Creations adattato da TRS                               \n" +
        "\n";

    public static final String CHOOSEYOURGOD = ", choose your God between ";
    public static final String AND = " and ";
    public static final String YOUHAVETOCHOOSE = ", you have to choose ";
    public static final String GODSHAVEBEENCHOSEN = "Gods have been chosen.\n" + "We are now in the place part.\n" + "The youngest begins.\n";




}
