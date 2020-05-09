package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

/*public class InGameConnectionTest {

    //constructor
    public InGameConnection constructor(String uniquePlayerCode) throws IOException {
        ServerThread serverThread = new ServerThread(6700, 6701);
        Socket clientSocket = new Socket();
        serverCliThread.start();
        clientSocket = serverThread.socketAccept.accept();
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

        return new InGameConnection(clientSocket, uniquePlayerCode, oos, ois);
    }

    @Test
    public void runTest() throws DataFormatException, IOException {
        Date birthDate = new Date(20, 06, 2000);
        IdentityCardOfPlayer identityCardOfPlayer = new IdentityCardOfPlayer("Luigi", birthDate, "123");
        InGameConnection inGameConnection = constructor(identityCardOfPlayer.getUniquePlayerCode());

        assertFalse(inGameConnection.isConnected());

        inGameConnection.run();

        //assertTrue(inGameConnection.isConnected());
    }

}*/