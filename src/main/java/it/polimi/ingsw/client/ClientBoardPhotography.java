package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.BoxPhotography;

import javax.swing.*;

/**
 * this class is used to simplify interaction between the BoardPhotography object and the ClientViewAdapter
 * and it also stores the ImageIcons for the boxes of the GUI
 * holds a copy of the last boardPhotography received
 */
public class ClientBoardPhotography {

    private BoardPhotography actualBoardPhotography = new BoardPhotography();

    /**
     * controls if the board has been modified and if yes it saves the new one and updates its representation on GUI or ClI
     *
     * @param boardPhotography that has been received last
     * @return true if it has changed
     */
    public boolean updateClientBoardPhotography(BoardPhotography boardPhotography){

        if( ! this.actualBoardPhotography.equals(boardPhotography) ){
            this.actualBoardPhotography = boardPhotography;
            ClientViewAdapter.showBoard(boardPhotography);
            return true;
        }

        else return false;

    }

    public BoxPhotography getBox(int row, int col){

        return this.actualBoardPhotography.getBoxPhoto(row, col);

    }


    //this are simply imageicons for the board on the GUI

    public final ImageIcon level0NoWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/level0NoWorker.png"));
    public final ImageIcon level1NoWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/level1NoWorker.png"));
    public final ImageIcon level2NoWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/level2NoWorker.png"));
    public final ImageIcon level3NoWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/level3NoWorker.png"));

    public final ImageIcon level0RedWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/0RedWorker.png"));
    public final ImageIcon level1RedWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/1RedWorker.png"));
    public final ImageIcon level2RedWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/2RedWorker.png"));
    public final ImageIcon level3RedWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/3RedWorker.png"));

    public final ImageIcon level0YellowWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/0YellowWorker.png"));
    public final ImageIcon level1YellowWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/1YellowWorker.png"));
    public final ImageIcon level2YellowWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/2YellowWorker.png"));
    public final ImageIcon level3YellowWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/3YellowWorker.png"));

    public final ImageIcon level0GreenWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/0GreenWorker.png"));
    public final ImageIcon level1GreenWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/1GreenWorker.png"));
    public final ImageIcon level2GreenWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/2GreenWorker.png"));
    public final ImageIcon level3GreenWorker = new ImageIcon(this.getClass().getClassLoader().getResource("Images/3GreenWorker.png"));

    public final ImageIcon dome = new ImageIcon(this.getClass().getClassLoader().getResource("Images/dome.png"));


}
