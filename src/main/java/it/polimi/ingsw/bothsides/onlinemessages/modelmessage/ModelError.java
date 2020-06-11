package it.polimi.ingsw.bothsides.onlinemessages.modelmessage;

import it.polimi.ingsw.bothsides.utils.Global;

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
    ONPERIMETER,
    CRUCIALBOX,
    NONE;

    public String toString(){
        switch (this) {
            case NONE:
                return Global.SPACE;

            case OUTOFBOUND:
                return Global.MERROROUTOFBOUND;

            case CURRENTBOX:
                return Global.MERRORCURRENTBOX;

            case TOOFAR:
                return Global.MERRORTOOFAR;

            case TOOHIGH:
                return Global.MERRORTOOHIGH;

            case TOOHIGHATHENA:
                return Global.MERRORTOOHIGHATHENA;

            case DOMETHERE:
                return Global.MERRORDOMETHERE;

            case WORKERTHERE:
                return Global.MERRORWORKERTHERE;

            case NOTYOURWORKERTHERE:
                return Global.MERRORNOTYOURWORKERTHERE;

            case INCORRECTGODNAME:
                return Global.MERRORINCORRECTGODNAME;

            case SAMEBOX:
                return Global.MERRORSAMEBOX;

            case THEREISNOBOXTOREMOVE:
                return Global.MERRORTHEREISNOBOXTOREMOVE;

            case EFFECTCANTBEUSED:
                return Global.MERROREFFECTCANTBEUSED;

            case ONPERIMETER:
                return Global.MERRORONPERIMETER;

            case CRUCIALBOX:
                return Global.CRUCIALBOX;

            default:
                return Global.EMPTY;
        }
    }
}
