package it.polimi.ingsw.server.TRS_TP;

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
        return "IdentityCardOfPlayer{" +
                "playerName='" + playerName + '\'' +
                ", dateOfBirthday=" + dateOfBirthday +
                ", uniquePlayerCode='" + uniquePlayerCode + '\'' +
                '}';
    }
}
