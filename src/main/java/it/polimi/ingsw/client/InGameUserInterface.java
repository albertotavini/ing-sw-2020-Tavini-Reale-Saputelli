package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.BoxPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.utils.CliAesthetics;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;
import it.polimi.ingsw.server.model.god.ListOfGodContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class is used to allow the user to interact with the game and insert input in the actual game
 */
public interface InGameUserInterface {


    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);
    void showBoard(BoardPhotography boardPhotography);
    void printInGameMessage(String message);
    void printSecondaryInGameMessage(String message);
    void showChosenGods(ModelMessage message, boolean yours);

}


/**
 * subclass used for the CLI version of the game, that takes input via scanner.nextline()
 */
class InGameCli implements InGameUserInterface {

    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();
    private String godsChosen = "";
    private boolean firstTime = true;

    /**
     * this method is used to send a coordinate input, it will parse what the user inserts and insert an invalid set of coordinates
     * if the input doesn't format properly the input
     *
     * @param message that specifies what will coords be used for
     * @return playermove to be sent to server
     */
    @Override
    public PlayerMove askForCoordinates(String message) {

        int row = Global.INVALID_BOX;
        int column = Global.INVALID_BOX;
        PlayerMove coordinates;
        String dataInput ;
        boolean correctInput;

        String[] ask = {Global.INSERTCOORDINATESXY};

        CliAesthetics.printBox(ask);

        dataInput = ClientMain.scannerIn.nextLine();

        String regexData = "^([0-4]),([0-4])$";
        Pattern playerMovePattern = Pattern.compile(regexData);
        Matcher matcherPlayerMove = playerMovePattern.matcher(dataInput);
        correctInput = matcherPlayerMove.find();


            if (correctInput) {

                row = Integer.parseInt(matcherPlayerMove.group(1));
                column = Integer.parseInt(matcherPlayerMove.group(2));

            }

        coordinates = PlayerMove.buildCoordPlayerMove(row, column, null);
        return coordinates;

    }

    /**
     * this method will ask input from the user until a Y or N is inserted, when this does it will generate a Confirmation
     * playermove accordingly to the input
     *
     * @param message with demands of the server
     * @return playermove to be sent to server
     */
    @Override
    public PlayerMove askForInGameConfirmation(String message) {


        String conferma;
        boolean correctInput;
        String regexData = "^([yn])$";
        Pattern confirmationPattern = Pattern.compile(regexData);
        Matcher matcherConfirmation;

        do {

            String[] ask = {Global.YORN};

            CliAesthetics.printBox(ask);

            conferma = ClientMain.scannerIn.nextLine();

            matcherConfirmation = confirmationPattern.matcher(conferma);
            correctInput = matcherConfirmation.find();

        }while(!correctInput);

            PlayerMove playerMoveConfirmation = PlayerMove.buildConfirmPlayerMove(ConfirmationEnum.NOTDEF, null);

            if (conferma.equals(Global.Y)) {
                playerMoveConfirmation = PlayerMove.buildConfirmPlayerMove(ConfirmationEnum.YES, null);

            } else if (conferma.equals(Global.N)) {

                playerMoveConfirmation = PlayerMove.buildConfirmPlayerMove(ConfirmationEnum.NO, null);

            }


            return playerMoveConfirmation;

    }

    /**
     * this method will simply generate a playermove with the string the player inserted, as parsing of the gods depends on the
     * json file that only the server has and no parse can be done on client
     *
     * being on cli the first time it will print a list of gods present in the game
     *
     * @param message that specifies among which you can choose
     * @return playermove with the string inserted
     */
    @Override
    public PlayerMove askForGodName(String message) {

        if (firstTime) {
            System.out.println(Global.GODS_YOU_CAN_CHOSE_FROM);
            firstTime = false;
        }


        System.out.println(Global.INSERTGODNAME);

        String godName = ClientMain.scannerIn.nextLine();
        godName = godName.toUpperCase();


        return PlayerMove.buildStringPlayerMove(godName, null);
    }

    /**
     * this method will simply print the board on the cli
     *
     * @param boardPhotography that has been updated
     */
    @Override
    public void showBoard(BoardPhotography boardPhotography) {

            BoxPhotography[][] matrix = boardPhotography.getMatrixPhotograph();

            System.out.println(Global.BACKSLASHN + "       "+Global.BOARDCLICOLUMNS);

            int rowIndex = 0;
            for (BoxPhotography[] line : matrix) {
                System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
                rowIndex++;
            }

            System.out.println(Global.BACKSLASHN+godsChosen);




        }

    /**
     * this method is simply used to print an eventual error's description
     *
     * @param message arrived with modelMessage
     */
    @Override
    public void printSecondaryInGameMessage(String message) {

        System.out.println(message);

    }

    /**
     * a simple print of the message given
     *
     * @param message from the server which tells what the game is expecting
     *
     */
    @Override
    public void printInGameMessage(String message) {

        System.out.println(message);
    }

    /**
     * this method will add info on what gods have been chosen to the string that is usually printed right after the board
     *
     * @param message with info on the gods chosen
     * @param yours true if the one who chose is the player who is handling this interface, false if not
     *
     */
    @Override
    public void showChosenGods(ModelMessage message, boolean yours){
        if (yours){
           godsChosen = godsChosen.concat(Global.YOUCHOSE +message.getMessage()+Global.BACKSLASHN);
        } else {
            godsChosen = godsChosen.concat(message.getColorOfTheCurrent() + Global.HASCHOSEN + message.getMessage()+Global.BACKSLASHN);
        }
    }

    void resetGodInfo() {
        godsChosen = "";
    }

}

/**
 * subclass used for the GUI version of the game
 */
class InGameGui extends JFrame implements InGameUserInterface {


    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();

    private ListOfGodContainer godDeck;

    //starts with setvisible false

    private CardLayout inGameCardLeftLayout = new CardLayout();
    private JPanel leftCardsPanel = new JPanel(inGameCardLeftLayout);

    private final String LEFTCARD_GAME_BUTTONS = Global.BUTTONSGAMEBUTTONS;

    private final InGameQuestionBooleanPanel inGameQuestionBooleanPanel;
    private final String IN_GAME_QUESTION_BOOLEN_PANEL = Global.QUESTIONBOOLEANPANEL;

    private final ChooseGodPanel chooseGodPanel;
    private final String CHOOSE_GOD_PANEL = Global.CHOOSEGODCARD;

    private final ChatPanel chatPanel;

    //panels to show on the righ side of guy gods chosen
    private JLabel your = new JLabel("");
    private JLabel yourGod = new JLabel("");
    private JLabel green = new JLabel("");
    private JLabel greenGod = new JLabel("");
    private JLabel red = new JLabel("");
    private JLabel redGod = new JLabel("");
    private JLabel yellow = new JLabel("");
    private JLabel yellowGod = new JLabel("");



    private JPanel rightPanel = new JPanel();
    private int buttonWidth = Global.JBUTTONDIM;
    private int buttonHeight = Global.JBUTTONDIM;
    private BoxButton[][] boxButtons = new BoxButton[Global.BOARD_DIM][Global.BOARD_DIM];

    private JTextArea whatToDoText = new JTextArea();
    private JTextArea errorText = new JTextArea();


    InGameGui() {
        //construction of the JFrame object InGameGui
        super(Global.SANTORINITHEGAME);


        try {
            godDeck = extractListOfGod();
        } catch (Exception e) {

            e.printStackTrace();
            printInGameMessage(Global.FATALERRORUNABLETOREADGOJSONFILE);
            Thread.currentThread().interrupt();
        }


        chooseGodPanel = new ChooseGodPanel();
        inGameQuestionBooleanPanel = new InGameQuestionBooleanPanel();
        chatPanel = new ChatPanel();
        JTabbedPane tabbedPane = new JTabbedPane();


        this.setSize(Global.INGAMEGUIWIDTH,Global.INGAMEGUIHEIGHT);
        //user can't resize the JFrame object
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //creating a Layout with the left part as the gameboard and the right part as a text area
        this.setLayout(new GridLayout(1,2));

        //setting panel: on this panel will be the gameboard
        JPanel leftCardPanelGameButtons = new JPanel();
        leftCardPanelGameButtons.setLayout(new GridLayout(Global.BOARD_DIM,Global.BOARD_DIM));
        for(int i = 0; i < Global.BOARD_DIM; i++){
            for(int j = 0; j < Global.BOARD_DIM; j++){
                boxButtons[i][j] = new BoxButton(clientBoardPhotography.getBox(i, j));
                (boxButtons[i][j]).setPreferredSize(new Dimension(buttonWidth,buttonHeight));
                leftCardPanelGameButtons.add(boxButtons[i][j]);
            }}


        //setting the text area on right panel
        whatToDoText.setLineWrap(true);
        whatToDoText.setWrapStyleWord(true);
        whatToDoText.setSize(Global.JTEXTSIZE,Global.JTEXTSIZE);
         rightPanel.add(whatToDoText);

        //setting the second textpanel
        errorText.setLineWrap(true);
        errorText.setWrapStyleWord(true);
        errorText.setSize(Global.JTEXTSIZE,Global.JTEXTSIZE);
        rightPanel.add(errorText);


        //adding panels on my JFrame InGameGui
        this.add(tabbedPane);
        tabbedPane.add(leftCardsPanel, Global.GAME);
        tabbedPane.add(chatPanel, Global.CHAT);
        leftCardsPanel.add(leftCardPanelGameButtons, LEFTCARD_GAME_BUTTONS);
        leftCardsPanel.add(inGameQuestionBooleanPanel, IN_GAME_QUESTION_BOOLEN_PANEL);
        leftCardsPanel.add(chooseGodPanel, CHOOSE_GOD_PANEL);
        this.add(rightPanel);


        this.setVisible(false);

    }
    private ListOfGodContainer extractListOfGod() throws IOException, ClassNotFoundException {

        ClassLoader classLoader = InGameUserInterface.class.getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(Global.GODSJSONFILE);

        ObjectInputStream objectinputstream = new ObjectInputStream(inputStream);

        ListOfGodContainer read = (ListOfGodContainer) objectinputstream.readObject();

        objectinputstream.close();

        return read;



    }
    private static class CollectorCoordinatesAnswer implements Runnable {

        private Coordinates coordinates = new Coordinates(Global.INVALID_BOX, Global.INVALID_BOX);
        private boolean isSleeping = false;


        @Override
        public void run() {


            try {

                waitCollector();

            } catch (InterruptedException e) {

                LogPrinter.printOnLog(e.toString());
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }




        }

        synchronized void waitCollector() throws InterruptedException {

            isSleeping = true;
            wait();

        }

        synchronized void notifyCollector(int row, int col){

            if(isSleeping) {

                this.coordinates.setRow(row);
                this.coordinates.setColumn(col);
                isSleeping = false;
                notifyAll();

            }
        }

        synchronized Coordinates giveCoordinates(){

            return coordinates;

        }

    }
    private static class Coordinates {

        private int row;
        private int column;

        private Coordinates(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setColumn(int column) {
            this.column = column;
        }


    }
    private boolean askGuiInGameConfirmation(String message) {

        inGameCardLeftLayout.show(leftCardsPanel, IN_GAME_QUESTION_BOOLEN_PANEL);
        return inGameQuestionBooleanPanel.askBooleanToQuestionPane(message);

    }
    void setInGameGuiVisible(boolean visible){
        this.setVisible(visible);
    }
    void updateChat(String message) {

        chatPanel.refreshChat(message);

    }
    private Image giveGodCardImage (String godName) {
        Image image;
        Image resizedImage;
        try {

            switch (godName.toUpperCase()) {
                case "APOLLO":
                    image = apolloIcon.getImage();
                    break;

                case "ARES":
                    image = aresIcon.getImage();
                    break;

                case "ARTEMIS":
                    image = artemisIcon.getImage();
                    break;

                case "ATHENA":
                    image = athenaIcon.getImage();
                    break;

                case "ATLAS":
                    image = atlasIcon.getImage();
                    break;

                case "CHRONUS":
                    image = chronusIcon.getImage();
                    break;

                case "DEMETER":
                    image = demeterIcon.getImage();
                    break;

                case "HEPHAESTUS":
                    image = hephaestusIcon.getImage();
                    break;

                case "HESTIA":
                    image = hestiaIcon.getImage();
                    break;

                case "MINOTAUR":
                    image = minotaurIcon.getImage();
                    break;

                case "PAN":
                    image = panIcon.getImage();
                    break;

                case "PROMETHEUS":
                    image = prometheusIcon.getImage();
                    break;

                case "TRITON":
                    image = tritonIcon.getImage();
                    break;

                case "ZEUS":
                    image = zeusIcon.getImage();
                    break;


                default:
                    image = errorIcon.getImage();
                    break;


            }
            resizedImage = image.getScaledInstance(Global.JGODCARDWIDTH,  Global.JGODCARDHEIGHT, java.awt.Image.SCALE_SMOOTH);
        } catch (Exception e) {
            LogPrinter.printOnLog(Global.FAILEDTODRWAGODSIMAGES);
            e.printStackTrace();
            resizedImage = null;
        }
        return resizedImage;
    }


    /**
     * method to resize images of god's cards
     *
     * @param icon to be resized
     * @return icon resized
     */
    private Icon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(Global.JGODCARDWIDTH, Global.JGODCARDHEIGHT,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }


    /**
     * this method is called when a match ends and the player wants to start a new one to clear the gui from
     * info on the precedent game
     */
    void resetInGameGui(){
        whatToDoText.setText(" ");
        errorText.setText(" ");
        chatPanel.areaMessages.setText(" ");
        your.setIcon(null);
        yourGod.setIcon(null);
        red.setIcon(null);
        redGod.setIcon(null);
        green.setIcon(null);
        greenGod.setIcon(null);
        yellow.setIcon(null);
        yellowGod.setIcon(null);
    }



    private static class InGameQuestionBooleanPanel extends JPanel{

        private final JTextPane question;
        private final InGameButtonConfirm buttonYes;
        private final InGameButtonConfirm buttonNo;
        private InGameAnswerCollector answerCollector = null;


        private InGameQuestionBooleanPanel() {

            this.setLayout(new GridLayout(8,1));

            question = new JTextPane();
            question.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            buttonYes = new InGameButtonConfirm("Yes", true);
            buttonNo = new InGameButtonConfirm("No", false);

            this.add(question);
            this.add(buttonYes);
            this.add(buttonNo);

        }

        private boolean askBooleanToQuestionPane(String message){

            question.setText(message);

            answerCollector = new InGameAnswerCollector();

            Thread collector = new Thread(answerCollector);

            collector.start();

            buttonNo.setButtonActiveTrue();
            buttonYes.setButtonActiveTrue();


            try {
                collector.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }


            boolean returnValue = (boolean) answerCollector.giveAnswer();

            answerCollector = null;

            return returnValue;
        }

        private class InGameButtonConfirm extends JButton implements ActionListener{

            private boolean isButtonActive = false;

            private final boolean buttonValue;

            private InGameButtonConfirm(String text, boolean buttonValue) {

                super(text);
                this.addActionListener(this);
                this.buttonValue = buttonValue;

            }

            private void setButtonActiveTrue() {

                this.isButtonActive = true;
            }

            @Override
            public void actionPerformed(ActionEvent e) {

                if(isButtonActive && answerCollector != null){

                    answerCollector.notifyCollector(buttonValue);

                    isButtonActive = false;

                }


            }
        }

    }

    private class ChooseGodPanel extends JPanel {

       private final ChooseGodButton[][] chooseGodButtons;



       private ChooseGodPanel() {

            int numberOfButtonsX = 0;

            //setting panel: on this panel will be the gameboard
            this.setLayout(new GridLayout(5,5));



            while ((numberOfButtonsX * numberOfButtonsX) < godDeck.getGodArrayList().size()) {
                numberOfButtonsX++;
            }



            chooseGodButtons = new ChooseGodButton[numberOfButtonsX][numberOfButtonsX];

            int numbOfGod = 0;

            for(int i = 0; i < numberOfButtonsX; i++) {
                for(int j = 0; j < numberOfButtonsX; j++){

                    if(numbOfGod < godDeck.getGodArrayList().size())
                    {
                        chooseGodButtons[i][j] = new ChooseGodButton(godDeck.getGodArrayList().get(numbOfGod).godName, godDeck.getGodArrayList().get(numbOfGod).godDescription);
                        (chooseGodButtons[i][j]).setPreferredSize(new Dimension(Global.GODBUTTONWIDTH, Global.GODBUTTONHEIGHT));
                        this.add(chooseGodButtons[i][j]);
                    }

                    numbOfGod++;

                }
            }


        }


       private class ChooseGodButton extends JButton implements MouseListener {



            private InGameAnswerCollector inGameAnswerCollector = null;
            private final String assignedGod;
            private final String assignedGodDescription;

            private boolean isButtonActive = false;


            private void setButtonActive(){

                this.isButtonActive = true;
            }

            private void setInGameAnswerCollector(InGameAnswerCollector inGameAnswerCollector) {

                this.inGameAnswerCollector = inGameAnswerCollector;

            }


            private ChooseGodButton(String assignedGod, String assignedGodDescription){

                Image image;
                image = giveGodCardImage(assignedGod);
                if (image != null ) {
                    this.setIcon(resizeIcon(new ImageIcon(image)));
                }
                else {
                    this.setText(assignedGod);
                }
                this.assignedGod = assignedGod;
                this.assignedGodDescription = assignedGodDescription;
                addMouseListener(this);

            }


            @Override
            public void mouseClicked(MouseEvent e) {

                if(isButtonActive && inGameAnswerCollector != null){

                    inGameAnswerCollector.notifyCollector(this.assignedGod);
                    this.isButtonActive = false;

                }




            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                this.setToolTipText(assignedGod +"\n" +assignedGodDescription);


            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        }




    }

    private static class ChatPanel extends JPanel {

        private final JTextField inputChat;

        private final JTextArea areaMessages;

        private String namePlayer = "";

        private boolean isNameSetted = false;



        private ChatPanel() {

            this.setLayout(new BorderLayout());

            JPanel subPanel = new JPanel();
            subPanel.setLayout(new BorderLayout());

            inputChat = new JTextField();
            inputChat.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Global.JCHATTEXTSIZE));
            areaMessages = new JTextArea();
            areaMessages.setFont(new Font(Font.SANS_SERIF, Font.BOLD,  Global.JCHATTEXTSIZE));
            SendButton sendButton = new SendButton();

            areaMessages.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(areaMessages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            subPanel.add(scrollPane, BorderLayout.CENTER);
            subPanel.add(inputChat, BorderLayout.SOUTH);

            this.add(subPanel, BorderLayout.CENTER);
            this.add(sendButton, BorderLayout.SOUTH);



        }


        private void refreshChat(String message) {

            this.areaMessages.append("\n" + message);

        }


        private class SendButton extends JButton implements ActionListener{


            private SendButton(){

                super("Send");
                addActionListener(this);
            }


            @Override
            public void actionPerformed(ActionEvent e) {

                if( !isNameSetted && ClientViewAdapter.getClientFsm() != null && ClientViewAdapter.getClientFsm().getPlayerName() != null){

                    namePlayer = ClientViewAdapter.getClientFsm().getPlayerName();
                    isNameSetted = true;

                }

                if(isNameSetted) {

                    String message = inputChat.getText();
                    message = namePlayer +" >>> " + message;
                    ClientViewAdapter.sendChatMessage(message);
                    System.out.println("Sono nel pulsante di send e ho inviato il messaggio " +message);
                    inputChat.setText("");

                }

                else {

                    String message = inputChat.getText();
                    message = "Error no name player" +" >>> " + message;
                    ClientViewAdapter.sendChatMessage(message);
                    inputChat.setText("");
                }


            }
        }


    }

    private static class InGameAnswerCollector implements Runnable{

        private Object answer = null;
        private boolean isSleeping = false;


        @Override
        public void run() {


            try {

                waitCollector();

            } catch (InterruptedException e) {

                LogPrinter.printOnLog(e.toString());
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }




        }

        synchronized void waitCollector() throws InterruptedException {

            isSleeping = true;

            while(isSleeping) {
                wait();
            }

        }

        synchronized void notifyCollector(Object answer){

            if(isSleeping) {

                this.answer = answer;
                isSleeping = false;
                notifyAll();

            }
        }

        synchronized Object giveAnswer() {

            return answer;

        }





    }


    /**
     * this method will collect info from the answercollector of box buttons and translate it into coordinates
     * that will be put i a playermove
     *
     * @param message given from server
     * @return playermove with coordinates in
     */
    @Override
    public PlayerMove askForCoordinates(String message) {

        inGameCardLeftLayout.show(leftCardsPanel, LEFTCARD_GAME_BUTTONS);

        CollectorCoordinatesAnswer collectorCoordinatesAnswer = new CollectorCoordinatesAnswer();

        Thread collector = new Thread(collectorCoordinatesAnswer);

        collector.start();

        //every button needs to be active to let the player choose his move
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).setButtonActive(true, collectorCoordinatesAnswer);
            }
        }


        try {
            collector.join();
        } catch (InterruptedException e) {

            LogPrinter.printOnLog(e.toString());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //after the move, every button needs to be turned off again
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).setButtonActive(false, null);
            }
        }


        return  PlayerMove.buildCoordPlayerMove(collectorCoordinatesAnswer.giveCoordinates().getRow(), collectorCoordinatesAnswer.giveCoordinates().getColumn(), null);


    }

    /**
     * will popup an element that will ask the question in message and allow to answer with yes or no
     * a confirmation playermove will be built accordingly
     *
     * @param message that tells question to be ask
     * @return built from the method
     */
    @Override
    public PlayerMove askForInGameConfirmation(String message) {

        boolean confirmation = askGuiInGameConfirmation(message);

        if(confirmation) return PlayerMove.buildConfirmPlayerMove(ConfirmationEnum.YES, null);

        else return PlayerMove.buildConfirmPlayerMove(ConfirmationEnum.NO, null);


    }

    /**
     * will capture input from chooseGodPanel and generate a string with the name of the god accordingly
     *
     * @param message to tell which use will be done of the choice
     * @return playermove with the string inside
     */
    @Override
    public PlayerMove askForGodName(String message) {

        inGameCardLeftLayout.show(leftCardsPanel, CHOOSE_GOD_PANEL);

        InGameAnswerCollector inGameAnswerCollector = new InGameAnswerCollector();

        Thread collector = new Thread(inGameAnswerCollector);

        collector.start();


        int numberOfButtonsX= 0;

        while ( (numberOfButtonsX * numberOfButtonsX) < godDeck.getGodArrayList().size()) {
            numberOfButtonsX++;
        }

        int numb = 0;

        for(int i = 0; i < numberOfButtonsX; i++) {
            for(int j = 0; j < numberOfButtonsX; j++){

                if(numb < godDeck.getGodArrayList().size())
                {
                    chooseGodPanel.chooseGodButtons[i][j].setButtonActive();
                    chooseGodPanel.chooseGodButtons[i][j].setInGameAnswerCollector(inGameAnswerCollector);
                    numb++;
                }
            }



        }


        try {
            collector.join();
        } catch (InterruptedException e) {
            printInGameMessage("FATAL ERROR IN COLLECTOR");
            Thread.currentThread().interrupt();
        }


        String godChosen = (String) inGameAnswerCollector.giveAnswer();

        return PlayerMove.buildStringPlayerMove(godChosen, null);

    }

    /**
     * will change current representation of the board according to the new one received
     *
     * @param boardPhotography that has been modified by model
     */
    @Override
    public void showBoard(BoardPhotography boardPhotography) {

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).updateButton(boardPhotography.getBoxPhoto(i, j));
            }
        }

    }

    /**
     * prints on the gui the last hint on the game status
     * @param message contained in the last modelMessage
     */
    @Override
    public void printInGameMessage(String message) {

        whatToDoText.setEditable(false);
        whatToDoText.setBorder(BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY));
        whatToDoText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Global.INGAMETEXTSIZE));
        whatToDoText.setText(message);

    }

    /**
     * method used to print error messages on the GUI
     * @param message toString of the modelerror
     */
    @Override
    public void printSecondaryInGameMessage (String message){

        errorText.setEditable(false);
        errorText.setVisible(true);
        errorText.setBorder(BorderFactory.createLineBorder(java.awt.Color.RED));
        errorText.setFont(new Font(Font.SERIF, Font.PLAIN , Global.INGAMETEXTSIZE));
        if (message.equals(" \n")) {
            errorText.setVisible(false);
        }
        errorText.setText(message);

    }

    /**
     * this method is used to print on the Gui the images of the cards of the gods chosen and the
     * color relative to the workers of the player that has the god
     *
     * @param message with info on god and colour of the workers associated
     * @param yours true if the god has been chosen by this GUI's user
     */
    @Override
    public void showChosenGods(ModelMessage message, boolean yours) {

        Image img;
        Image godImage;
        Image resizedImage;

        if (yours) {
            img = godChosenYou.getImage();
            resizedImage = img.getScaledInstance(Global.JCHOSENGODSTEXTPANELWIDTH, Global.JCHOSENGODSTEXTPANELHEIGHT, java.awt.Image.SCALE_SMOOTH);
            your.setIcon(new ImageIcon(resizedImage));
            godImage = giveGodCardImage(message.getMessage());
            yourGod.setIcon(new ImageIcon(godImage));
            rightPanel.add(your);
            rightPanel.add(yourGod);
        }
        else{

                switch (message.getColorOfTheCurrent()) {
                    case GREEN:

                        img = godChosenGreen.getImage();
                        resizedImage = img.getScaledInstance(Global.JCHOSENGODSTEXTPANELWIDTH, Global.JCHOSENGODSTEXTPANELHEIGHT, java.awt.Image.SCALE_SMOOTH);
                        green.setIcon(new ImageIcon(resizedImage));
                        godImage = giveGodCardImage(message.getMessage());
                        greenGod.setIcon(new ImageIcon(godImage));
                        rightPanel.add(green);
                        rightPanel.add(greenGod);

                        break;

                    case RED:
                        img = godChosenRed.getImage();
                        resizedImage = img.getScaledInstance(Global.JCHOSENGODSTEXTPANELWIDTH, Global.JCHOSENGODSTEXTPANELHEIGHT, java.awt.Image.SCALE_SMOOTH);
                        red.setIcon(new ImageIcon(resizedImage));
                        godImage = giveGodCardImage(message.getMessage());
                        redGod.setIcon(new ImageIcon(godImage));
                        rightPanel.add(red);
                        rightPanel.add(redGod);
                        break;

                    case YELLOW:
                        img = godChosenYellow.getImage();
                        resizedImage = img.getScaledInstance(Global.JCHOSENGODSTEXTPANELWIDTH, Global.JCHOSENGODSTEXTPANELHEIGHT, java.awt.Image.SCALE_SMOOTH);
                        yellow.setIcon(new ImageIcon(resizedImage));
                        godImage = giveGodCardImage(message.getMessage());
                        yellowGod.setIcon(new ImageIcon(godImage));
                        rightPanel.add(yellow);
                        rightPanel.add(yellowGod);
                        break;

                    default:
                        break;

                }
            }
    }

    void printMessageBlocking(String message) {
        JOptionPane.showMessageDialog(this, message);
    }



    //image icons that hell who chose who
    private final ImageIcon godChosenYou = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Images/YOU.png")));
    private final ImageIcon godChosenYellow = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Images/YELLOW.png")));
    private final ImageIcon godChosenGreen = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Images/GREEN.png")));
    private final ImageIcon godChosenRed = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Images/RED.png")));

    //image icons for divinity cards
    private final ImageIcon apolloIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Apollo.png")));
    private final ImageIcon aresIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Ares.png")));
    private final ImageIcon artemisIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Artemis.png")));
    private final ImageIcon athenaIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Athena.png")));
    private final ImageIcon atlasIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Atlas.png")));
    private final ImageIcon chronusIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Chronus.png")));
    private final ImageIcon demeterIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Demeter.png")));
    private final ImageIcon hephaestusIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Hephaestus.png")));
    private final ImageIcon hestiaIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Hestia.png")));
    private final ImageIcon minotaurIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Minotaur.png")));
    private final ImageIcon panIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Pan.png")));
    private final ImageIcon prometheusIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Prometheus.png")));
    private final ImageIcon tritonIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Triton.png")));
    private final ImageIcon zeusIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("GodCards/Zeus.png")));
    private final ImageIcon errorIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("Images/blackscreen.jpg")));


    private class BoxButton extends JButton implements ActionListener {

        private final int row;
        private final int col;
        private BoxPhotography box;

        private CollectorCoordinatesAnswer collectorCoordinatesAnswer;

        private boolean isButtonActive = false;


        BoxButton(BoxPhotography box){

            this.row = box.getRow();
            this.col = box.getColumn();
            this.box = box;
            setIcon(resizeIcon(clientBoardPhotography.level0NoWorker));
            this.addActionListener(this);

        }


        void setButtonActive(boolean buttonActive, CollectorCoordinatesAnswer collectorCoordinatesAnswer) {

            this.isButtonActive = buttonActive;
            this.collectorCoordinatesAnswer = collectorCoordinatesAnswer;

        }

        void updateButton(BoxPhotography newBox) {

            if( !this.box.equals(newBox) ){
                this.box = newBox;
                setRightImage(this.box);
            }
        }

        private void setRightImage(BoxPhotography box){

            int level = box.getLevel();
            boolean isOccupied = box.isOccupied();
            Color workerColor = box.getColor();
            boolean domed = box.isDomed();

            if( !isOccupied ){

                switch (level){

                    case 0:
                        setIcon(resizeIcon(clientBoardPhotography.level0NoWorker));
                        break;

                    case 1:
                        setIcon(resizeIcon(clientBoardPhotography.level1NoWorker));
                        break;

                    case 2:
                        setIcon(resizeIcon(clientBoardPhotography.level2NoWorker));
                        break;

                    case 3:
                        setIcon(resizeIcon(clientBoardPhotography.level3NoWorker));
                        break;

                    case 4:
                        setIcon(resizeIcon(clientBoardPhotography.dome));
                        break;

                    default:
                        //ci dovrebbe essere un'ciona di errore
                        break;
                }

                if(domed) { setIcon(resizeIcon(clientBoardPhotography.dome));}

            }

            else{

                switch (workerColor){

                    case RED:{

                        switch (level){

                            case 0:
                                setIcon(resizeIcon(clientBoardPhotography.level0RedWorker));
                                break;

                            case 1:
                                setIcon(resizeIcon(clientBoardPhotography.level1RedWorker));
                                break;

                            case 2:
                                setIcon(resizeIcon(clientBoardPhotography.level2RedWorker));
                                break;

                            case 3:
                                setIcon(resizeIcon(clientBoardPhotography.level3RedWorker));
                                break;

                            default:
                                break;
                        }

                        break;}


                    case YELLOW:{

                        switch (level){

                            case 0:
                                setIcon(resizeIcon(clientBoardPhotography.level0YellowWorker));
                                break;

                            case 1:
                                setIcon(resizeIcon(clientBoardPhotography.level1YellowWorker));
                                break;

                            case 2:
                                setIcon(resizeIcon(clientBoardPhotography.level2YellowWorker));
                                break;

                            case 3:
                                setIcon(resizeIcon(clientBoardPhotography.level3YellowWorker));
                                break;

                            default:
                                //ci dovrebbe essere un'ciona di errore
                                break;
                        }

                        break;}


                    case GREEN:{

                        switch (level){

                            case 0:
                                setIcon(resizeIcon(clientBoardPhotography.level0GreenWorker));
                                break;

                            case 1:
                                setIcon(resizeIcon(clientBoardPhotography.level1GreenWorker));
                                break;

                            case 2:
                                setIcon(resizeIcon(clientBoardPhotography.level2GreenWorker));
                                break;

                            case 3:
                                setIcon(resizeIcon(clientBoardPhotography.level3GreenWorker));
                                break;

                            default:
                                //ci dovrebbe essere un'ciona di errore
                                break;
                        }

                        break;

                    }

                    default:
                        break;
                }



            }








        }

        private Icon resizeIcon(ImageIcon icon) {
            Image img = icon.getImage();
            Image resizedImage = img.getScaledInstance(buttonWidth, buttonHeight,  java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }



        @Override
        public void actionPerformed(ActionEvent e) {

            if(isButtonActive){

                collectorCoordinatesAnswer.notifyCollector(row, col);

            }


        }

    }














}


