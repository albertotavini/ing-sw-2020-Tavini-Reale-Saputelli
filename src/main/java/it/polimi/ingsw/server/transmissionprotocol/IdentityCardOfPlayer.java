package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.model.Date;

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
