package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.onlinemessages.BoxPhotography;
import it.polimi.ingsw.server.model.piece.Block;
import it.polimi.ingsw.server.model.piece.Dome;
import it.polimi.ingsw.server.model.piece.Piece;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the box: the main element of the game board.
 * It also contains informations about the occupier of the Box, its tower's level
 * and its coordinates.
 */
public class Box {

    private Worker occupier;
    private ArrayList<Piece> tower;
    private boolean domed;
    private boolean isComplete;
    private final int row;
    private final int column;

    /**
     * The constructor initializes the Box object, setting its own row and column and
     * setting the box without occupiers or pieces.
     *
     * @param row the box's row
     * @param column the box's column
     */
    Box(int row, int column){
        this.occupier = null;
        this.tower = new ArrayList<>() ;
        this.row = row;
        this.column = column;
        domed = false;
        isComplete = false;
    }

    private void setTower(ArrayList<Piece> tower){ this.tower = tower; }

    public boolean isDomed() { return domed; }

    private void setDomed(boolean b) { domed = b; }

    public int getRow() { return row; }

    public List<Piece> getTower() { return tower; }

    public int getColumn() { return column; }

    public boolean isComplete() {
        return isComplete;
    }

    public Worker getOccupier() { return occupier; }

    public void setOccupier(Worker occupier) { this.occupier = occupier; }


    /**
     * This method is needed for Atlas effect.
     * It puts a dome on the current Box, increasing the Box's level and making it domed.
     */
    public void placeDome() {
        int height = tower.size() + 1;
        tower.add(new Dome(height));
        domed = true;
    }

    /**
     * This method increases the current Box's level if it is currently less than four,
     * doming and setting complete the Box if the new height is 4.
     */
    public void increaseLevel () {
        int height = tower.size() + 1;
        if (height < 4) {
            tower.add(new Block(height));
        }
        else if (height == 4) {
            tower.add(new Dome(height));
            domed = true;
            isComplete = true;
        }
    }

    /**
     * This method decreases the current Box's level if it is currently bigger than 0,
     * setting the Box not domed and not completed.
     */
    public void decreaseLevel() {
        int height = tower.size ();
        if( domed ) {
            domed = false;
        }
        if( isComplete ){
            isComplete = false;
        }
        if (height > 0) {
            tower.remove(tower.size() - 1);
        }
    }

    public String toString() {
        if ((getOccupier() == null) && (tower.isEmpty())) {
            return "-- --";
        }
        else if ((getOccupier() != null) && (tower.isEmpty())) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" --";
        }
        else if ((getOccupier() == null) && (!tower.isEmpty()) && (!isDomed())) {
            return "-- "+tower.get(tower.size()-1).getLevel()+"-";
        }
        else if ((getOccupier() == null) && (!tower.isEmpty()) && (isDomed())) {
            return "-- "+tower.get(tower.size()-1).getLevel()+"*";
        }
        else if (getOccupier() != null && !tower.isEmpty() && (isDomed())) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" "+tower.get(tower.size()-1).getLevel()+"*";
        }
        else if (getOccupier() != null && !tower.isEmpty() && (!isDomed())) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" "+tower.get(tower.size()-1).getLevel()+"-";
        }
        else return "err";
    }

    /**
     * @return a photograph of the Box, with all its attributes.
     */
    BoxPhotography photographBox() {
        boolean occupied;
        Color color;
        if (getOccupier() == null ) {
            color = null;
            occupied = false;
        }
        else  {
            occupied = true;
            color = getOccupier().getColour();
        }

        return new BoxPhotography(getRow(), getColumn(), getTower().size() , isDomed(), occupied, color );
    }

    private Piece cloneTowerElement(Piece piece) {
        if (piece instanceof Block) {
            return new Block (piece.getLevel());
        }
        else {
            return new Dome (piece.getLevel());
        }
    }

    Box cloneBox() {
        Box clonedBox = new Box(this.getRow(), this.getColumn());
        if( this.getOccupier() != null) {
            clonedBox.setOccupier(new Worker(this.getOccupier().getPlayer(), this.getOccupier().getColour(), this.getOccupier().getWorkerTag()));
        }
        clonedBox.setDomed(this.isDomed());
        ArrayList <Piece> provTower = new ArrayList<>();
        this.getTower().forEach( p-> provTower.add(cloneTowerElement(p)));
        clonedBox.setTower(provTower);
        return clonedBox;
    }
}
