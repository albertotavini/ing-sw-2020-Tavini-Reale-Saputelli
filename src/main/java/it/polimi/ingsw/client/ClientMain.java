package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.utils.CliAesthetics;
import it.polimi.ingsw.bothsides.utils.Global;


import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain {

    private static SocketChannel normalChannel;
    private static SocketChannel errorChannel;
    private static SocketChannel chatChannel1;
    private static SocketChannel chatChannel2;

    static final Scanner scannerIn = new Scanner(System.in);
    static final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    static MenuUserInterface menuUi = null;
    static InGameUserInterface inGameUi = null;

    static String serverIpAddress;
    static int serverPortStandard = 6700;
    static int serverPortError = 6701;
    static int serverPortChat1 = 6702;
    static int serverPortChat2 = 6703;


    public static void main(String[] args) {



        if(!verifyArguments(args)) {

            System.err.printf("Wrong arguments! Closing the application");
            System.exit(-1);


        }


        //prints a message on the cli
        if(args[0].equals("c")) printWelcome();


        try {

            openConnectionChannels();

            setTypeOfUserInterface(args[0]);

            serverIpAddress = args[1];

            if(!verifyIpAddress(serverIpAddress)) {

                ClientViewAdapter.printMenuMessage("Wrong ip address, closing the application");
                System.exit(-1);

            }

            initiateStandardCommunication();

            clientExecutor.shutdown();
            if (!clientExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) clientExecutor.shutdownNow();


            ClientViewAdapter.printMenuMessage(Global.CONNECTIONWASCLOSEDSUCCESSFULLY);

            System.exit(0);


        }catch(ConnectException ex){

            ClientViewAdapter.printMenuMessage(Global.ICOULDNOTCONNECTTOTHESERVERFORSTANDARDCONNECTION);
            System.exit(-1);

        } catch (Exception e) {

            ClientViewAdapter.printMenuMessage(Global.OPSIDISCONNECTED);
            Thread.currentThread().interrupt();
            // e.printStackTrace()
        }



    }




    private static void printWelcome() {

        System.out.println("\n\n\n\n\n\n\n\n" + Global.WELCOMETO);

        System.out.println("\n\n\n" + Global.SANTORINI);
        System.out.println(Global.ATRSPRODUCTION +"\n\n");

        System.out.println(GreekPhrases.giveCasualQuote() +"\n");





    }


    static class GreekPhrases {


        private static String[] phraseArray = {"Man is the measure of all things.\n" +
                "-  Protagoras, 487-412 BC, Ancient Greek sophist", "Man is the dream of a shadow.\n" +
                "-  Pindar, 522-438 BC, Ancient Greek lyric poet", "As mortals we should behave as mortals.\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Alcestis", "Man: one who thinks of what he sees.\n" +
                "-  Plato, 427-347 BC, Ancient Greek philosopher", "We became men once and one cannot become twice.\n" +
                "-  Epicurus, 341-270 BC, Ancient Greek philosopher", "Man is naturally deceitful ever, in every way!\n" +
                "-  Aristophanes, 445-386 BC, Ancient Greek comic playwright ‐ Birds", "Man is a microcosm.\n" +
                "-  Democritus, 470-370 BC, Ancient Greek philosopher", "This world, which is the same for all, has not been made by any god or man, but it always has been, is, and will be an ever-living fire.\n" +
                "-  Heraclitus, 544-484 BC, Ancient Greek philosopher", "The world is wonderful, because it is a creation of God.\n" +
                "-  Thales of Miletus, 643-548 BC, Ancient Greek philosopher", "World is decay, life is perception.\n" +
                "-  Democritus, 470-370 BC, Ancient Greek philosopher", "When asked if the shape of the world is a sphere, Socrates replied: “I didn’t bend so much.”\n" +
                "-  Socrates, 469-399 BC, Ancient Geek Philosopher", "There cannot be good without evil, but in their mixture things may turn out well.\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Aeolus", "Pleasure, a most mighty lure to evil.\n" +
                "-  Plato, 427-347 BC, Ancient Greek philosopher", "How sweet is life when fortune is not envious.\n" +
                "-  Menander, 4th cent. BC, Ancient Greek dramatist (New Comedy)", "God needs nothing.\n" +
                "-  Ancient Greek phrase", "God is the most ancient of all things, for he had no birth.\n" +
                "-  Thales of Miletus, 643-548 BC, Ancient Greek philosopher", "If gods do evil, they are not gods.\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Iphigeneia in Tauris", "For the gods exist, since the knowledge about them is obvious.\n" +
                "-  Epicurus, 341-270 BC, Ancient Greek philosopher", "Nothing is clear to men about the gods.\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Heracles", "According to law, there are many gods; according to nature, only one.\n" +
                "-  Antisthenes, 445-360 BC, Ancient Greek Cynic philosopher", "All things are full of gods.\n" +
                "-  Thales of Miletus, 643-548 BC, Ancient Greek philosopher", "What is god or what is not god or what is in between who among searching mortals can assert?\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Helen", "God is never, in no way unjust.\n" +
                "-  Plato, 427-347 BC, Ancient Greek philosopher", "Zeus whoever he may ever be.\n" +
                "-  Aeschylus, 525-456 BC, Ancient Greek tragedian ‐ Agamemnon", "It is the mark of a divine mind to be always contemplating something noble.\n" +
                "-  Democritus, 470-370 BC, Ancient Greek philosopher", "The great happiness is not lasting among mortals.\n" +
                "-  Euripides, 480-406 BC, Ancient Greek tragedian ‐ Orestes", "Untimely pleasures produce aversion.\n" +
                "-  Democritus, 470-370 BC, Ancient Greek philosopher", "Time is the healer of all inevitable ills.\n" +
                "-  Menander, 4th cent. BC, Ancient Greek dramatist (New Comedy)", "They say that time is by nature wise.\n" +
                "-  Agathon, 450-400 BC, Ancient Greek tragic poet", "Since life is short, don’t make it long with bad deeds.\n" +
                "-  Thales of Miletus, 643-548 BC, Ancient Greek philosopher"};




        private static String giveCasualQuote() {

            return phraseArray[new Random().nextInt(phraseArray.length)];

        }





    }

    private static void setTypeOfUserInterface(String guiOrCli) {

        guiOrCli = guiOrCli.toUpperCase();

        if (guiOrCli.equals("G")) {

            /*try {
                UIManager.setLookAndFeel( new FlatDarculaLaf());
            } catch( Exception ex ) {
                System.err.println(Global.FAILEDTOINITIALIZELAF);
            }*/

            menuUi = new MenuGui();
            inGameUi = new InGameGui();
        }

        //l'utente ha scelto di usare la cli
        else {

            menuUi = new MenuCli();
            inGameUi = new InGameCli();

        }


        ClientViewAdapter.setTypeInterface(menuUi, inGameUi);

    }


    private static boolean verifyArguments(String[] args){

        if(args.length != 2){ return false; }

        if(!(args[0].equals("c") || args[0].equals("g"))) {
            System.err.printf("\nInvalid gui or cli parameter\n");
            return false; }

        if(!verifyIpAddress(args[1])){
            System.err.printf("\nInvalid ip\n");
            return false; }

        else return true;





    }

    private static boolean verifyIpAddress(String ip){

        String regexIp =  "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        Pattern ipPattern = Pattern.compile(regexIp);
        Matcher matcherIp = ipPattern.matcher(ip);

        if(matcherIp.find()) {

            return true;
        }

        else return false;
    }

    private static void openConnectionChannels() throws IOException {

        normalChannel = SocketChannel.open();
        errorChannel = SocketChannel.open();
        chatChannel1 = SocketChannel.open();
        chatChannel2 = SocketChannel.open();

        normalChannel.configureBlocking(true);
        errorChannel.configureBlocking(true);
        chatChannel1.configureBlocking(true);
        chatChannel2.configureBlocking(true);


    }

    public static void closeConnectionChannels() throws IOException {

        normalChannel.close();
        errorChannel.close();
        chatChannel1.close();
        chatChannel2.close();

    }

    private static void initiateStandardCommunication() throws IOException {

        if (normalChannel.connect(new InetSocketAddress(serverIpAddress, serverPortStandard))) {

            ClientFsm clientFsm = new ClientFsm(normalChannel.socket());
            ClientViewAdapter.setClientFsm(clientFsm);
            clientFsm.run();

        }

    }

    static SocketChannel getErrorChannel() {
        return errorChannel;

    }

    static SocketChannel getChatChannel1(){return chatChannel1;}

    static SocketChannel getChatChannel2(){return chatChannel2;}



}