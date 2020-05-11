package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.Color;
import it.polimi.ingsw.server.utils.ColorAnsi;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InGameUserInterface {


    ClientBoardPhotography clientBoardPhotography = new ClientBoardPhotography();

    PlayerMove askForCoordinates(String message);
    PlayerMove askForInGameConfirmation(String message);
    PlayerMove askForGodName(String message);

    void showBoard(BoardPhotography boardPhotography);

}


class InGameCli implements InGameUserInterface {


    @Override
    public PlayerMove askForCoordinates(String message) {

        int row = 7;
        int column = 7;
        PlayerMove coordinates = null;
        String dataInput = null;
        boolean correctInput = false;

        do {

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

        }while(!correctInput);

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



        do{

            System.out.println(ColorAnsi.RED +"y/n:" +ColorAnsi.RESET);
            conferma = ClientMain.scannerIn.nextLine();

            matcherConfirmation = confirmationPattern.matcher(conferma);
            correctInput = matcherConfirmation.find();


        }while(!correctInput);




        PlayerMove playerMoveConfirmation;


        if(conferma.equals("y"))
        {
            playerMoveConfirmation = new PlayerMove(ConfirmationEnum.Yes, null);

        }

        else{

            playerMoveConfirmation = new PlayerMove(ConfirmationEnum.No, null);

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

    }

class InGameGui extends JFrame implements InGameUserInterface {


    //parte con set visible false

    JPanel panel = new JPanel();
    JPanel panel2 = new JPanel();
    BoxButton[][] boxButtons = new BoxButton[5][5];
    JLabel eti = new JLabel("ECCO LA PLANCIA DI GIOCO");


    public InGameGui(){
        super("Santorini : The Game");
        this.setSize(1200,700);
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1,2));
        panel.setLayout(new GridLayout(5,5));

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                boxButtons[i][j] = new BoxButton(clientBoardPhotography.getBox(i, j));
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
                e.printStackTrace();
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
                notify();

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


    /*private class InGameConfirmation extends JDialog {

        boolean confirmation;

        public InGameConfirmation(JFrame parent, String title, String message) {
            super(parent, title);
            Point p = new Point(400, 400);
            setLocation(p.x, p.y);

            JPanel messagePane = new JPanel();
            messagePane.add(new JLabel(message));
            getContentPane().add(messagePane);
            JPanel buttonPane = new JPanel();
            ButtonYes buttonYes = new ButtonYes("Yes");
            ButtonNo buttonNo = new ButtonNo("No");
            buttonPane.add(buttonYes);
            buttonPane.add(buttonNo);
            getContentPane().add(buttonPane, BorderLayout.PAGE_END);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);

        }




    }*/



    private boolean askGuiInGameConfirmation(String message) {
        Object[] options = {"Yes", "No"};
        int answer = JOptionPane.showOptionDialog(null, "You have to choose!", message, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return answer == JOptionPane.YES_OPTION;
    }

    private String askGuiGodName(String message) {
        String answer = JOptionPane.showInputDialog(message);
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
            e.printStackTrace();
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

        if(confirmation) return new PlayerMove(ConfirmationEnum.Yes, null);

        else return new PlayerMove(ConfirmationEnum.No, null);


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

            if( !isOccupied ){

                switch (level){

                    case 0:
                        setIcon(clientBoardPhotography.level0NoWorker);
                        break;

                    case 1:
                        setIcon(clientBoardPhotography.level1NoWorker);
                        break;

                    case 2:
                        setIcon(clientBoardPhotography.level2NoWorker);
                        break;

                    case 3:
                        setIcon(clientBoardPhotography.level3NoWorker);
                        break;

                    case 4:
                        setIcon(clientBoardPhotography.dome);
                        break;
                }

            }

            else{

                switch (workerColor){

                    case RED:{

                        switch (level){

                            case 0:
                                setIcon(clientBoardPhotography.level0RedWorker);
                                break;

                            case 1:
                                setIcon(clientBoardPhotography.level1RedWorker);
                                break;

                            case 2:
                                setIcon(clientBoardPhotography.level2RedWorker);
                                break;

                            case 3:
                                setIcon(clientBoardPhotography.level3RedWorker);
                                break;
                        }

                        break;}


                    case YELLOW:{

                        switch (level){

                            case 0:
                                setIcon(clientBoardPhotography.level0YellowWorker);
                                break;

                            case 1:
                                setIcon(clientBoardPhotography.level1YellowWorker);
                                break;

                            case 2:
                                setIcon(clientBoardPhotography.level2YellowWorker);
                                break;

                            case 3:
                                setIcon(clientBoardPhotography.level3YellowWorker);
                                break;
                        }

                        break;}


                    case GREEN:{

                        switch (level){

                            case 0:
                                setIcon(clientBoardPhotography.level0GreenWorker);
                                break;

                            case 1:
                                setIcon(clientBoardPhotography.level1GreenWorker);
                                break;

                            case 2:
                                setIcon(clientBoardPhotography.level2GreenWorker);
                                break;

                            case 3:
                                setIcon(clientBoardPhotography.level3GreenWorker);
                                break;
                        }

                        break;}

                }



            }








        }



        @Override
        public void actionPerformed(ActionEvent e) {

            if(isButtonActive){

                collectorAnswer.notifyCollector(row, col);

            }


        }

    }













}


