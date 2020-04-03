package it.polimi.ingsw.server;

public class WebPacket {

    //class that contains the user's input

    private Worker worker;

    private int box_move_x;
    private int box_move_y;

    private int box_build_x;
    private int box_build_y;


    public int getBox_build_x() {
        return box_build_x;
    }

    public int getBox_build_y() {
        return box_build_y;
    }

    public int getBox_move_x() {
        return box_move_x;
    }

    public int getBox_move_y() {
        return box_move_y;
    }

    public Worker getWorker() {
        return worker;
    }

    public WebPacket(int box_move_x, int box_move_y, int box_build_x, int box_build_y, Worker worker){

        this.box_move_x = box_move_x;
        this.box_move_y = box_move_y;

        this.box_build_x = box_build_x;
        this.box_build_y = box_build_y;

        this.worker = worker;
    }
}
