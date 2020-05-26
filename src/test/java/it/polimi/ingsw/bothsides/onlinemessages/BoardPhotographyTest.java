package it.polimi.ingsw.bothsides.onlinemessages;

import it.polimi.ingsw.server.model.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardPhotographyTest {


    void settingDefaultBoard(BoardPhotography boardPhotography){
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                boardPhotography.setBoxPhoto(i, j, new BoxPhotography(i, j, 0, false, false, null));
            }
        }
    }

    @Test
    void equalsOnLevelsTest() {
        BoardPhotography boardPhotography1 = new BoardPhotography();
        BoardPhotography boardPhotography2 = new BoardPhotography();

        //instantiating both boardPhotographies with default Boxes
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);

        //setting level 1 on (0,0) on bf1
        boardPhotography1.setBoxPhoto(0, 0, new BoxPhotography(0, 0, 1, false, false, null));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on bf1
        settingDefaultBoard(boardPhotography1);
        assertEquals(boardPhotography1, boardPhotography2);

        //setting level 3 on (3,3) on bf2
        boardPhotography2.setBoxPhoto(3, 3, new BoxPhotography(3, 3, 3, false, false, null));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on bf2
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);

        //setting level 1 on two different boxes:(0,0) on bf1 and (4,4) on bf2
        boardPhotography1.setBoxPhoto(0, 0, new BoxPhotography(0, 0, 1, false, false, null));
        boardPhotography2.setBoxPhoto(0, 0, new BoxPhotography(4, 4, 1, false, false, null));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on both bf1 and bf2
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);
    }

    @Test
    void equalsOnDomeTest(){
        BoardPhotography boardPhotography1 = new BoardPhotography();
        BoardPhotography boardPhotography2 = new BoardPhotography();

        //instantiating both boardPhotographies with default Boxes
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);

        //doming (1,1) on bf1
        boardPhotography1.setBoxPhoto(1, 1, new BoxPhotography(1, 1, 0, true, false, null));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on bf1
        settingDefaultBoard(boardPhotography1);
        assertEquals(boardPhotography1, boardPhotography2);

        //doming two different boxes:(0,0) on bf1 and (4,4) on bf2
        boardPhotography1.setBoxPhoto(0, 0, new BoxPhotography(0, 0, 0, true, false, null));
        boardPhotography2.setBoxPhoto(0, 0, new BoxPhotography(4, 4, 0, true, false, null));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on both bf1 and bf2
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);
    }

    @Test
    void equalsOnOccupiersTest(){
        BoardPhotography boardPhotography1 = new BoardPhotography();
        BoardPhotography boardPhotography2 = new BoardPhotography();

        //instantiating both boardPhotographies with default Boxes
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);

        //setting an occupier on (2,2) on bf1
        boardPhotography1.setBoxPhoto(2, 2, new BoxPhotography(2, 2, 0, false, true, Color.GREEN));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //setting the same occupier on (2,2) on bf2
        boardPhotography2.setBoxPhoto(2, 2, new BoxPhotography(2, 2, 0, false, true, Color.GREEN));
        assertEquals(boardPhotography1, boardPhotography2);
        //changing occupier's color on bf2
        boardPhotography2.setBoxPhoto(2, 2, new BoxPhotography(2, 2, 0, false, true, Color.RED));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on bf1
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);

        //occupying two different boxes with the same worker:(0,0) on bf1 and (4,4) on bf2
        boardPhotography1.setBoxPhoto(0, 0, new BoxPhotography(0, 0, 0, false, true, Color.YELLOW));
        boardPhotography2.setBoxPhoto(0, 0, new BoxPhotography(4, 4, 0, false, true, Color.YELLOW));
        assertNotEquals(boardPhotography1, boardPhotography2);
        //settings again default boxes on both bf1 and bf2
        settingDefaultBoard(boardPhotography1);
        settingDefaultBoard(boardPhotography2);
        assertEquals(boardPhotography1, boardPhotography2);
    }

}