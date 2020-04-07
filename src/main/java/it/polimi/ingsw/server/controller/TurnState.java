package it.polimi.ingsw.server.controller;

public interface TurnState {

}

//every state is singleton
class SelectionState implements TurnState {

    private static SelectionState instance = new SelectionState();

    public static SelectionState getInstance(){
        return instance;
    }

}

class MoveState implements TurnState {

    private static MoveState instance = new MoveState();

    public static MoveState getInstance(){
        return instance;
    }

}

class BuildState implements TurnState {

    private static BuildState instance = new BuildState();

    public static BuildState getInstance() {
        return instance;
    }

}

/*    public interface TurnState {
        //Modifier "public" is redundant for interface methods
        void updateState(NewController controller, playerMove message);
    }

    class SelectionState implements TurnState {

        private static SelectionState instance = new SelectionState();

        //empty constructor
        private SelectionState(){}

        public static SelectionState getInstance(){
            return instance;
        }

        @Override
        public void updateState(NewController controller, playerMove message){
            System.out.println("I'm in SelectionState");
            if (controller.getMatch().getCurrentPlayer().getPersonalTurn().selectWorker(controller.getMatch().getGameboard(), message.getRow(), message.getColumn()))
                //updating state
                controller.setCurrentTurnState(MoveState.getInstance());
        }
    }

    class MoveState implements TurnState {

        private static MoveState instance = new MoveState();

        //empty constructor
        private MoveState(){}

        public static MoveState getInstance(){
            return instance;
        }

        @Override
        public void updateState(NewController controller, playerMove message){
            System.out.println("I'm in MoveState");
        }

    }

    class BuildState implements TurnState {

        private static BuildState instance = new BuildState();

        //empty constructor
        private BuildState() {
        }

        public static BuildState getInstance() {
            return instance;
        }

        @Override
        public void updateState(NewController controller, playerMove message) {
            System.out.println("I'm in MoveState");
            if (controller.getMatch().getCurrentPlayer().getPersonalTurn().build(controller.getMatch().getGameboard(), message.getRow(), message.getColumn())) {
                System.out.println("Turn is complete!");
                controller.getMatch().updateTurn();
            }
        }

    }*/


