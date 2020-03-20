package it.polimi.ingsw;

import it.polimi.ingsw.model.Player;
import org.junit.Test;

import java.util.zip.DataFormatException;

import static org.junit.Assert.assertTrue;

public class PlayerTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void youngerTest() throws DataFormatException {
        Player p1 =new Player("Ezio", 1, 2, 2000);
        Player p2 = new Player ("Marco", 1, 2, 3);
        System.out.println(" i giocatori sono " +p1+ " e " +p2);

    }
}