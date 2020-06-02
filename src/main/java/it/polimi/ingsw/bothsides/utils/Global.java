package it.polimi.ingsw.bothsides.utils;

//this entire class is created to save global constats

import java.io.Serializable;

/**
 * this class is used to set a series of numerical and string constants that are used through the whole code
 */
public class Global implements Serializable {


    public static final String CONFIGPROPERTIES = "config.properties";

    private static Configuration configGlobal= new Configuration();

    public static final String GODSJSONFILE = configGlobal.getGenericStringFromConfig("GODSJSONFILE");

    public static final int BOARD_DIM = configGlobal.getGenericIntFromConfig("BOARD_DIM");
    public static final int INVALID_BOX = configGlobal.getGenericIntFromConfig("INVALID_BOX");
    public static final int WINNINGLEVEL = configGlobal.getGenericIntFromConfig("WINNINGLEVEL");
    public static final int NUMOFWORKERS = configGlobal.getGenericIntFromConfig("NUMOFWORKERS");

    public static final int JBUTTONDIM = configGlobal.getGenericIntFromConfig("JBUTTONDIM");
    public static final int INGAMEGUIWIDTH = configGlobal.getGenericIntFromConfig("INGAMEGUIWIDTH");
    public static final int INGAMEGUIHEIGHT = configGlobal.getGenericIntFromConfig("INGAMEGUIHEIGHT");
    public static final int JTEXTSIZE = configGlobal.getGenericIntFromConfig("JTEXTSIZE");
    public static final int JGODCARDHEIGHT = configGlobal.getGenericIntFromConfig("JGODCARDHEIGHT");
    public static final int JGODCARDWIDTH = configGlobal.getGenericIntFromConfig("JGODCARDWIDTH");
    public static final int JCHATTEXTSIZE = configGlobal.getGenericIntFromConfig("JCHATTEXTSIZE");
    public static final int INGAMETEXTSIZE = configGlobal.getGenericIntFromConfig("INGAMETEXTSIZE");
    public static final int JCHOSENGODSTEXTPANELHEIGHT = configGlobal.getGenericIntFromConfig("JCHOSENGODSTEXTPANELHEIGHT");
    public static final int JCHOSENGODSTEXTPANELWIDTH = configGlobal.getGenericIntFromConfig("JCHOSENGODSTEXTPANELWIDTH");

    //wildly used
    public static final String SPACE = " ";
    public static final String BACKSLASHN = " \n";
    public static final String DOT = ". ";
    public static final String COMMA = ", ";
    public static final String A =  "A";
    public static final String B =  "B";


    //Gods' categories
    public static final String ON_MOVE = configGlobal.getGenericStringFromConfig("ON_MOVE");
    public static final String ON_BUILD = configGlobal.getGenericStringFromConfig("ON_BUILD");
    public static final String ON_OPPONENT = configGlobal.getGenericStringFromConfig("ON_OPPONENT");
    public static final String ON_SETUP = configGlobal.getGenericStringFromConfig("ON_SETUP");
    public static final String ON_NEEDCONFIRMATION = configGlobal.getGenericStringFromConfig("ON_NEEDCONFIRMATION");             //on_need... doesn't make sense, but coherence is more important

    //Gods' names
    public static final String APOLLO = configGlobal.getGenericStringFromConfig("APOLLO");
    public static final String ARTEMIS = configGlobal.getGenericStringFromConfig("ARTEMIS");
    public static final String ATHENA = configGlobal.getGenericStringFromConfig("ATHENA");
    public static final String ATLAS = configGlobal.getGenericStringFromConfig("ATLAS");
    public static final String DEMETER = configGlobal.getGenericStringFromConfig("DEMETER");
    public static final String HEPHAESTUS = configGlobal.getGenericStringFromConfig("HEPHAESTUS");
    public static final String MINOTAUR = configGlobal.getGenericStringFromConfig("MINOTAUR");
    public static final String PAN = configGlobal.getGenericStringFromConfig("PAN");
    public static final String PROMETHEUS = configGlobal.getGenericStringFromConfig("PROMETHEUS");
    public static final String TRITON = configGlobal.getGenericStringFromConfig("TRITON");
    public static final String HESTIA = configGlobal.getGenericStringFromConfig("HESTIA");
    public static final String ARES = configGlobal.getGenericStringFromConfig("ARES");
    public static final String ZEUS = configGlobal.getGenericStringFromConfig("ZEUS");
    public static final String CHRONUS = configGlobal.getGenericStringFromConfig("CHRONUS");


    //Gods' descriptions
    public static final String APOLLO_DESCRIPTION = configGlobal.getGenericStringFromConfig("APOLLO_DESCRIPTION");
    public static final String ARTEMIS_DESCRIPTION = configGlobal.getGenericStringFromConfig("ARTEMIS_DESCRIPTION");
    public static final String ATHENA_DESCRIPTION = configGlobal.getGenericStringFromConfig("ATHENA_DESCRIPTION");
    public static final String ATLAS_DESCRIPTION = configGlobal.getGenericStringFromConfig("ATLAS_DESCRIPTION");
    public static final String DEMETER_DESCRIPTION = configGlobal.getGenericStringFromConfig("DEMETER_DESCRIPTION");
    public static final String HEPHAESTUS_DESCRIPTION = configGlobal.getGenericStringFromConfig("HEPHAESTUS_DESCRIPTION");
    public static final String MINOTAUR_DESCRIPTION = configGlobal.getGenericStringFromConfig("MINOTAUR_DESCRIPTION");
    public static final String PAN_DESCRIPTION = configGlobal.getGenericStringFromConfig("PAN_DESCRIPTION");
    public static final String PROMETHEUS_DESCRIPTION = configGlobal.getGenericStringFromConfig("PROMETHEUS_DESCRIPTION");
    public static final String TRITON_DESCRIPTION = configGlobal.getGenericStringFromConfig("TRITON_DESCRIPTION");
    public static final String HESTIA_DESCRIPTION = configGlobal.getGenericStringFromConfig("HESTIA_DESCRIPTION");
    public static final String ARES_DESCRIPTION = configGlobal.getGenericStringFromConfig("ARES_DESCRIPTION");
    public static final String ZEUS_DESCRIPTION = configGlobal.getGenericStringFromConfig("ZEUS_DESCRIPTION");
    public static final String CHRONUS_DESCRIPTION = configGlobal.getGenericStringFromConfig("CHRONUS_DESCRIPTION");



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


    //constants for GodLookUpTalbe
    public static final String BUILDBEFOREMOVING = configGlobal.getGenericStringFromConfig("BUILDBEFOREMOVING");
    public static final String NOGOINGUP = configGlobal.getGenericStringFromConfig("NOGOINGUP");
    public static final String GODPOWERNOTALLOWED = configGlobal.getGenericStringFromConfig("GODPOWERNOTALLOWED");
    public static final String MOVEFIRSTTIME = configGlobal.getGenericStringFromConfig("MOVEFIRSTTIME");
    public static final String SECONDMOVE = configGlobal.getGenericStringFromConfig("SECONDMOVE");
    public static final String YOUCANBUILDADOME = configGlobal.getGenericStringFromConfig("YOUCANBUILDADOME");
    public static final String BUILDFIRSTTIME = configGlobal.getGenericStringFromConfig("BUILDFIRSTTIME");
    public static final String BUILDSECONDTIME = configGlobal.getGenericStringFromConfig("BUILDSECONDTIME");
    public static final String BUILDTWOBLOCKS = configGlobal.getGenericStringFromConfig("BUILDTWOBLOCKS");
    public static final String BUILDAGAIN = configGlobal.getGenericStringFromConfig("BUILDAGAIN");
    public static final String AGAINBUTNOTONPERIMETER = configGlobal.getGenericStringFromConfig("AGAINBUTNOTONPERIMETER");
    public static final String MOVEAGAIN = configGlobal.getGenericStringFromConfig("MOVEAGAIN");
    public static final String YOUCANREMOVEABLOCKNEAR = configGlobal.getGenericStringFromConfig("YOUCANREMOVEABLOCKNEAR");


    //constants for class Controller
    public static final String WELCOME = configGlobal.getGenericStringFromConfig("WELCOME");
    public static final String YOUARETHEYOUNGESTSELECT = configGlobal.getGenericStringFromConfig("YOUARETHEYOUNGESTSELECT");
    public static final String GODS = configGlobal.getGenericStringFromConfig("GODS");
    public static final String YOUHAVECHOSEN = configGlobal.getGenericStringFromConfig("YOUHAVECHOSEN");
    public static final String REMAININGGODSARE = configGlobal.getGenericStringFromConfig("REMAININGGODSARE");
    public static final String PLACEWORKERA = configGlobal.getGenericStringFromConfig("PLACEWORKERA");
    public static final String PLACEWORKERB = configGlobal.getGenericStringFromConfig("PLACEWORKERB");
    public static final String DOYOUWANTTOUSEGODEFFECT = configGlobal.getGenericStringFromConfig("DOYOUWANTTOUSEGODEFFECT");
    public static final String SELECTWHERETOMOVE = configGlobal.getGenericStringFromConfig("SELECTWHERETOMOVE");
    public static final String SELECTWHERETOBUILD = configGlobal.getGenericStringFromConfig("SELECTWHERETOBUILD");
    public static final String WORKERSCANTMOVE = configGlobal.getGenericStringFromConfig("WORKERSCANTMOVE");
    public static final String WORKERSCANTBUILD = configGlobal.getGenericStringFromConfig("WORKERSCANTBUILD");
    public static final String SELECTWORKER = configGlobal.getGenericStringFromConfig("SELECTWORKER");
    public static final String YOUSTART = configGlobal.getGenericStringFromConfig("YOUSTART");
    public static final String NOTYOURTURN = configGlobal.getGenericStringFromConfig("NOTYOURTURN");
    public static final String GAMEOVER = configGlobal.getGenericStringFromConfig("GAMEOVER");
    public static final String ISTHEWINNER = configGlobal.getGenericStringFromConfig("ISTHEWINNER");

    public static final String CHOOSEYOURGOD = configGlobal.getGenericStringFromConfig("CHOOSEYOURGOD");
    public static final String AND = configGlobal.getGenericStringFromConfig("AND");
    public static final String YOUHAVETOCHOOSE = configGlobal.getGenericStringFromConfig("YOUHAVETOCHOOSE");
    public static final String GODSHAVEBEENCHOSEN = configGlobal.getGenericStringFromConfig("GODSHAVEBEENCHOSEN");

    //constants for class AsciiArt
    public static final String SPACEFORASCIIART = configGlobal.getGenericStringFromConfig("SPACEFORASCIIART");

    //constants for class Configuration

    public static final String PROPERTYFILE = configGlobal.getGenericStringFromConfig("PROPERTYFILE");
    public static final String NOTFOUNDINTHECLASSPATH = configGlobal.getGenericStringFromConfig("NOTFOUNDINTHECLASSPATH");
    public static final String EXCEPTION = configGlobal.getGenericStringFromConfig("EXCEPTION");
    public static final String ERRORCONFIG = configGlobal.getGenericStringFromConfig("ERRORCONFIG");

    //constants for MenuGui
    public static final String INSERTUSERNAME = configGlobal.getGenericStringFromConfig("INSERTUSERNAME");
    public static final String INSERTBIRTHDATE = configGlobal.getGenericStringFromConfig("INSERTBIRTHDATE");
    public static final String DOYOUWANTTOCREATEANEWLOBBY = configGlobal.getGenericStringFromConfig("DOYOUWANTTOCREATEANEWLOBBY");
    public static final String DOYOUWANTTOJOINAPUBLICLOBBY = configGlobal.getGenericStringFromConfig("DOYOUWANTTOJOINAPUBLICLOBBY");


    //string constants for ServerFsm
    public static final String SERVERSETIDENTITYSTATE = configGlobal.getGenericStringFromConfig("SERVERSETIDENTITYSTATE");
    public static final String SERVERCREATEORPARTECIPATESTATE =configGlobal.getGenericStringFromConfig("SERVERCREATEORPARTECIPATESTATE");
    public static final String SERVERWAITINGINLOBBYSTATE = configGlobal.getGenericStringFromConfig("SERVERWAITINGINLOBBYSTATE");
    public static final String SERVERINGAMESTATE = configGlobal.getGenericStringFromConfig("SERVERINGAMESTATE");
    public static final String SERVERFINALSTATE = configGlobal.getGenericStringFromConfig("SERVERFINALSTATE");
    public static final String INCONSISTENTSTATE = configGlobal.getGenericStringFromConfig("INCONSISTENTSTATE");
    public static final String WHOIS = configGlobal.getGenericStringFromConfig("WHOIS");
    public static final String IDENTITYSET = configGlobal.getGenericStringFromConfig("IDENTITYSET");
    public static final String NAMEALREADYCHOSEN = configGlobal.getGenericStringFromConfig("NAMEALREADYCHOSEN");
    public static final String PRIVATELOBBYCREATED = configGlobal.getGenericStringFromConfig("PRIVATELOBBYCREATED");
    public static final String PUBLICLOBBYCREATED = configGlobal.getGenericStringFromConfig("PUBLICLOBBYCREATED");
    public static final String CASUALLOBBYCREATED = configGlobal.getGenericStringFromConfig("CASUALLOBBYCREATED");
    public static final String LOBBYNOTAVAILABLE = configGlobal.getGenericStringFromConfig("LOBBYNOTAVAILABLE");
    public static final String INCORRECTPASSWORD = configGlobal.getGenericStringFromConfig("INCORRECTPASSWORD");
    public static final String ADDEDTOLOBBY = configGlobal.getGenericStringFromConfig("ADDEDTOLOBBY");
    public static final String COMPLETEDLOBBY = configGlobal.getGenericStringFromConfig("COMPLETEDLOBBY");
    public static final String FSMNOTFUNCTIONING = configGlobal.getGenericStringFromConfig("FSMNOTFUNCTIONING");

    //string constants for ClientFSM
    public static final String CLIENTCLOSED = configGlobal.getGenericStringFromConfig("CLIENTCLOSED");
    public static final String INCORRECTPLAYERMOVE = configGlobal.getGenericStringFromConfig("INCORRECTPLAYERMOVE");
    public static final String WAITYOURTURN = configGlobal.getGenericStringFromConfig("WAITYOURTURN");
    public static final String YOUHAVEBEENDISCONNECTED = configGlobal.getGenericStringFromConfig("YOUHAVEBEENDISCONNECTED");
    public static final String READSERVERMESSAGEFAILED = configGlobal.getGenericStringFromConfig("READSERVERMESSAGEFAILED");
    public static final String NOTALLOWED = configGlobal.getGenericStringFromConfig("NOTALLOWED");
    public static final String TIMETOPLAY = configGlobal.getGenericStringFromConfig("TIMETOPLAY");
    public static final String WAITINGINLOBBY = configGlobal.getGenericStringFromConfig("WAITINGINLOBBY");
    public static final String HASDISCONNECTEDFROMLOBBY = configGlobal.getGenericStringFromConfig("HASDISCONNECTEDFROMLOBBY");
    public static final String HASJOINEDTHELOBBY = configGlobal.getGenericStringFromConfig("HASJOINEDTHELOBBY");
    public static final String LOBBYFULL = configGlobal.getGenericStringFromConfig("LOBBYFULL");
    public static final String HASHTAG = configGlobal.getGenericStringFromConfig("HASHTAG");;
    public static final String CHATERROR = configGlobal.getGenericStringFromConfig("CHATERROR");
    public static final String IDENTITYSTATECLIENTERROR = configGlobal.getGenericStringFromConfig("IDENTITYSTATECLIENTERROR");
    public static final String CHOOSEORCREATESTATECLIENTERROR = configGlobal.getGenericStringFromConfig("CHOOSEORCREATESTATECLIENTERROR");
    public static final String WAITINGSTATECLIENTERROR = configGlobal.getGenericStringFromConfig("WAITINGSTATECLIENTERROR");
    public static final String HANDLEMODELMESSAGEERROR = configGlobal.getGenericStringFromConfig("HANDLEMODELMESSAGEERROR");


    //strings to describe erroneous situations on the LOG that registers exceptions
    public static final String COULDNOTREMOVEFROMLOBBY = configGlobal.getGenericStringFromConfig("COULDNOTREMOVEFROMLOBBY");
    public static final String INCORRECTLOBBYOPTIONS = configGlobal.getGenericStringFromConfig("INCORRECTLOBBYOPTIONS");
    public static final String FSMDIDNOTKILLLOBBY = configGlobal.getGenericStringFromConfig("FSMDIDNOTKILLLOBBY");
    public static final String WAITINGINGAMESTATE = configGlobal.getGenericStringFromConfig("WAITINGINGAMESTATE");
    public static final String DISCONNECTEDIN = configGlobal.getGenericStringFromConfig("DISCONNECTEDIN");


    //strings for Model:
    //Board
    public static final String GIVEDIVINITYNAMES = configGlobal.getGenericStringFromConfig("GIVEDIVINITYNAMES");
    public static final String BOARDCLICOLUMNS = configGlobal.getGenericStringFromConfig("BOARDCLICOLUMNS");
    //Box
    public static final String ERROR = configGlobal.getGenericStringFromConfig("ERROR");
    //Color
    public static final String GREEN = configGlobal.getGenericStringFromConfig("GREEN");
    public static final String RED = configGlobal.getGenericStringFromConfig("RED");
    public static final String YELLOW = configGlobal.getGenericStringFromConfig("YELLOW");
    public static final String NONE = configGlobal.getGenericStringFromConfig("NONE");
    //Date
    public static final String NOTFORMATMATCHINGDATE = configGlobal.getGenericStringFromConfig("NOTFORMATMATCHINGDATE");
    public static final String SLASH = configGlobal.getGenericStringFromConfig("SLASH");
    //Model
    public static final String SELECTTHEWORKERTOMOVE = configGlobal.getGenericStringFromConfig("SELECTTHEWORKERTOMOVE");
    //Player
    public static final String PLAYERNAMED = configGlobal.getGenericStringFromConfig("PLAYERNAMED");
    public static final String BORNON = configGlobal.getGenericStringFromConfig("BORNON");












}
