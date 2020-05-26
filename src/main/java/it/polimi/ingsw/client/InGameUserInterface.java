package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.BoxPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InGameUserInterface {


    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);
    void showBoard(BoardPhotography boardPhotography);
    void printInGameMessage(String message);
    void printSecondaryInGameMessage(String message);
    void showChosenGods(ModelMessage message, boolean yours);

}


class InGameCli implements InGameUserInterface {

    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();

    @Override
    public PlayerMove askForCoordinates(String message) {

        int row = -1;
        int column = -1;
        PlayerMove coordinates = null;
        String dataInput = null;
        boolean correctInput = false;

            System.out.println(ColorAnsi.RED +"\nInsert coordinates (x,y)" +ColorAnsi.RESET);
            dataInput = ClientMain.scannerIn.nextLine();


            String regexData = "^([0-4]),([0-4])$";
            Pattern playerMovePattern = Pattern.compile(regexData);
            Matcher matcherPlayerMove = playerMovePattern.matcher(dataInput);
            correctInput = matcherPlayerMove.find();


            if (correctInput) {

                row = Integer.parseInt(matcherPlayerMove.group(1));
                column = Integer.parseInt(matcherPlayerMove.group(2));

            }

        coordinates = new PlayerMove(row, column, null);
        return coordinates;

    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {


        String conferma = null;
        boolean correctInput = false;
        String regexData = "^(y|n)$";
        Pattern confirmationPattern = Pattern.compile(regexData);
        Matcher matcherConfirmation;

        do {


            System.out.println(ColorAnsi.RED + "y/n:" + ColorAnsi.RESET);
            conferma = ClientMain.scannerIn.nextLine();

            matcherConfirmation = confirmationPattern.matcher(conferma);
            correctInput = matcherConfirmation.find();

        }while(!correctInput);

            PlayerMove playerMoveConfirmation = new PlayerMove(ConfirmationEnum.NOTDEF, null);

            if (conferma.equals("y")) {
                playerMoveConfirmation = new PlayerMove(ConfirmationEnum.YES, null);

            } else if (conferma.equals("n")) {

                playerMoveConfirmation = new PlayerMove(ConfirmationEnum.NO, null);

            }


            return playerMoveConfirmation;

    }

    @Override
    public PlayerMove askForGodName(String message) {

        System.out.println(ColorAnsi.RED +"Insert god name:" +ColorAnsi.RESET);

        String godName = ClientMain.scannerIn.nextLine();

        PlayerMove playerMoveGodName = new PlayerMove(godName, null);

        return playerMoveGodName;

    }

    @Override
    public void showBoard(BoardPhotography boardPhotography) {

            BoxPhotography[][] matrix = boardPhotography.getMatrixPhotograph();

            System.out.println("\n" +"       0       1       2       3       4 ");

            int rowIndex = 0;
            for (BoxPhotography[] line : matrix) {
                System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
                rowIndex++;
            }

        }

    @Override
    public void printSecondaryInGameMessage(String message) {

        System.out.println(message);

    }

    @Override
    public void printInGameMessage(String message) {

        System.out.println(message);
    }

    @Override
    public void showChosenGods(ModelMessage message, boolean yours){
        if (yours){
            System.out.println("You chose "+message.getMessage());
        } else {
            System.out.println("The Player with " + message.getColorOfTheCurrent() + " has chosen " + message.getMessage());
        }
    }

}

class InGameGui extends JFrame implements InGameUserInterface {


    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();

    //parte con set visible false

    CardLayout inGameCardLeftLayout = new CardLayout();
    JPanel leftCardsPanel = new JPanel(inGameCardLeftLayout);

    private final JPanel leftCardPanelGameButtons = new JPanel();
    private final String LEFTCARD_GAME_BUTTONS = "BUTTONS GAME BUTTONS";

    private final InGameQuestionBooleanPanel inGameQuestionBooleanPanel = new InGameQuestionBooleanPanel();
    private final String IN_GAME_QUESTION_BOOLEN_PANEL = "QUESTION BOOLEAN PANEL";

    JPanel rightPanel = new JPanel();

    private int buttonWidth = 150;
    private int buttonHeight = 150;
    BoxButton[][] boxButtons = new BoxButton[5][5];

    JTextArea eti = new JTextArea();
    JTextArea eti2 = new JTextArea();


    public InGameGui() {
        //construction of the JFrame object InGameGui
        super("Santorini : The Game");

        this.setSize(1200,700);
        //user can't resize the JFrame object
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //creating a Layout with the left part as the gameboard and the right part as a text area
        this.setLayout(new GridLayout(1,2));

        //setting panel: on this panel will be the gameboard
        leftCardPanelGameButtons.setLayout(new GridLayout(5,5));
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                boxButtons[i][j] = new BoxButton(clientBoardPhotography.getBox(i, j));
                (boxButtons[i][j]).setPreferredSize(new Dimension(buttonWidth,buttonHeight));
                leftCardPanelGameButtons.add(boxButtons[i][j]);
            }}


        //setting the text area on right panel
        eti.setLineWrap(true);
        eti.setWrapStyleWord(true);
        eti.setSize(500,500);
         rightPanel.add(eti);

        //setting the second textpanel
        eti2.setLineWrap(true);
        eti2.setWrapStyleWord(true);
        eti2.setSize(500,500);
        rightPanel.add(eti2);


        //adding panels on my JFrame InGameGui
        add(leftCardsPanel);
        leftCardsPanel.add(leftCardPanelGameButtons, LEFTCARD_GAME_BUTTONS);
        leftCardsPanel.add(inGameQuestionBooleanPanel, IN_GAME_QUESTION_BOOLEN_PANEL);
        add(rightPanel);


        this.setVisible(false);

    }



    private class CollectorCoordinatesAnswer implements Runnable {

        private Coordinates coordinates = new Coordinates(-1, -1);
        private boolean isSleeping = false;


        @Override
        public void run() {


            try {

                waitCollector();

            } catch (InterruptedException e) {

                LogPrinter.printOnLog(e.toString());
                Thread.currentThread().interrupt();
            }




        }

        public synchronized void waitCollector() throws InterruptedException {

            isSleeping = true;
            wait();

        }

        public synchronized void notifyCollector(int row, int col){

            if(isSleeping) {

                this.coordinates.setRow(row);
                this.coordinates.setColumn(col);
                isSleeping = false;
                notifyAll();

            }
        }

        public synchronized Coordinates giveCoordinates(){

            return coordinates;

        }

    }

    private class Coordinates {

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

    private String askGuiGodName(String message) {

        return JOptionPane.showInputDialog(this, message);

    }

    public void setInGameGuiVisible(boolean visible){
        this.setVisible(visible);
    }



    private class InGameQuestionBooleanPanel extends JPanel{

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

    private static class InGameAnswerCollector implements Runnable{

        private Object answer = null;
        private boolean isSleeping = false;


        @Override
        public void run() {


            try {

                waitCollector();

            } catch (InterruptedException e) {

                LogPrinter.printOnLog(e.toString());
                Thread.currentThread().interrupt();
            }




        }

        public synchronized void waitCollector() throws InterruptedException {

            isSleeping = true;

            while(isSleeping) {
                wait();
            }

        }

        public synchronized void notifyCollector(Object answer){

            if(isSleeping) {

                this.answer = answer;
                isSleeping = false;
                notifyAll();

            }
        }

        public synchronized Object giveAnswer() {

            return answer;

        }





    }




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
            Thread.currentThread().interrupt();
        }

        //after the move, every button needs to be turned off again
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).setButtonActive(false, null);
            }
        }


        return new PlayerMove(collectorCoordinatesAnswer.giveCoordinates().getRow(), collectorCoordinatesAnswer.giveCoordinates().getColumn(), null);


    }

    @Override
    public PlayerMove askForInGameConfirmation(String message) {

        boolean confirmation = askGuiInGameConfirmation(message);

        if(confirmation) return new PlayerMove(ConfirmationEnum.YES, null);

        else return new PlayerMove(ConfirmationEnum.NO, null);


    }

    @Override
    public PlayerMove askForGodName(String message) {

        String godName = askGuiGodName(message);

        return new PlayerMove(godName, null);

    }

    @Override
    public void showBoard(BoardPhotography boardPhotography) {

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).updateButton(boardPhotography.getBoxPhoto(i, j));
            }
        }

    }

    @Override
    public void printInGameMessage(String message) {

        eti.setEditable(false);
        eti.setBorder(BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY));
        eti.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        eti.setText(message);

    }

    @Override
    public void printSecondaryInGameMessage (String message){

        eti2.setEditable(false);
        eti2.setBorder(BorderFactory.createLineBorder(java.awt.Color.RED));
        eti2.setFont(new Font(Font.SERIF, Font.PLAIN , 25));
        eti2.setText(message);

    }


    @Override
    public void showChosenGods(ModelMessage message, boolean yours) {

        Image img;
        Image godImage;
        Image resizedImage;

        if (yours) {
            JLabel your = new JLabel("");
            JLabel yourGod = new JLabel("");
            img = godChosenYou.getImage();
            resizedImage = img.getScaledInstance(320, 80, java.awt.Image.SCALE_SMOOTH);
            your.setIcon(new ImageIcon(resizedImage));
            godImage = giveGodCardImage(message.getMessage());
            yourGod.setIcon(new ImageIcon(godImage));
            rightPanel.add(your);
            rightPanel.add(yourGod);
        }
        else{

                switch (message.getColorOfTheCurrent()) {
                    case GREEN:
                        JLabel green = new JLabel("");
                        JLabel greenGod = new JLabel("");
                        img = godChosenGreen.getImage();
                        resizedImage = img.getScaledInstance(320, 80, java.awt.Image.SCALE_SMOOTH);
                        green.setIcon(new ImageIcon(resizedImage));
                        godImage = giveGodCardImage(message.getMessage());
                        greenGod.setIcon(new ImageIcon(godImage));
                        rightPanel.add(green);
                        rightPanel.add(greenGod);

                        break;

                    case RED:
                        JLabel red = new JLabel("");
                        JLabel redGod = new JLabel("");
                        img = godChosenRed.getImage();
                        resizedImage = img.getScaledInstance(320, 80, java.awt.Image.SCALE_SMOOTH);
                        red.setIcon(new ImageIcon(resizedImage));
                        godImage = giveGodCardImage(message.getMessage());
                        redGod.setIcon(new ImageIcon(godImage));
                        rightPanel.add(red);
                        rightPanel.add(redGod);
                        break;

                    case YELLOW:
                        JLabel yellow = new JLabel("");
                        JLabel yellowGod = new JLabel("");
                        img = godChosenYellow.getImage();
                        resizedImage = img.getScaledInstance(320, 80, java.awt.Image.SCALE_SMOOTH);
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

    private Image giveGodCardImage (String godName) {
        Image image;
        Image resizedImage;

        switch (godName.toUpperCase()){
            case "APOLLO":
                image =apolloIcon.getImage();
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
        resizedImage = image.getScaledInstance(84, 140, java.awt.Image.SCALE_SMOOTH);
        return resizedImage;
    }


    private final ImageIcon godChosenYou = new ImageIcon(this.getClass().getClassLoader().getResource("Images/YOU.jpg"));
    private final ImageIcon godChosenYellow = new ImageIcon(this.getClass().getClassLoader().getResource("Images/YELLOW.jpg"));
    private final ImageIcon godChosenGreen = new ImageIcon(this.getClass().getClassLoader().getResource("Images/GREEN.jpg"));
    private final ImageIcon godChosenRed = new ImageIcon(this.getClass().getClassLoader().getResource("Images/RED.jpg"));

    private final ImageIcon apolloIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Apollo.png"));
    private final ImageIcon aresIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Ares.png"));
    private final ImageIcon artemisIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Artemis.png"));
    private final ImageIcon athenaIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Athena.png"));
    private final ImageIcon atlasIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Atlas.png"));
    private final ImageIcon chronusIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Chronus.png"));
    private final ImageIcon demeterIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Demeter.png"));
    private final ImageIcon hephaestusIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Hephaestus.png"));
    private final ImageIcon hestiaIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Hestia.png"));
    private final ImageIcon minotaurIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Minotaur.png"));
    private final ImageIcon panIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Pan.png"));
    private final ImageIcon prometheusIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Prometheus.png"));
    private final ImageIcon tritonIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Triton.png"));
    private final ImageIcon zeusIcon = new ImageIcon(this.getClass().getClassLoader().getResource("GodCards/Zeus.png"));
    private final ImageIcon errorIcon = new ImageIcon(this.getClass().getClassLoader().getResource("Images/blackscreen.jpg"));


    private class BoxButton extends JButton implements ActionListener {

        private final int row;
        private final int col;
        private BoxPhotography box;

        private CollectorCoordinatesAnswer collectorCoordinatesAnswer;

        private boolean isButtonActive = false;


        public BoxButton(BoxPhotography box){

            this.row = box.getRow();
            this.col = box.getColumn();
            this.box = box;
            setIcon(resizeIcon(clientBoardPhotography.level0NoWorker));
            this.addActionListener(this);

        }


        public void setButtonActive(boolean buttonActive, CollectorCoordinatesAnswer collectorCoordinatesAnswer) {

            this.isButtonActive = buttonActive;
            this.collectorCoordinatesAnswer = collectorCoordinatesAnswer;

        }

        public void updateButton(BoxPhotography newBox) {

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
                                //ci dovrebbe essere un'ciona di errore
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


    private static class ImagePanel extends JPanel{


        private BufferedImage image;

        public ImagePanel(ImageIcon icon) {

            try {
                image = (BufferedImage) icon.getImage();
            } catch (Exception ex) {
                LogPrinter.printOnLog("Swing fucked up while setting Icons in ImagePanel "+ex.toString());
                Thread.currentThread().interrupt();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage((Image) image, 0, 0, this);
        }
    }











}


