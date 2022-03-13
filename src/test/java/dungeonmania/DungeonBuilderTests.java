package dungeonmania;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import dungeonmania.DungeonManiaController;
import dungeonmania.models.Dungeon;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;



public class DungeonBuilderTests {

    @Test
    public void testGenerateDungeonDoesCompileNoForeverLoop() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.generateDungeon(3, 3, 47, 47, "standard"); 
    }
    
    @Test
    public void testPlayerStartPositionHasBFSEmptySpacePathToEndExitCase1() {
        // Start and end positions are all odd
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse generated = dc.generateDungeon(3, 3, 47, 47, "peaceful");
        String dungeonId = generated.getDungeonId();
        Dungeon innerDungeon = dc.getDungeon(dungeonId);
        
        // check that player is at the start position before moving anywhere
        assertEquals(new Position(3,3), TestHelpers.getPlayerPosition(generated));
        // check that there is only one 'exit' at the end position
        List<EntityResponse> exits = TestHelpers.getEntityResponseList(generated, "exit");
        assertEquals(1, exits.size());
        assertEquals(new Position(47,47), exits.get(0).getPosition());
        // check that the goal is to exit
        String goalString = generated.getGoals();
        assertEquals(":exit", goalString);
        // check that there is exists a bfs path to the exit
        assertTrue(bfsPathExistsFromPlayerToExit(TestHelpers.getPlayerPosition(generated), exits.get(0).getPosition(), innerDungeon));
    }

    @Test
    public void testPlayerStartPositionHasBFSEmptySpacePathToEndExitCase2() {
        // Start and end positions are all even
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse generated = dc.generateDungeon(4, 4, 46, 46, "peaceful");
        String dungeonId = generated.getDungeonId();
        Dungeon innerDungeon = dc.getDungeon(dungeonId);
        
        // check that player is at the start position before moving anywhere
        assertEquals(new Position(4, 4), TestHelpers.getPlayerPosition(generated));
        // check that there is only one 'exit' at the end position
        List<EntityResponse> exits = TestHelpers.getEntityResponseList(generated, "exit");
        assertEquals(1, exits.size());
        assertEquals(new Position(46, 46), exits.get(0).getPosition());
        // check that the goal is to exit
        String goalString = generated.getGoals();
        assertEquals(":exit", goalString);
        // check that there is exists a bfs path to the exit
        assertTrue(bfsPathExistsFromPlayerToExit(TestHelpers.getPlayerPosition(generated), exits.get(0).getPosition(), innerDungeon));
    }

    @Test
    public void testPlayerStartPositionHasBFSEmptySpacePathToEndExitCase3() {
        // One pair odd, one pair even
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse generated = dc.generateDungeon(5, 5, 46, 46, "peaceful");
        String dungeonId = generated.getDungeonId();
        Dungeon innerDungeon = dc.getDungeon(dungeonId);
        
        // check that player is at the start position before moving anywhere
        assertEquals(new Position(5, 5), TestHelpers.getPlayerPosition(generated));
        // check that there is only one 'exit' at the end position
        List<EntityResponse> exits = TestHelpers.getEntityResponseList(generated, "exit");
        assertEquals(1, exits.size());
        assertEquals(new Position(46, 46), exits.get(0).getPosition());
        // check that the goal is to exit
        String goalString = generated.getGoals();
        assertEquals(":exit", goalString);
        // check that there is exists a bfs path to the exit
        assertTrue(bfsPathExistsFromPlayerToExit(TestHelpers.getPlayerPosition(generated), exits.get(0).getPosition(), innerDungeon));
    }

    @Test
    public void testPlayerStartPositionHasBFSEmptySpacePathToEndExitCase4() {
        // Odd and even pairs (odd x and even y)
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse generated = dc.generateDungeon(5, 4, 46, 45, "peaceful");
        String dungeonId = generated.getDungeonId();
        Dungeon innerDungeon = dc.getDungeon(dungeonId);
        
        // check that player is at the start position before moving anywhere
        assertEquals(new Position(5, 4), TestHelpers.getPlayerPosition(generated));
        // check that there is only one 'exit' at the end position
        List<EntityResponse> exits = TestHelpers.getEntityResponseList(generated, "exit");
        assertEquals(1, exits.size());
        assertEquals(new Position(46, 45), exits.get(0).getPosition());
        // check that the goal is to exit
        String goalString = generated.getGoals();
        assertEquals(":exit", goalString);
        // check that there is exists a bfs path to the exit
        assertTrue(bfsPathExistsFromPlayerToExit(TestHelpers.getPlayerPosition(generated), exits.get(0).getPosition(), innerDungeon));
    }

    /*
    @Test
    public void testPredictedMazeGeneratedFromGivenRandomSeedDifferentResults() {
        // test ~3 different generated dungeons

    }*/

    private static boolean bfsPathExistsFromPlayerToExit(Position start, Position end, Dungeon innerDungeon) {
        // get positions of all walls
        List<Position> wallPositions = innerDungeon.getEntities().stream()
            .filter(entity -> entity.getType() == "wall")
            .map(e -> e.getPosition())
            .collect(Collectors.toList());
        // create queue
        Queue<Position> queue = new LinkedList<>();
        // create visited 2d matirx
        int dWidth = innerDungeon.getWidth();
        int dHeight = innerDungeon.getHeight();
        boolean[][] visited = new boolean[dWidth][dHeight]; // by default they are initialised all to false

        // put the start position into queue
        queue.add(start);
        visited[start.getX()][start.getY()] = true;
        // While there is position to be handled in the queue
        while (!queue.isEmpty()) {
            // remove top position
            Position currPosition = queue.poll();
            // if currPosition is equivalent to end position, path exists
            if (currPosition.equals(end)) {
                return true;
            }
            // get all free adjacent positions, i.e. that are not a wall 
            List<Position> adjacents = currPosition.getAdjacentPositions().stream()
                .filter(p -> !(wallPositions.contains(p)))
                .collect(Collectors.toList());
            // Get all valid positions
            adjacents = adjacents.stream()
                .filter(p -> (p.getX() > 0 && p.getX() < 49))
                .filter(p -> (p.getY() > 0 && p.getY() < 49))
                .collect(Collectors.toList());
            // If an adjacent has not been visited, then mark it
            // visited and enqueue it
            for (Position a : adjacents) {
                if (visited[a.getX()][a.getY()] == false) {
                    visited[a.getX()][a.getY()] = true; 
                    queue.add(a);
                }
            }
      
        }

        return false;
    }

}
