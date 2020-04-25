package it.polimi.ingsw.server.TRS_TP;

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

    public String getUniquePlayerCode() {
        return uniquePlayerCode;
    }

    public boolean equals(IdentityCardOfPlayer identity) {

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
