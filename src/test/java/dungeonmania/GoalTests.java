package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class GoalTests {

    @Test
    public void goalANDPrintTest() {
        // controller, new game
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
        assertEquals("(:mercenary AND :exit)", exitgame.getGoals());
    }

    @Test
    public void goalORPrintTest() {
        // controller, new game
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("enemiesExitOR", "standard");
        assertEquals("(:mercenary OR :exit)", exitgame.getGoals());
    }

    @Test
    public void goalPrintTreasureNumberTest() {
        // controller, new game
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("5treasure", "standard");
        assertEquals(":treasure(5)", exitgame.getGoals());
        DungeonResponse d1 = dc.tick(null, Direction.RIGHT);
        assertEquals(":treasure(4)", d1.getGoals());
        DungeonResponse d2 = dc.tick(null, Direction.RIGHT);
        assertEquals(":treasure(3)", d2.getGoals());
        dc.tick(null, Direction.RIGHT);
        DungeonResponse d3 = dc.tick(null, Direction.DOWN);
        assertEquals(":treasure", d3.getGoals());
        DungeonResponse d4 = dc.tick(null, Direction.DOWN);
        assertEquals("", d4.getGoals());
    }

    @Test
    public void goalEnemyUpdateTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
        DungeonResponse dr1 = dc.tick(null, Direction.UP);
        Dungeon currDungeon = dc.getDungeon(exitgame.getDungeonId());
        Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0);
        Player player = (Player) playerE;
        player.setHP(10000);
        assertEquals("(:mercenary(2) AND :exit)", dr1.getGoals());
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);
        dr2 = dc.tick(null, Direction.DOWN);
        assertEquals("(:spider AND :exit)", dr2.getGoals());
        DungeonResponse dr3 = dc.tick(null, Direction.RIGHT);
        assertEquals("(:spider AND :exit)", dr3.getGoals());
        dc.tick(null, Direction.LEFT);
        DungeonResponse dr4 = dc.tick(null, Direction.DOWN);
        assertEquals(":spider", dr4.getGoals());
        DungeonResponse dr5 = dc.tick(null, Direction.DOWN);
        assertEquals(":exit", dr5.getGoals());
    }

    @Test
    public void goalBoulderUpdateTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("boulderGoal", "standard");
        assertEquals("(:exit AND :boulder(4)/:switch(3))", exitgame.getGoals());
        dc.tick(null, Direction.RIGHT);
        DungeonResponse d1 = dc.tick(null, Direction.DOWN);
        assertEquals("(:exit AND :boulder(3)/:switch(2))", d1.getGoals());
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse d2 = dc.tick(null, Direction.DOWN);
        assertEquals("(:exit AND :boulder(2)/:switch)", d2.getGoals());
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.RIGHT);
        DungeonResponse d3 = dc.tick(null, Direction.DOWN);
        assertEquals(":exit", d3.getGoals());
        DungeonResponse d4 = dc.tick(null, Direction.DOWN);
        assertEquals("(:exit AND :boulder(2)/:switch)", d4.getGoals());
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.UP);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.DOWN);
        DungeonResponse d5 = dc.tick(null, Direction.LEFT);
        assertEquals(":exit", d5.getGoals());
    }

    @Test
    public void goalExitUpdateTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("treasureExit", "standard");
        assertEquals("(:treasure AND :exit)", exitgame.getGoals());
        dc.tick(null, Direction.DOWN);
        DungeonResponse dr1 = dc.tick(null, Direction.RIGHT);
        assertEquals(":treasure", dr1.getGoals());
        DungeonResponse dr2 = dc.tick(null, Direction.RIGHT);
        assertEquals("(:treasure AND :exit)", dr2.getGoals());
        dc.tick(null, Direction.UP);
        DungeonResponse dr3 = dc.tick(null, Direction.LEFT);
        assertEquals(":exit", dr3.getGoals());
        dc.tick(null, Direction.DOWN);
    }

    @Test
    public void goalNestedAndOr1Test() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("NestedAndOr", "standard");
        assertEquals("((:treasure OR :mercenary) AND :exit)", exitgame.getGoals());
        Dungeon currDungeon = dc.getDungeon(exitgame.getDungeonId());
        Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0);
        Player player = (Player) playerE;
        player.setHP(10000);
        DungeonResponse d1 = dc.tick(null, Direction.RIGHT);
        assertEquals(":exit", d1.getGoals());
        DungeonResponse d2 = dc.tick(null, Direction.DOWN);
        assertEquals("", d2.getGoals());
    }

    @Test
    public void goalNestedAndOr2Test() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("NestedAndOr", "standard");
        assertEquals("((:treasure OR :mercenary) AND :exit)", exitgame.getGoals());
        Dungeon currDungeon = dc.getDungeon(exitgame.getDungeonId());
        Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0);
        Player player = (Player) playerE;
        player.setHP(10000);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.LEFT);
        dc.tick(null, Direction.DOWN);
        DungeonResponse d1 = dc.tick(null, Direction.LEFT);
        DungeonResponse d2 = dc.tick(null, Direction.UP);
        assertEquals(":exit", d1.getGoals());
        assertEquals("", d2.getGoals());
    }

    @Test
    public void goalNestedOrAnd1Test() {
        // TO DO: TEST GAME with nested and then or
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("NestedOrAnd", "standard");
        assertEquals("((:treasure AND :mercenary) OR :exit)", exitgame.getGoals());
        dc.tick(null, Direction.DOWN);
        DungeonResponse d1 = dc.tick(null, Direction.RIGHT);
        assertEquals("", d1.getGoals());
    }

    @Test
    public void goalNestedOrAnd2Test() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("NestedOrAnd", "standard");
        assertEquals("((:treasure AND :mercenary) OR :exit)", exitgame.getGoals());
        DungeonResponse d1 = dc.tick(null, Direction.RIGHT);
        assertEquals("(:spider OR :exit)", d1.getGoals());
        DungeonResponse d2 = dc.tick(null, Direction.DOWN);
        assertEquals("", d2.getGoals());
    }

    @Test
    public void goalComplexNest1Test() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("complexNest", "standard");
        assertEquals("(((:treasure AND :boulder/:switch) OR :mercenary) AND :exit)", exitgame.getGoals());
        DungeonResponse dr1 = dc.tick(null, Direction.RIGHT);
        assertEquals(":exit", dr1.getGoals());
        DungeonResponse dr2 = dc.tick(null, Direction.DOWN);
        assertEquals("", dr2.getGoals());
    }

    @Test
    public void goalComplexNest2Test() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse exitgame = dc.newGame("complexNest", "standard");
        assertEquals("(((:treasure AND :boulder/:switch) OR :mercenary) AND :exit)", exitgame.getGoals());
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.DOWN);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.RIGHT);
        dc.tick(null, Direction.UP);
        DungeonResponse dr2 = dc.tick(null, Direction.DOWN);
        DungeonResponse dr1 = dc.tick(null, Direction.UP); // treasure ??m & exit
        dr2 = dc.tick(null, Direction.DOWN); // exit
        System.out.println(dr1.getGoals());
        assertTrue(dr1.getGoals().equals(":exit"));
        assertEquals(":exit", dr2.getGoals());
    }

    @Test
    public void loadGameWithNoGoals() throws InterruptedException {
        // checks everything is where they should be
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon1 = dc.newGame("noGoal", "standard");
        assertEquals("", dungeon1.getGoals());
        dc.saveGame(dungeon1.getDungeonId());
        Thread.sleep(1000);
        DungeonResponse wallgame = dc.loadGame(dungeon1.getDungeonId());
        dc.saveGame(wallgame.getDungeonId());

        assertEquals(dungeon1.getGoals(), wallgame.getGoals());
        assertEquals(dungeon1.getDungeonName(), wallgame.getDungeonName());
        assertEquals(dungeon1.getDungeonId(), wallgame.getDungeonId());

        List<String> d1IDs = new ArrayList<>();
        List<String> wallIDs = new ArrayList<>();
        dungeon1.getEntities().forEach(e -> d1IDs.add(e.getId()));
        wallgame.getEntities().forEach(e -> wallIDs.add(e.getId()));
        TestHelpers.assertListAreEqualIgnoringOrder(dungeon1.getBuildables(), wallgame.getBuildables());
        TestHelpers.assertListAreEqualIgnoringOrder(d1IDs, wallIDs);
    }
}
