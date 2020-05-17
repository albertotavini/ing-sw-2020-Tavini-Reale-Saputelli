package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.piece.Block;
import it.polimi.ingsw.server.model.piece.Dome;
import it.polimi.ingsw.server.model.piece.Piece;
import java.util.ArrayList;
import java.util.List;

public class Box {

    private Worker occupier;
    private ArrayList<Piece> tower;
    private boolean domed;
    private boolean isComplete;
    private final int row;
    private final  int column;

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

    //method to support atlas' effect, the parse is done in its SpecialEffect
    public void placeDome() {
        int height = tower.size() + 1;
        tower.add(new Dome(height));
        domed = true;
    }

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

    public Worker getOccupier() { return occupier; }

    public void setOccupier(Worker occupier) { this.occupier = occupier; }

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
}
