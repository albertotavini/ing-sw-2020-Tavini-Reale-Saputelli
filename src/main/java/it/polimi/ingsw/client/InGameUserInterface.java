package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InGameUserInterface {


    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);
    void showBoard(BoardPhotography boardPhotography);
    void printInGameMessage(String message);

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
    public void printInGameMessage(String message) {

        System.out.println(message);

    }

}

class InGameGui extends JFrame implements InGameUserInterface {


    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();

    //parte con set visible false

    JPanel panel = new JPanel();
    JPanel panel2 = new JPanel();
    private int buttonWidth = 150;
    private int buttonHeight = 150;
    BoxButton[][] boxButtons = new BoxButton[5][5];
    JTextArea eti = new JTextArea("");


    public InGameGui() {
        super("Santorini : The Game");
        this.setSize(1200,700);
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1,2));
        panel.setLayout(new GridLayout(5,5));

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                boxButtons[i][j] = new BoxButton(clientBoardPhotography.getBox(i, j));
                (boxButtons[i][j]).setPreferredSize(new Dimension(buttonWidth,buttonHeight));
                panel.add(boxButtons[i][j]);
            }}

        panel2.add(eti);
        add(panel);
        add(panel2);
        this.setVisible(false);

    }



    private class CollectorAnswer implements Runnable {

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

            if(isSleeping == true) {

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
        Object[] options = {"Yes", "No"};
        int answer = JOptionPane.showOptionDialog(this, "You have to choose!", message, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return answer == JOptionPane.YES_OPTION;
    }

    private String askGuiGodName(String message) {

        String answer = JOptionPane.showInputDialog(this, message);
        return answer;
    }

    public void setInGameGuiVisible(boolean visible){
        this.setVisible(visible);
    }



    @Override
    public PlayerMove askForCoordinates(String message) {


        CollectorAnswer collectorAnswer = new CollectorAnswer();

        Thread collector = new Thread(collectorAnswer);

        collector.start();


        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).setButtonActive(true, collectorAnswer);
            }
        }


        try {
            collector.join();
        } catch (InterruptedException e) {

            LogPrinter.printOnLog(e.toString());
            Thread.currentThread().interrupt();
        }

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                (boxButtons[i][j]).setButtonActive(false, null);
            }
        }


        return new PlayerMove(collectorAnswer.giveCoordinates().getRow(), collectorAnswer.giveCoordinates().getColumn(), null);


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

        eti.append(message);
    }


    private class BoxButton extends JButton implements ActionListener {

        private final int row;
        private final int col;
        private BoxPhotography box;

        private CollectorAnswer collectorAnswer;

        private boolean isButtonActive = false;


        public BoxButton(BoxPhotography box){

            this.row = box.getRow();
            this.col = box.getColumn();
            this.box = box;
            setIcon(resizeIcon(clientBoardPhotography.level0NoWorker));
            this.addActionListener(this);

        }


        public void setButtonActive(boolean buttonActive, CollectorAnswer collectorAnswer) {

            this.isButtonActive = buttonActive;
            this.collectorAnswer = collectorAnswer;

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

                collectorAnswer.notifyCollector(row, col);

            }


        }

    }













}


