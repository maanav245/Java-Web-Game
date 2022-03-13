package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TimeTravelTests {

   @Test
   public void testSimpletTimeTurner() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse d1 = dc.newGame("timeSimple", "standard");
      dc.tick(null, Direction.RIGHT);
      dc.tick(null, Direction.UP);
      dc.tick(null, Direction.RIGHT);
      dc.tick(null, Direction.LEFT);
      DungeonResponse dungeon = dc.tick(null, Direction.LEFT);
      assertEquals(1, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      dungeon = dc.rewind(5);
      assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "time_turner").size());
      assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "player").size());
   }

   @Test
   public void testPlayerCantBattleOlderPlayerWithSunStone() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse dungeon = dc.newGame("timeSimple copy", "Standard");
      for(int i=0; i<3; i++) dungeon = dc.tick(null, Direction.LEFT);
      for(int i=0; i<12; i++) dungeon = dc.tick(null, Direction.RIGHT);
      for(int i=0; i<6; i++) dungeon = dc.tick(null, Direction.LEFT);

      String id = dungeon.getInventory().stream().filter(entity -> entity.getType().equals("invincibility_potion"))
         .collect(Collectors.toList()).get(0).getId();
      dungeon = dc.tick(id, Direction.LEFT); 

      assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "player").size());

      for(int i=0; i<3; i++) dungeon = dc.tick("", Direction.NONE);
      for(int i=0; i<5; i++) dungeon = dc.tick(null, Direction.LEFT);
      for(int i=0; i<3; i++) dungeon = dc.tick(null, Direction.RIGHT);

      assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "player").size());
      assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "older_player").size());

      for(int i=0; i<15; i++) dungeon = dc.tick(null, Direction.LEFT);
      assertEquals(2, TestHelpers.getEntityResponseList(dungeon, "player").size() + TestHelpers.getEntityResponseList(dungeon, "older_player").size());
   }

   @Test
   public void testPlayerCanBattleOlderPlayer() {
      DungeonManiaController dc = new DungeonManiaController();
      DungeonResponse dungeon = dc.newGame("timeSimple", "standard");
      dungeon = dc.tick(null, Direction.UP);
      dungeon = dc.tick(null, Direction.RIGHT);
      dungeon = dc.tick(null, Direction.RIGHT);
      dungeon = dc.tick(null, Direction.RIGHT);
      dungeon = dc.tick(null, Direction.RIGHT);

      dc.rewind(5); 

      dungeon = dc.tick(null, Direction.LEFT);
      dungeon = dc.tick(null, Direction.LEFT);
      dungeon = dc.tick(null, Direction.LEFT);

      assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "player").size() + TestHelpers.getEntityResponseList(dungeon, "older_player").size());

   }

   @Test 
   public void testOldDungeonsAreSavingCorrectly(){
      DungeonManiaController dc = new DungeonManiaController();
      dc.newGame("timeSimple", "standard");
      DungeonResponse dungeon5away = null, dungeon1away = null, dungeon30away = null; 
      for(int i=0; i<31; i++){
         if(i==0) dungeon30away = dc.tick(null, Direction.DOWN);
         else if(i==25) dungeon5away = dc.tick(null, Direction.DOWN);
         else if(i==29) dungeon1away = dc.tick(null, Direction.DOWN);
         else dc.tick(null, Direction.DOWN);
      }
      
      Position playerPosition = TestHelpers.getPlayerPosition(dungeon1away);
      assertEquals(playerPosition, dc.getOneTickAway().getPlayer().getPosition());

      playerPosition = TestHelpers.getPlayerPosition(dungeon30away);
      assertEquals(playerPosition, dc.getThirtyTickAway().getPlayer().getPosition());

      playerPosition = TestHelpers.getPlayerPosition(dungeon5away);
      assertEquals(playerPosition, dc.getFiveTickAway().getPlayer().getPosition());

   }
}
