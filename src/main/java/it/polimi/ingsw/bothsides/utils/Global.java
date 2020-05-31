package it.polimi.ingsw.bothsides.utils;

//this entire class is created to save global constats

import java.io.Serializable;

/**
 * this class is used to set a series of numerical and string constants that are used through the whole code
 */
public class Global implements Serializable {

    public static final int BOARD_DIM = 5;
    public static final int INVALID_BOX = -1;
    public static final int WINNINGLEVEL = 3;
    public static final int NUMOFWORKERS = 2;

    public static final int JBUTTONDIM = 150;
    public static final int INGAMEGUIWIDTH = 1200;
    public static final int INGAMEGUIHEIGHT = 700;
    public static final int JTEXTSIZE = 500;
    public static final int JGODCARDHEIGHT = 140;
    public static final int JGODCARDWIDTH = 84;
    public static final int JCHATTEXTSIZE = 15;
    public static final int INGAMETEXTSIZE = 15;
    public static final int JCHOSENGODSTEXTPANELHEIGHT = 80;
    public static final int JCHOSENGODSTEXTPANELWIDTH = 320;


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
                ColorAnsi.BLUE + "███████╗ █████╗ ███╗   ██╗████████╗ ██████╗ ██████╗ ██╗███╗   ██╗██╗    \n" + ColorAnsi.RESET+
    ColorAnsi.BLUE + "██╔════╝██╔══██╗████╗  ██║╚══██╔══╝██╔═══██╗██╔══██╗██║████╗  ██║██║    \n" + ColorAnsi.RESET+
        "███████╗███████║██╔██╗ ██║   ██║   ██║   ██║██████╔╝██║██╔██╗ ██║██║    \n" +
        "╚════██║██╔══██║██║╚██╗██║   ██║   ██║   ██║██╔══██╗██║██║╚██╗██║██║    \n" +
        "███████║██║  ██║██║ ╚████║   ██║   ╚██████╔╝██║  ██║██║██║ ╚████║██║    \n" +
        "╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝╚═╝    \n" +
        "un gioco Cranio Creations adattato da TRS                               \n" +
        "\n";

    //constants for class Controller
    public static final String CHOOSEYOURGOD = ", choose your God between ";
    public static final String AND = " and ";
    public static final String YOUHAVETOCHOOSE = ", you have to choose ";
    public static final String GODSHAVEBEENCHOSEN = "Gods have been chosen.\n" + "We are now in the place part.\n" + "The youngest begins.\n";

    //constants for class AsciiArt
    public static final String SPACEFORASCIIART = "                                                                                                                                                \n";

    //constants for class Configuration
    public static final String CONFIGPROPERTIES = "config.properties";
    public static final String PROPERTYFILE = "property file '";
    public static final String NOTFOUNDINTHECLASSPATH = "' not found in the classpath";
    public static final String EXCEPTION = "Exception: ";

    //constants for MenuGui
    public static final String INSERTUSERNAME = "Insert username:     ";
    public static final String INSERTBIRTHDATE = "Insert your birthdate:    ";
    public static final String DOYOUWANTTOCREATEANEWLOBBY = "Do you want to create a new lobby?";
    public static final String DOYOUWANTTOJOINAPUBLICLOBBY = "Do you want to join a public lobby?";


    //string constants for ServerFsm
    public static final String SERVERSETIDENTITYSTATE = "ServerSetIdentityState";
    public static final String SERVERCREATEORPARTECIPATESTATE ="CreateOrPartecipateState";
    public static final String SERVERWAITINGINLOBBYSTATE = "ServerWaitingInLobbyState";
    public static final String SERVERINGAMESTATE = "ServerInGameState";
    public static final String SERVERFINALSTATE = "ServerFinalState";
    public static final String INCONSISTENTSTATE = "Wrong state: ERRORR";
    public static final String WHOIS = "\n---Player ";
    public static final String IDENTITYSET = "Identity correctly set";
    public static final String NAMEALREADYCHOSEN = "Name has already been chosen, pick another one";
    public static final String PRIVATELOBBYCREATED = "Private lobby was successfully created";
    public static final String PUBLICLOBBYCREATED = "Public lobby successfully created";
    public static final String CASUALLOBBYCREATED = "Casual lobby was successfully created";
    public static final String LOBBYNOTAVAILABLE = "The Lobby doesn't exist or is already full";
    public static final String INCORRECTPASSWORD = "Password is not correct";
    public static final String ADDEDTOLOBBY = "You Were added to the lobby";
    public static final String COMPLETEDLOBBY = "The Lobby is complete, now the game can start";
    public static final String FSMNOTFUNCTIONING = "----FsmServer couldn't start----";


    //strings to describe erroneous situations on the LOG that registers exceptions
    public static final String COULDNOTREMOVEFROMLOBBY = "\n----Couldn't remove player from assigned lobby----";
    public static final String INCORRECTLOBBYOPTIONS = "----ServerFsm wasn't able to correctly choose lobby options";
    public static final String FSMDIDNOTKILLLOBBY = "----Server Fsm it wasn't able to kill the lobby";
    public static final String WAITINGINGAMESTATE = "I'm in InGame State waiting, i am: ";
    public static final String DISCONNECTEDIN = " has disconnected in";


}
