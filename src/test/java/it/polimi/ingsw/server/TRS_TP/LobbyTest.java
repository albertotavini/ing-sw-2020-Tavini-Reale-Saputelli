package it.polimi.ingsw.server.TRS_TP;

/*import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;



public class LobbyTest {

    public void istanzioSocket() throws IOException {

        ExecutorService serverExecutor = Executors.newCachedThreadPool();

        ServerThread serverThread = new ServerThread(6700, 6701);
        Socket clientSocket;

        try {

            clientSocket = serverThread.getSocketAccept().accept();
            serverExecutor.submit(new MenuFsmServerSingleClientHandler(clientSocket, "123"));


        } catch (Exception e) {
            serverThreadReference.stopServer();
        }


    }while(serverThreadReference.isActive);


}
    }

    @Test
    public void addFsmClientHandlerToListTest() throws IOException {

        HashMap<IdentityCardOfPlayer, MenuFsmServerSingleClientHandler> correlationMap = new HashMap<>();

        MenuFsmServerSingleClientHandler fsm1 = new MenuFsmServerSingleClientHandler(new Socket(), "123");
        Lobby lobby = new PublicLobby("Lobby", "Franco", 2, fsm1);
        assertTrue(lobby.addFsmClientHandlerToList(fsm1));

    }

}*/