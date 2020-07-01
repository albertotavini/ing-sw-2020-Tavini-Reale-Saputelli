package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.model.Date;

/**
 * this is the the class that stores info about a certain user that has connected through a client
 * the name is UNIQUE
 * and so is the uniquePlayerCode
 * two players with the same name will not be accepted by server
 *
 */
public class IdentityCardOfPlayer {

    private final String playerName;
    private final Date dateOfBirthday;
    private final String uniquePlayerCode;


    public IdentityCardOfPlayer(String playerName, Date dateOfBirthday, String uniquePlayerCode) {

        this.playerName = playerName;
        this.dateOfBirthday = dateOfBirthday;
        this.uniquePlayerCode = uniquePlayerCode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }

    public String getUniquePlayerCode() {
        return uniquePlayerCode;
    }


    /**
     * something similar to an equals but not exactly the same
     *
     * @param identity another of this object
     * @return true if they have same name and uniqueCode
     */
    public boolean isTheSameOf(IdentityCardOfPlayer identity) {

        return identity.getPlayerName().equals(this.playerName) && identity.getUniquePlayerCode().equals(this.uniquePlayerCode);

    }

    @Override
    public String toString() {
        return Global.IDENTITYCARDOFPLAYER +
                Global.PLAYERNAME + playerName + Global.BACKSLASH +
                Global.DATEOFBIRTHDAY + dateOfBirthday +
                Global.UNIQUEPLAYERCODE + uniquePlayerCode + Global.BACKSLASH +
                Global.CLOSINGBRACE;
    }
}
