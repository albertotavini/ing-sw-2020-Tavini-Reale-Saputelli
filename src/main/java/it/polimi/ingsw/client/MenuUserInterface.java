package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public interface MenuUserInterface {

    String askForName();
    boolean askBooleanQuestion(String message);
    Date askForDate();
    void printMenuMessage(String message);
    MenuMessage askForInfoToCreateLobby(String creator);
    MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer);


}


class MenuCli implements MenuUserInterface {

    @Override
    public String askForName() {
        System.out.println(ColorAnsi.RED +"Inserisci nome:" +ColorAnsi.RESET);
        return ClientMain.scannerIn.nextLine();
    }

    @Override
    public boolean askBooleanQuestion(String message) {
        String conferma = null;

        do{

                System.out.println(ColorAnsi.RED +message +ColorAnsi.RESET);
                conferma = ClientMain.scannerIn.nextLine();
                conferma = conferma.toUpperCase();

        }while(!(conferma.equals("Y") || conferma.equals("N")));

        return conferma.equals("Y");
    }

    @Override
    public Date askForDate() {

        Date data = null;

        do{

            System.out.println(ColorAnsi.RED +"Inserisci giorno di nascita in formato gg/mm/aaaa:" +ColorAnsi.RESET);
            String dataInput = ClientMain.scannerIn.nextLine();

            //forse troppo naif?

            String regexData = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/([0-9]{4}$)";
            Pattern datePattern = Pattern.compile(regexData);
            Matcher matcherDate = datePattern.matcher(dataInput);

            if(matcherDate.find()) {


                int giorno = Integer.parseInt(matcherDate.group(1));
                int mese = Integer.parseInt(matcherDate.group(2));
                int anno = Integer.parseInt(matcherDate.group(3));


                try {

                    data = new Date(giorno, mese, anno);

                } catch (DataFormatException e) {
                    //da vedere
                }
            }

        }while(data == null);

        return data;

    }

    @Override
    public void printMenuMessage(String message) {
        System.out.println(message);
    }

    @Override
    public MenuMessage askForInfoToCreateLobby(String nameCreator) {

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;

        String nomeLobby;

        String passwordLobby;
        String ripetizionePassword;

        String isPublic;


        //Color set
        System.out.println(ColorAnsi.RED);

        System.out.println("Inserisci nome lobby:");
        nomeLobby = ClientMain.scannerIn.nextLine();

        do {

            System.out.println("Inserisci capienza lobby:");
            capienzaLobby = ClientMain.scannerIn.nextLine();
            String regexData = "^([2|3])$";

            capacityLobbyPattern = Pattern.compile(regexData);
            matcherCapacity = capacityLobbyPattern.matcher(capienzaLobby);
            correctInput = matcherCapacity.find();

            if (correctInput) {

                capacity = Integer.parseInt(matcherCapacity.group(1));

            }

        }while(!correctInput);


        do{

            System.out.printf("%s", "Preferisci una lobby pubblica o privata? pu/pr\n");
            isPublic = ClientMain.scannerIn.nextLine();
            isPublic = isPublic.toUpperCase();

        }while(!(isPublic.equals("PU") || isPublic.equals("PR")));

        MenuMessage createLobbyInfo;

        //creo una lobby privata
        if(isPublic.equals("PR")) {

            do {

                System.out.println("Inserisci password lobby:");
                passwordLobby = ClientMain.scannerIn.nextLine();
                System.out.println("Inserisci di nuovo password lobby:");
                ripetizionePassword = ClientMain.scannerIn.nextLine();

            } while (!(passwordLobby.equals(ripetizionePassword)));

            //costruttore di create per la lobby private
            createLobbyInfo = MenuMessage.newMenuMessageCreatePrivate(nomeLobby, capacity, passwordLobby, nameCreator);
        }


        //creo una lobby pubblica
        else {

            //costruttore di create per la lobby pubblica
            createLobbyInfo = MenuMessage.newMenuMessageCreatePublic(nomeLobby, capacity, nameCreator);

        }

        //Color reset
        System.out.printf("%s",ColorAnsi.RESET);


        return createLobbyInfo;
    }

    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        String nomeLobby;
        String passwordLobby;

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;
        System.out.printf("%s", ColorAnsi.RED);
        MenuMessage lobbyInfoToParticipate = null;
        boolean wantsLobbyCasual = false;


        if(isPublic) {

            wantsLobbyCasual = askBooleanQuestion("Vuoi partecipare ad una lobby casual? y/n");

            if (wantsLobbyCasual) {

                do {

                    System.out.println(ColorAnsi.RED + "Inserisci capienza lobby:" + ColorAnsi.RESET);
                    capienzaLobby = ClientMain.scannerIn.nextLine();
                    String regexData = "^([2|3])$";

                    capacityLobbyPattern = Pattern.compile(regexData);
                    matcherCapacity = capacityLobbyPattern.matcher(capienzaLobby);
                    correctInput = matcherCapacity.find();

                    if (correctInput) {

                        capacity = Integer.parseInt(matcherCapacity.group(1));

                    }

                } while (!correctInput);


                lobbyInfoToParticipate = MenuMessage.newMenuMessageCasual(namePlayer, capacity);

            }
        }



      if(!wantsLobbyCasual) {

          System.out.println("Inserisci nome lobby");
          nomeLobby = ClientMain.scannerIn.nextLine();

          if (isPublic) {

              //creo un messaggio utilizzando il costruttore per messaggi di participate pubblica
              lobbyInfoToParticipate = MenuMessage.newMenuMessagePartPublic(nomeLobby, namePlayer);

          }

          if (!isPublic) {

              System.out.println("Inserisci password lobby:");
              passwordLobby = ClientMain.scannerIn.nextLine();
              //creo un messaggio utilizzando il costruttore per messaggi di participate privata
              lobbyInfoToParticipate = MenuMessage.newMenuMessagePartPrivate(nomeLobby, passwordLobby, namePlayer);
          }

      }



        System.out.printf("%s", ColorAnsi.RESET);


        return lobbyInfoToParticipate;
    }


}


class MenuGui extends JFrame implements MenuUserInterface {

    private final static CardLayout cardLayout = new CardLayout();
    private final static JPanel cardsPanel = new JPanel(cardLayout);

    private final WelcomePanel welcomePanel;
    private final String WELCOMEPANEL = "WELCOME CARD";

    private final InsertStringPanel insertNamePanel;
    private final String INSERTNAMEPANEL = "INSERT NAME CARD";

    private final InsertStringPanel insertBirthdayPanel;
    private final String INSERTBIRTHPANEL = "INSERT BIRTH CARD";

    private final CreateLobbyPanel createLobbyPanel;
    private final String CREATELOBBYPANEL = "CREATE LOBBY CARD";

    private final ParticipateLobbyPublicPanel participateLobbyPublicPanel;
    private final String PARTICIPATELOBBYPUBLICPANEL = "PARTICIPATE LOBBY PUBLIC CARD";

    private final ParticipateLobbyPrivatePanel participateLobbyPrivatePanel;
    private final String PARTICIPATELOBBYPRIVATEPANEL = "PARTICIPATE LOBBY PRIVATE CARD";





    public MenuGui() {

        super("Santorini");
        this.setSize(1000,800);
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(cardsPanel);

        welcomePanel = new WelcomePanel();
        cardsPanel.add(welcomePanel, WELCOMEPANEL);

        insertNamePanel = new InsertStringPanel("Set Name State", "Insert username:     ");
        cardsPanel.add(insertNamePanel, INSERTNAMEPANEL);

        insertBirthdayPanel = new InsertStringPanel("Set Birth State", "Insert birth:    ");
        cardsPanel.add(insertBirthdayPanel, INSERTBIRTHPANEL);

        createLobbyPanel = new CreateLobbyPanel();
        cardsPanel.add(createLobbyPanel, CREATELOBBYPANEL);

        participateLobbyPublicPanel = new ParticipateLobbyPublicPanel();
        cardsPanel.add(participateLobbyPublicPanel, PARTICIPATELOBBYPUBLICPANEL);

        participateLobbyPrivatePanel = new ParticipateLobbyPrivatePanel();
        cardsPanel.add(participateLobbyPrivatePanel, PARTICIPATELOBBYPRIVATEPANEL);

        this.setVisible(true);


    }

    private static class GenericImagePanel extends JPanel{


        private BufferedImage image;

        public GenericImagePanel() {

            try {
                image = ImageIO.read(new File("Resources/Santorini2.jpg"));
            } catch (IOException ex) {
                Thread.currentThread().interrupt();
            }

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image resizedImage = image.getScaledInstance(1200, 800,  Image.SCALE_SMOOTH);
            g.drawImage(resizedImage, 0, 0, this);
        }




    }

    private static class WelcomePanel extends JPanel {

        private BufferedImage image;

        private final JLabel title = new JLabel("SANTORINI");
        private final StartButton start = new StartButton("Start Game");

        public WelcomePanel() {

            this.setLayout(new BorderLayout());

            try {
                image = ImageIO.read(new File("src/main/resources/MenuImages/Santorini2.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }


            title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 125));
            this.add(title, BorderLayout.NORTH);

            start.setBackground(Color.RED);
            start.setFont(new Font(Font.DIALOG, Font.BOLD, 80));
            this.add(start, BorderLayout.SOUTH);

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image resizedImage = image.getScaledInstance(1200, 800,  Image.SCALE_SMOOTH);
            g.drawImage(resizedImage, 0, 0, this);
        }

        private class StartButton extends JButton implements ActionListener {


            private StartButton(String message) {
                super(message);
                this.addActionListener(this);


            }


            @Override
            public void actionPerformed(ActionEvent e) {

                cardLayout.next(cardsPanel);

            }
        }


    }

    private static class InsertStringPanel extends JPanel {

        private final SendNameButton sendNameButton = new SendNameButton();
        private final JPanel inputPanel;
        private final JTextField input;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        public InsertStringPanel(String title, String textLabel) {

            titleLabel.setText(title);

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridBagLayout());

            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

            JLabel insertUsernameLabel = new JLabel(textLabel);
            input = new JTextField();
            input.setColumns(30);
            input.setFont(inputFont);



            inputPanel.add(insertUsernameLabel);
            inputPanel.add(input);
            inputPanel.add(sendNameButton);
            this.add(inputPanel, BorderLayout.CENTER);

            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));
            this.add(titleLabel, BorderLayout.NORTH);


        }

        private void setAnswerCollector(AnswerCollector answerCollector) {

            this.answerCollector = answerCollector;

        }

        private void setIsButtonActiveTrue(){this.isButtonActive = true;}


        private class SendNameButton extends JButton implements ActionListener {


            private SendNameButton() {

                super("Send");
                this.addActionListener(this);


            }



            @Override
            public void actionPerformed(ActionEvent e) {

                if(answerCollector != null && isButtonActive) answerCollector.notifyCollector(input.getText());

                isButtonActive = false;
                answerCollector = null;

            }



        }



    }

    private static class CreateLobbyPanel extends JPanel {


        private String creator = null;

        private final SendInfoCreateLobbyButton sendNameButton = new SendInfoCreateLobbyButton();
        private final JPanel inputPanel;
        private final JTextField inputNameLobby;
        private final JTextField inputCapacityLobby;
        private final JTextField inputPublicOrPrivateLobby;
        private final JTextField inputPassword;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        private CreateLobbyPanel() {

            titleLabel.setText("Create Lobby");

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridLayout(10,1));


            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

            JLabel insertNameLobby = new JLabel("Insert lobby name:     ");
            inputNameLobby = new JTextField();
            inputNameLobby.setColumns(30);
            inputNameLobby.setFont(inputFont);
            inputPanel.add(insertNameLobby);
            inputPanel.add(inputNameLobby);

            JLabel insertCapacity = new JLabel("Insert lobby capacity:      ");
            inputCapacityLobby = new JTextField();
            inputCapacityLobby.setColumns(30);
            inputCapacityLobby.setFont(inputFont);
            inputPanel.add(insertCapacity);
            inputPanel.add(inputCapacityLobby);


            JLabel insertLobbyPublicOrNo = new JLabel("Lobby public? y/n :     ");
            inputPublicOrPrivateLobby = new JTextField();
            inputPublicOrPrivateLobby.setColumns(30);
            inputPublicOrPrivateLobby.setFont(inputFont);
            inputPanel.add(insertLobbyPublicOrNo);
            inputPanel.add(inputPublicOrPrivateLobby);



            JLabel insertpassword = new JLabel("Insert password :     ");
            inputPassword = new JTextField();
            inputPassword.setColumns(30);
            inputPassword.setFont(inputFont);
            inputPanel.add(insertpassword);
            inputPanel.add(inputPassword);


            inputPanel.add(sendNameButton);
            this.add(inputPanel, BorderLayout.CENTER);

            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));
            this.add(titleLabel, BorderLayout.NORTH);


        }


        private void setCreator(String creator) {

            this.creator = creator;
        }

        private void setAnswerCollector(AnswerCollector answerCollector) {

            this.answerCollector = answerCollector;

        }

        private void setIsButtonActiveTrue(){this.isButtonActive = true;}

        private class SendInfoCreateLobbyButton extends JButton implements ActionListener {


            private SendInfoCreateLobbyButton() {

                super("Send");
                this.addActionListener(this);

            }



            @Override
            public void actionPerformed(ActionEvent e) {


                MenuMessage createMessage = null;

                if(answerCollector != null && isButtonActive){

                    int inputCapacity;

                    try {

                        inputCapacity = Integer.parseInt(inputCapacityLobby.getText());

                    }catch (Exception ex){

                        inputCapacity = -1;
                    }


                    if(inputPublicOrPrivateLobby.getText().toUpperCase().equals("Y") || inputPublicOrPrivateLobby.getText().toUpperCase().equals("N") ) {


                        if (inputPublicOrPrivateLobby.getText().toUpperCase().equals("Y")) {

                            createMessage = MenuMessage.newMenuMessageCreatePublic(inputNameLobby.getText(), inputCapacity, creator);

                        } else {


                            createMessage = MenuMessage.newMenuMessageCreatePrivate(inputNameLobby.getText(), inputCapacity, inputPassword.getText(), creator);

                        }

                    }

                    answerCollector.notifyCollector(createMessage);
                }

                isButtonActive = false;
                answerCollector = null;

            }



        }





    }

    private static class ParticipateLobbyPublicPanel extends JPanel {


        private String creator = null;

        private final SendInfoParticipateobbyPublicButton sendNameButton = new SendInfoParticipateobbyPublicButton();
        private final JPanel inputPanel;
        private final JTextField inputCapacityLobby;
        private final JTextField inputNameLobby;
        private final JTextField inputCasual;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        private ParticipateLobbyPublicPanel() {

            titleLabel.setText("Participate Lobby Public");

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridLayout(10,1));


            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

            JLabel insertNameLobby = new JLabel("Insert lobby name:     ");
            inputNameLobby = new JTextField();
            inputNameLobby.setColumns(30);
            inputNameLobby.setFont(inputFont);
            inputPanel.add(insertNameLobby);
            inputPanel.add(inputNameLobby);


            JLabel insertCasual = new JLabel("Do you want a casual lobby? y/n:      ");
            inputCasual = new JTextField();
            inputCasual.setColumns(30);
            inputCasual.setFont(inputFont);
            inputPanel.add(insertCasual);
            inputPanel.add(inputCasual);


            JLabel insertCapacity = new JLabel("Insert lobby capacity:      ");
            inputCapacityLobby = new JTextField();
            inputCapacityLobby.setColumns(30);
            inputCapacityLobby.setFont(inputFont);
            inputPanel.add(insertCapacity);
            inputPanel.add(inputCapacityLobby);


            inputPanel.add(sendNameButton);
            this.add(inputPanel, BorderLayout.CENTER);

            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));
            this.add(titleLabel, BorderLayout.NORTH);


        }


        private void setCreator(String creator) {

            this.creator = creator;
        }

        private void setAnswerCollector(AnswerCollector answerCollector) {

            this.answerCollector = answerCollector;

        }

        private void setIsButtonActiveTrue(){this.isButtonActive = true;}

        private class SendInfoParticipateobbyPublicButton extends JButton implements ActionListener {


            private SendInfoParticipateobbyPublicButton(){

                super("Send");
                this.addActionListener(this);

            }



            @Override
            public void actionPerformed(ActionEvent e) {


                MenuMessage participateMessage = null;

                int lobbyCapacity;

                if(answerCollector != null && isButtonActive){

                    try {
                        lobbyCapacity = Integer.parseInt(inputCapacityLobby.getText());
                    }catch(Exception ex){

                        lobbyCapacity = -1;

                    }


                    if( (inputCasual.getText().toUpperCase().equals("Y") || inputCasual.getText().toUpperCase().equals("N") ) && (lobbyCapacity == 3 || lobbyCapacity == 2)){

                        if(inputCasual.getText().toUpperCase().equals("Y") ) {

                            participateMessage = MenuMessage.newMenuMessageCasual(creator, lobbyCapacity);

                        }

                        else{

                            participateMessage = MenuMessage.newMenuMessagePartPublic(inputNameLobby.getText(), creator);

                        }

                    }

                }

                    answerCollector.notifyCollector(participateMessage);


                isButtonActive = false;
                answerCollector = null;

            }
        }
    }

    private static class ParticipateLobbyPrivatePanel extends JPanel {


        private String creator = null;

        private final SendInfoParticipateLobbyPrivateButton sendNameButton = new SendInfoParticipateLobbyPrivateButton();
        private final JPanel inputPanel;
        private final JTextField inputNameLobby;
        private final JTextField inputPassword;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        private ParticipateLobbyPrivatePanel() {

            titleLabel.setText("Participate Lobby Private");

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridLayout(10,1));


            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

            JLabel insertNameLobby = new JLabel("Insert lobby name:     ");
            inputNameLobby = new JTextField();
            inputNameLobby.setColumns(30);
            inputNameLobby.setFont(inputFont);
            inputPanel.add(insertNameLobby);
            inputPanel.add(inputNameLobby);


            JLabel insertpassword = new JLabel("Insert password :     ");
            inputPassword = new JTextField();
            inputPassword.setColumns(30);
            inputPassword.setFont(inputFont);
            inputPanel.add(insertpassword);
            inputPanel.add(inputPassword);


            inputPanel.add(sendNameButton);
            this.add(inputPanel, BorderLayout.CENTER);

            titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));
            this.add(titleLabel, BorderLayout.NORTH);


        }


        private void setCreator(String creator) {

            this.creator = creator;
        }

        private void setAnswerCollector(AnswerCollector answerCollector) {

            this.answerCollector = answerCollector;

        }

        private void setIsButtonActiveTrue(){this.isButtonActive = true;}

        private class SendInfoParticipateLobbyPrivateButton extends JButton implements ActionListener {


            private SendInfoParticipateLobbyPrivateButton() {

                super("Send");
                this.addActionListener(this);

            }



            @Override
            public void actionPerformed(ActionEvent e) {


                MenuMessage participateMessage = null;

                if(answerCollector != null && isButtonActive){

                    participateMessage = MenuMessage.newMenuMessagePartPrivate(inputNameLobby.getText(), inputPassword.getText(), creator);
                    answerCollector.notifyCollector(participateMessage);
                }

                isButtonActive = false;
                answerCollector = null;

            }
        }



    }




    private static class AnswerCollector implements Runnable {

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

    public void setMenuGuiVisible(boolean visible) {
        this.setVisible(visible);
    }

    @Override
    public String askForName() {

        AnswerCollector answerCollector = new AnswerCollector();

        Thread collector = new Thread(answerCollector);

        collector.start();

        insertNamePanel.setIsButtonActiveTrue();
        insertNamePanel.setAnswerCollector(answerCollector);

        try {
            collector.join();
        } catch (InterruptedException e) {

            e.printStackTrace();
            Thread.currentThread().interrupt();
        }


        return (String) answerCollector.giveAnswer();



    }

    @Override
    public boolean askBooleanQuestion(String message) {
        Object[] options = {"Yes", "No"};
        int answer = JOptionPane.showOptionDialog(this, "You have to choose!", message, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return answer == JOptionPane.YES_OPTION;
    }

    @Override
    public Date askForDate() {

        Date date = null;
        cardLayout.show(cardsPanel, INSERTBIRTHPANEL);

        do {

            AnswerCollector answerCollector = new AnswerCollector();

            Thread collector = new Thread(answerCollector);

            collector.start();

            insertBirthdayPanel.setIsButtonActiveTrue();
            insertBirthdayPanel.setAnswerCollector(answerCollector);

            try {
                collector.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }


            String dataInput = (String) answerCollector.giveAnswer();

            String regexData = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/([0-9]{4}$)";
            Pattern datePattern = Pattern.compile(regexData);
            Matcher matcherDate = datePattern.matcher(dataInput);

            if (matcherDate.find()) {

                int giorno = Integer.parseInt(matcherDate.group(1));
                int mese = Integer.parseInt(matcherDate.group(2));
                int anno = Integer.parseInt(matcherDate.group(3));


                try {

                    date = new Date(giorno, mese, anno);

                } catch (DataFormatException e) {
                    //da vedere
                }
            }


            if(date == null) insertBirthdayPanel.titleLabel.setText("WRONG FORMAT");

        }while(date == null);

        insertBirthdayPanel.titleLabel.setText("GOOD FORMAT");

        return date;


    }

    @Override
    public void printMenuMessage(String message) {

        JOptionPane.showMessageDialog(this, message);


    }

    @Override
    public MenuMessage askForInfoToCreateLobby(String creator) {

        MenuMessage createLobbyInfo = null;
        cardLayout.show(cardsPanel, CREATELOBBYPANEL);

        int numberOfErrors = 1;

        do {

            AnswerCollector answerCollector = new AnswerCollector();

            Thread collector = new Thread(answerCollector);

            collector.start();

            createLobbyPanel.setCreator(creator);
            createLobbyPanel.setIsButtonActiveTrue();
            createLobbyPanel.setAnswerCollector(answerCollector);

            try {
                collector.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            createLobbyInfo = (MenuMessage) answerCollector.giveAnswer();


            if(createLobbyInfo != null) {
                if (createLobbyInfo.getNumberOfPlayers() > 3 || createLobbyInfo.getNumberOfPlayers() < 2) {

                    createLobbyInfo = null;
                    createLobbyPanel.titleLabel.setText("Wrong input n° " + numberOfErrors);
                    numberOfErrors++;
                }

            }

            else{

                createLobbyPanel.titleLabel.setText("Wrong input n° " + numberOfErrors);
                numberOfErrors++;
            }


        }while(createLobbyInfo == null);

        createLobbyPanel.titleLabel.setText("GOOD");

        return createLobbyInfo;
    }

    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        MenuMessage participateLobbyInfo = null;


        if(isPublic){ cardLayout.show(cardsPanel, PARTICIPATELOBBYPUBLICPANEL); }

        else cardLayout.show(cardsPanel, PARTICIPATELOBBYPRIVATEPANEL);


        int numberOfErrors = 1;

        do {

            AnswerCollector answerCollector = new AnswerCollector();

            Thread collector = new Thread(answerCollector);

            collector.start();

            if(isPublic) {

                participateLobbyPublicPanel.setCreator(namePlayer);
                participateLobbyPublicPanel.setIsButtonActiveTrue();
                participateLobbyPublicPanel.setAnswerCollector(answerCollector);

            }

            else{

                participateLobbyPrivatePanel.setCreator(namePlayer);
                participateLobbyPrivatePanel.setIsButtonActiveTrue();
                participateLobbyPrivatePanel.setAnswerCollector(answerCollector);

            }

            try {
                collector.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            participateLobbyInfo = (MenuMessage) answerCollector.giveAnswer();



            if(participateLobbyInfo == null) {

                if(isPublic) participateLobbyPublicPanel.titleLabel.setText("Wrong input n° " + numberOfErrors);
                else participateLobbyPrivatePanel.titleLabel.setText("Wrong input n° " + numberOfErrors);
                numberOfErrors++;

            }


        }while(participateLobbyInfo == null);

        if(isPublic) participateLobbyPublicPanel.titleLabel.setText("GOOD");
        else participateLobbyPrivatePanel.titleLabel.setText("GOOD");

        return participateLobbyInfo;



    }

}
