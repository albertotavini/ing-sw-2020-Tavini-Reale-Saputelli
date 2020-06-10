package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

/**
 *the that describes eventual errors that led the model to refuse input inserted by the player
 *NONE is a sort of default case that is set when no error has been made
 */
public enum ModelError {
    OUTOFBOUND,
    TOOFAR,
    TOOHIGH,
    TOOHIGHATHENA,
    CURRENTBOX,
    WORKERTHERE,
    DOMETHERE,
    NOTYOURWORKERTHERE,
    INCORRECTGODNAME,
    SAMEBOX,
    THEREISNOBOXTOREMOVE,
    EFFECTCANTBEUSED,
    NONE;

    public String toString(){
        switch (this) {
            case NONE:
                return " ";

            case OUTOFBOUND:
                return "You chose coordinates out of the board";

            case CURRENTBOX:
                return "You chose the same box where your worker is";

            case TOOFAR:
                return "You chose a box too distant for your worker";

            case TOOHIGH:
                return "You cannot go up that many levels";

            case TOOHIGHATHENA:
                return "Athena's effect was activated, you cannot go up";

            case DOMETHERE:
                return "The box you chose has a dome on it";

            case WORKERTHERE:
                return "The box you chose is occupied by another worker";

            case NOTYOURWORKERTHERE:
                return "Not occupied by one of your workers, or the worker cannot move";

            case INCORRECTGODNAME:
                return "The god you asked for is not valid";

            case SAMEBOX:
                return "It's the same box you chose before";

            case THEREISNOBOXTOREMOVE:
                return "You cannot remove any block from there";

            case EFFECTCANTBEUSED:
                return "God's power cannot be used!";

            default:
                return "";
        }
    }
}
