package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.piece.Block;
import it.polimi.ingsw.server.model.piece.Dome;
import it.polimi.ingsw.server.model.piece.Piece;

import java.util.Stack;
import java.util.stream.Collectors;

public class Box {

    final private int row;
    final private int column;
    private Worker occupier;
    private Stack<Piece> tower;
    private int towerSize;
    private boolean domed;

    public Box (int row, int column){
        this.occupier = null;
        this.tower = new Stack<Piece>();
        tower.add(new Block(0));
        this.row = row;
        this.column = column;
        domed = false;
    }

    public void setTower(Stack <Piece> tower){ this.tower = tower; }

    public boolean isDomed() { return domed; }

    public void setDomed(boolean b) { domed = b; }

    public int getRow() { return row; }

    public Stack<Piece> getTower() { return tower; }

    public int getColumn() { return column; }

    public int getTowerSize() { return towerSize; }

    public void setTowerSize(int towerSize){
        this.towerSize = towerSize;
    }

    //method to support atlas' effect, the parse is done in its SpecialEffect
    public void placeDome() {
        int height = tower.get(tower.size() - 1).getLevel() + 1;
        setTowerSize(height);
        tower.add(new Dome(height));
        domed = true;
    }

    public void increaseLevel () {
        int height = tower.get(tower.size() - 1).getLevel() + 1;
        if (height < 4) {
            tower.add(new Block(height));
            setTowerSize(height);
        }
        else if (height == 4) {
            tower.add(new Dome(height));
            setTowerSize(height);
            domed = true;
        }
        else { System.out.println("This tower is complete."); }
    }

    public void decreaseLevel() {
        int height = tower.get(tower.size() - 1).getLevel();
        if( domed ) {
            domed = false;
        }
        if (height > 0) {
            tower.remove(tower.size() - 1);
            setTowerSize(height - 1);
        }
        else {System.out.println("This tower doesn't have floors.");}
    }

    public Worker getOccupier() { return occupier; }

    public void setOccupier(Worker occupier) { this.occupier = occupier; }

    public String toString() {
        if ((getOccupier() == null) && (tower.size()==1)) {
            return "-- --";
        }
        else if ((getOccupier() != null) && (tower.size()==1)) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" --";
        }
        else if ((getOccupier() == null) && (tower.size()>1) && (!isDomed())) {
            return "-- "+tower.get(tower.size()-1).getLevel()+"-";
        }
        else if ((getOccupier() == null) && (tower.size()>1) && (isDomed())) {
            return "-- "+tower.get(tower.size()-1).getLevel()+"*";
        }
        else if (getOccupier() != null && tower.size()>1 && (isDomed())) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" "+tower.get(tower.size()-1).getLevel()+"*";
        }
        else if (getOccupier() != null && tower.size()>1 && (!isDomed())) {
            return getOccupier().getColour().abbrev()+getOccupier().getWorkerTag()+" "+tower.get(tower.size()-1).getLevel()+"-";
        }
        else return "err";
    }

    public Piece cloneTowerElement(Piece piece) {
        if (piece instanceof Block) {
            return new Block (piece.getLevel());
        }
        else {
            return new Dome (piece.getLevel());
        }
    }

    public Box cloneBox () {
        Box clonedBox = new Box(this.getRow(), this.getColumn());
        if( this.getOccupier() != null) {
            clonedBox.setOccupier(new Worker(this.getOccupier().getPlayer(), this.getOccupier().getColour(), this.getOccupier().getWorkerTag()));
        }
        clonedBox.setDomed(this.isDomed());
        Stack <Piece> provTower = new Stack<Piece>();
        this.getTower().forEach( p-> provTower.add(cloneTowerElement(p)));
        clonedBox.setTower(provTower);
        clonedBox.setTowerSize(this.getTowerSize());
        return clonedBox;
    }

}
