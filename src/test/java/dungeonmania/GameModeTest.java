package dungeonmania;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.MovingEntities.Spider;
import dungeonmania.models.StaticEntities.InvincibilityPotion;
import dungeonmania.models.StaticEntities.Potion;
import dungeonmania.models.StaticEntities.Wieldable;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import spark.utils.Assert;

public class GameModeTest {
    @Test
    void PeacefulModeNoPlayerInciatedBattleTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("enemiesBasic", "peaceful");
        Dungeon dungeon = dc.getDungeon(dr.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        assertFalse(dungeon.getGameModeFactory().isBattleEnabled());
        dc.tick(null, Direction.RIGHT);
        DungeonResponse response = dc.tick(null, Direction.NONE);
        response = dc.saveGame(response.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        response = dc.loadGame(response.getDungeonId());
        // if battles were running the player would inciate a battle
        // both the player and the character should still be on the map
        assertFalse(TestHelpers.getEntityList(dungeon, "spider").isEmpty());
        assertFalse(TestHelpers.getEntityList(dungeon, "player").isEmpty());
    }

    @Test
    void PeacefulModeNoCharacterInciatedBattleTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("mercenaryBasic", "peaceful");
        Dungeon dungeon = dc.getDungeon(dr.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        Mercenary s = (Mercenary) TestHelpers.getEntityList(dungeon, "mercenary").get(0);
        assertFalse(s.isBattleEnabled());
        dc.tick(null, Direction.NONE);
        DungeonResponse response = dc.tick(null, Direction.NONE);
        response = dc.saveGame(response.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        response = dc.loadGame(response.getDungeonId());
        // if battles were running the charcter would inciate a battle
        // both the player and the character should still be on the map
        assertFalse(TestHelpers.getEntityList(dungeon, "mercenary").isEmpty());
        assertFalse(TestHelpers.getEntityList(dungeon, "player").isEmpty());
    }

    @Test
    void HardModeZombieSpawnTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("enemiesBasic", "hard");
        Dungeon dungeon = dc.getDungeon(dr.getDungeonId());
        dungeon.getGameModeFactory().setMaxSpiders(1);
        dungeon.getPlayer().setHP(9000);
        for (int i = 0; i < 16; i++) {
            dc.tick(null, Direction.NONE);
        }
        DungeonResponse dr3 = dc.tick(null, Direction.NONE);

        dr3 = dc.saveGame(dr3.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dr3 = dc.loadGame(dr3.getDungeonId());
        List<EntityResponse> zombies = TestHelpers.getEntityResponseList(dr3, "zombie_toast");
        // check that there is now more than 1 zombie
        assertTrue(zombies.size() > 0);
    }

    @Test
    void HardModePlayerHealthTest() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("3x3maze", "hard").getDungeonId();
        Dungeon dungeon = dc.getDungeon(id);
        assertEquals(5, dungeon.getPlayer().getHP());
    }

    @Test
    void HardModeInvincibilityNoEffectTest() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dr = dc.newGame("invincibleExit", "hard");
        String dungeonId = dr.getDungeonId();
        Dungeon dungeon = dc.getDungeon(dr.getDungeonId());
        Player player = dungeon.getPlayer();
        dungeon.getGameModeFactory().setMaxSpiders(1);

        DoBattle mercenary = (DoBattle) TestHelpers.getEntityList(dungeon, "mercenary").get(0);
        // set mercenary HP to something crazy high to ensure player will lose
        mercenary.setHP(100000);

        // pick up potion
        dc.tick(null, Direction.UP);

        List<Entity> potionList = player.getInventory().getIfContains("invincibility_potion");
        assertEquals(potionList.size(), 1);
        Potion potion = (Potion) potionList.get(0);
        Entity Epotion = (Entity) potion;
        assertFalse(potion.instantWinBattle());
        player.setHP(1);

        // test player even after drinking potion loses battle
        // moving right will trigger battle with mercenary
        dc.tick(Epotion.getId(), Direction.DOWN);

        // dungeon response no longer has player
        dr = dc.getDungeon(dungeonId).returnDungeonResponse();
        List<EntityResponse> endR = dr.getEntities();
        List<EntityResponse> playerR = endR.stream().filter(e -> e.getType().equals("player"))
                .collect(Collectors.toList());
        assertEquals(playerR.size(), 0);

    }

    @Test
    public void loadGameModeTest() throws InterruptedException {
        // check that when a saved game is loaded it has the relevant game mode settings
        DungeonManiaController dc1 = new DungeonManiaController();
        DungeonResponse d1 = dc1.newGame("3x3maze", "standard");
        DungeonResponse sd1 = dc1.saveGame(d1.getDungeonId());
        Thread.sleep(600);
        DungeonResponse ld1 = dc1.loadGame(d1.getDungeonId());
        Dungeon sdun1 = dc1.getDungeon(d1.getDungeonId());

        assertEquals("standard", sdun1.getGameMode());
        assertEquals(20, sdun1.getGameModeFactory().getFreqZombies());

        DungeonManiaController dc2 = new DungeonManiaController();
        DungeonResponse d2 = dc2.newGame("3x3maze", "hard");
        DungeonResponse sd2 = dc2.saveGame(d2.getDungeonId());
        Thread.sleep(1000);
        DungeonResponse ld2 = dc2.loadGame(d2.getDungeonId());
        Dungeon sdun2 = dc2.getDungeon(d2.getDungeonId());

        assertEquals("hard", sdun2.getGameMode());
        assertEquals(15, sdun2.getGameModeFactory().getFreqZombies());
        assertEquals(5, sdun2.getPlayer().getInitialHP());
        Thread.sleep(300);

        DungeonManiaController dc3 = new DungeonManiaController();
        DungeonResponse d3 = dc3.newGame("3x3maze", "peaceful");
        DungeonResponse sd3 = dc3.saveGame(d3.getDungeonId());
        Thread.sleep(1000);
        DungeonResponse ld3 = dc3.loadGame(d3.getDungeonId());
        Dungeon sdun3 = dc3.getDungeon(d3.getDungeonId());

        assertEquals("peaceful", sdun3.getGameMode());
        assertEquals(false, sdun3.getGameModeFactory().isBattleEnabled());
    }

    @Test
    public void saveGameModeTest() {
        // test that when a game is saved it saves its game mode
        DungeonManiaController dc1 = new DungeonManiaController();
        DungeonResponse d1 = dc1.newGame("3x3maze", "standard");
        DungeonResponse sd1 = dc1.saveGame(d1.getDungeonId());
        Dungeon sdun1 = dc1.getDungeon(d1.getDungeonId());
        assertEquals("standard", sdun1.getGameMode());

        DungeonManiaController dc2 = new DungeonManiaController();
        DungeonResponse d2 = dc2.newGame("3x3maze", "hard");
        DungeonResponse ld2 = dc2.saveGame(d2.getDungeonId());
        Dungeon sdun2 = dc2.getDungeon(d2.getDungeonId());
        assertEquals("hard", sdun2.getGameMode());

        DungeonManiaController dc3 = new DungeonManiaController();
        DungeonResponse d3 = dc3.newGame("3x3maze", "peaceful");
        DungeonResponse ld3 = dc3.saveGame(d3.getDungeonId());
        Dungeon sdun3 = dc3.getDungeon(d3.getDungeonId());
        assertEquals("peaceful", sdun3.getGameMode());
    }
}
