package it.polimi.ingsw.client;



import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.utils.CliAesthetics;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * this interface is meant to provide methods for the user to interact with the game (inserting input) and for the game to show messages on the cli or the
 * while the player is in the menu part of the game
 */
public interface MenuUserInterface {

    String askForName();
    boolean askBooleanQuestion(String message);
    Date askForDate();
    void printMenuMessage(String message);
    MenuMessage askForInfoToCreateLobby(String creator);
    MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer);


}


/**
 * this class is the version of MenuUserInterface used when the player chooses the CLI version of the game
 */
class MenuCli implements MenuUserInterface {

    /**
     * this method demands the string via Scanner that the player will use as username
     * @return the string the player inserted
     */
    @Override
    public String askForName() {

        String[] insertName = {"InsertName"};

        CliAesthetics.printBox(insertName);

        return ClientMain.scannerIn.nextLine();
    }

    /**
     *
     * this method is used to demand the answer to a boolean question
     * the player inserts a string that is catch via scanner
     * if its Y will return true, if N will return false
     * if another string will demand input again
     *
     * @param message the text with the question to answer
     * @return true or false depending on player's answer
     */
    @Override
    public boolean askBooleanQuestion(String message) {
        String conferma = null;

        do{

                System.out.println(message);
                try {
                    conferma = ClientMain.scannerIn.nextLine();
                    conferma = conferma.toUpperCase();
                }catch (Exception ignored){
                    conferma = "Y";
                }


        }while(!(conferma.equals("Y") || conferma.equals("N")));

        return conferma.equals("Y");
    }

    /**
     * asks from standard input a date in format dd/mm/dddd until the player inserts a correct one
     * @return date correctly inserted
     */
    @Override
    public Date askForDate() {

        Date data = null;

        do{

            String[] askDate = {"Insert date of birth with format gg/mm/aaaa:"};

            CliAesthetics.printBox(askDate);

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
                    //not important because will simply demand it again
                }
            }

        }while(data == null);

        return data;

    }

    /**
     * prints message that the model gives to suggest next input/ state of the game
     *
     * @param message that is given in ModelMessage
     */
    @Override
    public void printMenuMessage(String message) {

        String[] mess = {message};

        CliAesthetics.printBox(mess);

    }

    /**
     * this method creates a message for server that will comunicate if the player wants to create a public or private lobby
     * the number of players will be parsed if not 2 or 3
     * the server will then accept or refuse the proposal if a lobby of the same type and with the same name exists
     *
     * @param nameCreator the name of player that is interacting with this interface
     * @return the message that will set the game
     */
    @Override
    public MenuMessage askForInfoToCreateLobby(String nameCreator) {

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;

        String nomeLobby = null;

        String passwordLobby;
        String ripetizionePassword;

        String isPublic;


        //Color set


        String[] askInf1 = {"Insert lobby name:"};

        CliAesthetics.printBox(askInf1);

        //when restarting a game could launch exception, so better have a try catch
        nomeLobby = ClientMain.scannerIn.nextLine();

        do {

            System.out.println("Insert preferred lobby capacity:");
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

            System.out.printf("%s", "Do you prefer a public or private lobby? pu/pr\n");
            isPublic = ClientMain.scannerIn.nextLine();
            isPublic = isPublic.toUpperCase();

        }while(!(isPublic.equals("PU") || isPublic.equals("PR")));

        MenuMessage createLobbyInfo;

        //creo una lobby privata
        if(isPublic.equals("PR")) {

            do {

                System.out.println("Insert lobby password:");
                passwordLobby = ClientMain.scannerIn.nextLine();
                System.out.println("Insert lobby password again:");
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



        return createLobbyInfo;
    }

    /**
     * the method will ask the info for joining a public or private lobby, which will then be parsed by the server once send
     *
     *
     * @param isPublic if the lobby is public, false if private
     * @param namePlayer that is demanding to join
     * @return message to be sent to server
     */
    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        String nomeLobby;
        String passwordLobby;

        String capienzaLobby;
        int capacity = 0;
        boolean correctInput = false;
        Matcher matcherCapacity;
        Pattern capacityLobbyPattern;
        MenuMessage lobbyInfoToParticipate = null;
        boolean wantsLobbyCasual = false;


        if(isPublic) {

            wantsLobbyCasual = askBooleanQuestion("Wanna join a casual lobby? y/n");

            if (wantsLobbyCasual) {

                do {

                    System.out.println("Insert lobby capacity" );
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

          System.out.println("Insert lobby name");
          nomeLobby = ClientMain.scannerIn.nextLine();

          if (isPublic) {

              //creo un messaggio utilizzando il costruttore per messaggi di participate pubblica
              lobbyInfoToParticipate = MenuMessage.newMenuMessagePartPublic(nomeLobby, namePlayer);

          }

          if (!isPublic) {

              System.out.println("Insert password lobby:");
              passwordLobby = ClientMain.scannerIn.nextLine();
              //creo un messaggio utilizzando il costruttore per messaggi di participate privata
              lobbyInfoToParticipate = MenuMessage.newMenuMessagePartPrivate(nomeLobby, passwordLobby, namePlayer);
          }

      }






        return lobbyInfoToParticipate;
    }



}


/**
 * this class is the version of MenuUserInterface used when the player chooses the GUI version of the game
 */
class MenuGui extends JFrame implements MenuUserInterface {

    private final static CardLayout cardLayout = new CardLayout();
    private final static JPanel cardsPanel = new JPanel(cardLayout);

    private final WelcomePanel welcomePanel;
    private final static String WELCOMEPANEL = "WELCOME CARD";

    private final InsertStringPanel insertNamePanel;
    private final static String INSERT_NAME_PANEL = "INSERT NAME CARD";

    private final InsertStringPanel insertBirthdayPanel;
    private final static String INSERT_BIRTH_PANEL = "INSERT BIRTH CARD";

    private final CreateLobbyPanel createLobbyPanel;
    private final static String CREATE_LOBBY_PANEL = "CREATE LOBBY CARD";

    private final ParticipateLobbyPublicPanel participateLobbyPublicPanel;
    private final static String PARTICIPATE_LOBBY_PUBLIC_PANEL = "PARTICIPATE LOBBY PUBLIC CARD";

    private final ParticipateLobbyPrivatePanel participateLobbyPrivatePanel;
    private final static String PARTICIPATE_LOBBY_PRIVATE_PANEL = "PARTICIPATE LOBBY PRIVATE CARD";

    private final QuestionBooleanPanel questionBooleanPanel;
    private final static String QUESTION_PANEL = "QUESTION CARD";

    private final WaitingInLobbyPanel waitingInLobbyPanel;
    private final static String WAITING_PANEL = "WAITING CARD";


    /**
     * constructor that instantiates menugui, that is built whit a panel that uses CardLayout
     */
    public MenuGui() {

        super("Santorini : The Game");
        this.setSize(1000,800);
        setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(cardsPanel);

        welcomePanel = new WelcomePanel();
        cardsPanel.add(welcomePanel, WELCOMEPANEL);

        insertNamePanel = new InsertStringPanel(Global.INSERTUSERNAME);
        cardsPanel.add(insertNamePanel, INSERT_NAME_PANEL);

        insertBirthdayPanel = new InsertStringPanel(Global.INSERTBIRTHDATE);
        cardsPanel.add(insertBirthdayPanel, INSERT_BIRTH_PANEL);

        createLobbyPanel = new CreateLobbyPanel();
        cardsPanel.add(createLobbyPanel, CREATE_LOBBY_PANEL);

        participateLobbyPublicPanel = new ParticipateLobbyPublicPanel();
        cardsPanel.add(participateLobbyPublicPanel, PARTICIPATE_LOBBY_PUBLIC_PANEL);

        participateLobbyPrivatePanel = new ParticipateLobbyPrivatePanel();
        cardsPanel.add(participateLobbyPrivatePanel, PARTICIPATE_LOBBY_PRIVATE_PANEL);

        questionBooleanPanel = new QuestionBooleanPanel();
        cardsPanel.add(questionBooleanPanel, QUESTION_PANEL);

        waitingInLobbyPanel = new WaitingInLobbyPanel();
        cardsPanel.add(waitingInLobbyPanel, WAITING_PANEL);



        this.setVisible(true);


    }

    /**
     * will make the menugui visible or not as needed
     *
     * @param visible true or false as needed
     */
    public void setMenuGuiVisible(boolean visible) {
        this.setVisible(visible);
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

    /**
     * method to show the correct panel while the player waits
     */
    public static void setWaitInLobby(){

        cardLayout.show(cardsPanel, WAITING_PANEL);

    }

    private static Icon resizeIcon(ImageIcon icon, int buttonWidth, int buttonHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(buttonWidth, buttonHeight,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }


    /**
     * method to clean the menugui's textboxes when starting another game after finishing one
     */
    public void resetMenuGui(){
        createLobbyPanel.inputCapacityLobby.setText(null);
        createLobbyPanel.inputPassword.setText(null);
        createLobbyPanel.inputNameLobby.setText(null);
        participateLobbyPublicPanel.inputCapacityLobby.setText(null);
        participateLobbyPublicPanel.inputNameLobby.setText(null);
        participateLobbyPrivatePanel.inputNameLobby.setText(null);
        participateLobbyPrivatePanel.inputPassword.setText(null);
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

        private BufferedImage matchPanelImage;
        private boolean alreadyPassed = false;

        private final StartButton start = new StartButton();

        public final ImageIcon startButtonImage = new ImageIcon( this.getClass().getClassLoader().getResource("MenuImages/StartButton.jpg"));


        /**
         * constructor of the welcome panel, that will just show an image and have a START button
         */
        public WelcomePanel() {

            this.setLayout(new BorderLayout());

            try {
                matchPanelImage = ImageIO.read(this.getClass().getClassLoader().getResource("MenuImages/start.jpg"));

            } catch (IOException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }

            start.setSize(new Dimension(1000,120));
            start.setIcon(resizeIcon(startButtonImage, 1000, 133));
            this.add(start, BorderLayout.SOUTH);

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            /*Graphics2D ig = matchPanel.createGraphics();
            ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ig.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            ig.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Graphics2D g2draw = (Graphics2D) g.create();*/

            Image resizedImage = matchPanelImage.getScaledInstance(1000, 667,  Image.SCALE_SMOOTH);
            g.drawImage(resizedImage, 0, -25, this);
        }

        private static Icon resizeIcon(ImageIcon icon, int buttonWidth, int buttonHeight) {
            Image img = icon.getImage();
            Image resizedImage = img.getScaledInstance(buttonWidth, buttonHeight,  java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }

        private boolean getAlreadyPassed(){
            return alreadyPassed;
        }

        private class StartButton extends JButton implements ActionListener {

            private StartButton() {

                this.addActionListener(this);

            }


            @Override
            public void actionPerformed(ActionEvent e) {

                alreadyPassed = true;
                cardLayout.show(cardsPanel, INSERT_NAME_PANEL);

            }
        }


    }

    private static class InsertStringPanel extends JPanel {

        private final SendInputButton sendInputButton = new SendInputButton();
        private final JPanel inputPanel;
        private final JTextField input;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;

        public InsertStringPanel(String textLabel) {

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridBagLayout());

            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 25);

            if(textLabel.equals(Global.INSERTUSERNAME)) {

                final ImageIcon setNameImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/insertname.png"));
                JLabel imageLabel = new JLabel(resizeIcon(setNameImage, 605, 500));
                imageLabel.setSize(new Dimension(605, 500));
                this.add(imageLabel, BorderLayout.NORTH);

            }
            else if(textLabel.equals(Global.INSERTBIRTHDATE)){

                final ImageIcon setBirthdateImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/insertbirthdate.png"));
                JLabel imageLabel = new JLabel(resizeIcon(setBirthdateImage, 605, 500));
                imageLabel.setSize(new Dimension(605, 500));
                this.add(imageLabel, BorderLayout.NORTH);

            }

            JLabel insertInputLabel = new JLabel(textLabel);
            input = new JTextField();
            input.setColumns(25);
            input.setFont(inputFont);

            inputPanel.add(insertInputLabel);
            inputPanel.add(input);
            inputPanel.add(sendInputButton);
            this.add(inputPanel, BorderLayout.CENTER);

        }

        private static Icon resizeIcon(ImageIcon icon, int buttonWidth, int buttonHeight) {
            Image img = icon.getImage();
            Image resizedImage = img.getScaledInstance(buttonWidth, buttonHeight,  java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        }

        private void setAnswerCollector(AnswerCollector answerCollector) {

            this.answerCollector = answerCollector;

        }

        private void setIsButtonActiveTrue(){this.isButtonActive = true;}


        private class SendInputButton extends JButton implements ActionListener {


            private SendInputButton() {

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
        private final JTextField inputPassword;
        private final CheckLobbyPublic checkLobbyPublic;
        private final JLabel insertNameLobby;
        private final JLabel insertCapacity;
        private final JLabel insertPassword;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        private CreateLobbyPanel() {final ImageIcon choosePublicImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/createMenu.png"));

            titleLabel.setIcon(resizeIcon(choosePublicImage, 1000, 100));

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridLayout(15,2));


            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

            checkLobbyPublic = new CheckLobbyPublic();
            inputPanel.add(checkLobbyPublic);


            insertNameLobby = new JLabel("Insert lobby name:     ");
            inputNameLobby = new JTextField();
            inputNameLobby.setColumns(20);
            inputNameLobby.setFont(inputFont);
            inputPanel.add(insertNameLobby);
            inputPanel.add(inputNameLobby);

            insertCapacity = new JLabel("Insert lobby capacity:      ");
            inputCapacityLobby = new JTextField();
            inputCapacityLobby.setColumns(20);
            inputCapacityLobby.setFont(inputFont);
            inputPanel.add(insertCapacity);
            inputPanel.add(inputCapacityLobby);


            insertPassword= new JLabel("Insert password :     ");
            inputPassword = new JTextField();
            inputPassword.setColumns(20);
            inputPassword.setFont(inputFont);
            inputPanel.add(insertPassword);
            inputPanel.add(inputPassword);
            inputPassword.setVisible(false);
            insertPassword.setVisible(false);


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





                        if (checkLobbyPublic.isSelected()) {

                            createMessage = MenuMessage.newMenuMessageCreatePublic(inputNameLobby.getText(), inputCapacity, creator);

                        } else {

                            createMessage = MenuMessage.newMenuMessageCreatePrivate(inputNameLobby.getText(), inputCapacity, inputPassword.getText(), creator);

                        }

                    answerCollector.notifyCollector(createMessage);
                }

                isButtonActive = false;
                answerCollector = null;

            }



        }

        private class CheckLobbyPublic extends JCheckBox implements ItemListener {

            CheckLobbyPublic() {

                super("Do you want a public lobby?", true);
                this.addItemListener(this);

            }


            @Override
            public void itemStateChanged(ItemEvent e) {

                if(this.isSelected()){

                    inputPassword.setVisible(false);
                    insertPassword.setVisible(false);



                }

                if(!this.isSelected()){

                    inputPassword.setVisible(true);
                    insertPassword.setVisible(true);

                }


            }
        }





    }

    private static class ParticipateLobbyPublicPanel extends JPanel {


        private String creator = null;

        private final SendInfoParticipateobbyPublicButton sendNameButton = new SendInfoParticipateobbyPublicButton();
        private final JPanel inputPanel;
        private final JTextField inputCapacityLobby;
        private final JLabel insertCapacity;
        private final JTextField inputNameLobby;
        private final JLabel insertNameLobby;

        private final CheckCasual checkCasual;
        private final JLabel titleLabel = new JLabel();

        private AnswerCollector answerCollector = null;
        private boolean isButtonActive = false;



        private ParticipateLobbyPublicPanel() {

            final ImageIcon choosePublicImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/chooseLobbyPublic.png"));

            titleLabel.setIcon(resizeIcon(choosePublicImage, 1000, 100));

            this.setLayout(new BorderLayout());

            inputPanel = new JPanel(new GridLayout(10,1));


            Font inputFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);


            insertCapacity = new JLabel("Insert preferred lobby capacity:      ");
            inputCapacityLobby = new JTextField();
            inputCapacityLobby.setColumns(30);
            inputCapacityLobby.setFont(inputFont);
            inputPanel.add(insertCapacity);
            inputPanel.add(inputCapacityLobby);


            checkCasual = new CheckCasual();
            inputPanel.add(checkCasual);


            insertNameLobby = new JLabel("Insert lobby name:     ");
            inputNameLobby = new JTextField();
            inputNameLobby.setColumns(30);
            inputNameLobby.setFont(inputFont);
            inputPanel.add(insertNameLobby);
            inputPanel.add(inputNameLobby);
            inputNameLobby.setVisible(false);
            insertNameLobby.setVisible(false);


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


                        if(checkCasual.isSelected() && (lobbyCapacity == 3 || lobbyCapacity == 2)) {

                            participateMessage = MenuMessage.newMenuMessageCasual(creator, lobbyCapacity);

                        }

                        else{

                            participateMessage = MenuMessage.newMenuMessagePartPublic(inputNameLobby.getText(), creator);

                        }


                    answerCollector.notifyCollector(participateMessage);

                }




                isButtonActive = false;
                answerCollector = null;

            }
        }

        private class CheckCasual extends JCheckBox implements ItemListener {

            CheckCasual() {

                super("Do you want a casual lobby?", true);
                this.addItemListener(this);

            }


            @Override
            public void itemStateChanged(ItemEvent e) {

                if(this.isSelected()){

                    inputNameLobby.setVisible(false);
                    insertNameLobby.setVisible(false);
                    inputCapacityLobby.setVisible(true);
                    insertCapacity.setVisible(true);


                }

                if(!this.isSelected()){

                    inputCapacityLobby.setVisible(false);
                    insertNameLobby.setVisible(true);
                    inputNameLobby.setVisible(true);
                    insertCapacity.setVisible(false);

                }


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
            final ImageIcon choosePublicImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/chooseLobbyPrivate.png"));

            titleLabel.setIcon(resizeIcon(choosePublicImage, 1000, 100));

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

    private static class QuestionBooleanPanel extends JPanel {

        //private final JTextPane question;
        private final JLabel question;
        private final ButtonConfirm buttonYes;
        private final ButtonConfirm buttonNo;
        private AnswerCollector answerCollector = null;


        private QuestionBooleanPanel(){

            this.setLayout(new GridLayout(6,1));

            //question = new JTextPane();
            question = new JLabel(" ");
            //question.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            buttonYes = new ButtonConfirm("Yes", true);
            buttonNo = new ButtonConfirm("No", false);
            buttonNo.setFont(new Font (Font.SANS_SERIF, Font.PLAIN, 25));
            buttonYes.setFont(new Font (Font.SANS_SERIF, Font.PLAIN, 25));

            this.add(question);
            this.add(buttonYes);
            this.add(buttonNo);

        }


        private boolean askBooleanToQuestionPane(String message) {

            //question.setText(message);

            if(message.equals(Global.DOYOUWANTTOCREATEANEWLOBBY)){
                final ImageIcon setNameImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/createLobby.png"));
                question.setSize(new Dimension(1000, 100));
                question.setIcon(resizeIcon(setNameImage, 1000, 100));
            }
            else if(message.equals(Global.DOYOUWANTTOJOINAPUBLICLOBBY)){
                final ImageIcon setNameImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/joinPublicLobby.png"));
                question.setSize(new Dimension(1000, 100));
                question.setIcon(resizeIcon(setNameImage, 1000, 100));
            }
            else if(message.equals(Global.DOYOUWANTTORESTART)){
                final ImageIcon setNameImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/restartLobby.png"));
                question.setSize(new Dimension(1000, 100));
                question.setIcon(resizeIcon(setNameImage, 1000, 100));
            }
            else{

                question.setText(message);
            }

            answerCollector = new AnswerCollector();

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

        private class ButtonConfirm extends JButton implements ActionListener{

            private boolean isButtonActive = false;

            private final boolean buttonValue;

            private ButtonConfirm(String text, boolean buttonValue) {

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

    private static class WaitingInLobbyPanel extends JPanel {

        private BufferedImage waitingPanel;

        public final ImageIcon waitingImage = new ImageIcon(this.getClass().getClassLoader().getResource("MenuImages/WaitingImage.gif"));


        private WaitingInLobbyPanel() {


            this.setLayout(new BorderLayout());
            this.setBackground(Color.WHITE);

            try {

                JLabel label = new JLabel(waitingImage, JLabel.CENTER);
                this.add(label, BorderLayout.CENTER);
                this.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


    /**
     * demands to the player his name by taking it from an insertstringpanel
     *
     * @return the string inserted
     */
    @Override
    public String askForName() {

        if(welcomePanel.getAlreadyPassed()) cardLayout.show(cardsPanel, INSERT_NAME_PANEL);

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


    /**
     * method to ask a boolean question on the gui
     *
     * @param message to be shown while asking
     * @return true or false depending on the answer
     */
    @Override
    public boolean askBooleanQuestion(String message) {


        cardLayout.show(cardsPanel, QUESTION_PANEL);

        return questionBooleanPanel.askBooleanToQuestionPane(message);

    }

    /**
     * demands to the player his date by taking it from an insertstringpanel
     *
     * @return string inserted
     */
    @Override
    public Date askForDate() {

        Date date = null;
        cardLayout.show(cardsPanel, INSERT_BIRTH_PANEL);

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

    /**
     * method to display the an eventual message on a JoptionPane
     * @param message to be printed
     */
    @Override
    public void printMenuMessage(String message) {

        JOptionPane.showMessageDialog(this, message);


    }

    /**
     * by using the corrects panels from the cards panel this method will demand info to create a lobby
     *
     * @param creator name of the player interacting
     * @return message to send to the server, that will parse it
     */
    @Override
    public MenuMessage askForInfoToCreateLobby(String creator) {

        MenuMessage createLobbyInfo = null;
        cardLayout.show(cardsPanel, CREATE_LOBBY_PANEL);

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

    /**
     *
     *by using the corrects panels from the cards panel this method will demand info to join a lobby
     *
     *
     * @param isPublic true if the lobby to join is public, false if private
     * @param namePlayer that is handling the interface
     * @return message to send to the server, that will parse it
     */
    @Override
    public MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {

        MenuMessage participateLobbyInfo = null;


        if(isPublic){ cardLayout.show(cardsPanel, PARTICIPATE_LOBBY_PUBLIC_PANEL); }

        else cardLayout.show(cardsPanel, PARTICIPATE_LOBBY_PRIVATE_PANEL);


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
