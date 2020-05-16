package it.polimi.ingsw.server.transmissionprotocol;

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