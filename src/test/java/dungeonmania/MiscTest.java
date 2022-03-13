package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.StaticEntities.*;
import dungeonmania.models.StaticEntities.HealthPotion;
import dungeonmania.models.StaticEntities.InvisibilityPotion;
import dungeonmania.models.StaticEntities.Shield;
import dungeonmania.models.StaticEntities.TheOneRing;
import dungeonmania.models.StaticEntities.ZombieToasterSpawner;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Position;

public class MiscTest {
    @Test
    public void testDungeons() {
        assertTrue(DungeonManiaController.dungeons().size() > 0);
        assertTrue(DungeonManiaController.dungeons().contains("maze"));
    }

    @Test
    public void testLoadingEntities() throws InterruptedException {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse d1 = dc.newGame("3x3maze", "standard");
        Dungeon dungeon = dc.getDungeon(d1.getDungeonId());

        Shield e1 = new Shield("1", new Position(0, 0));
        Bow e2 = new Bow("2", new Position(0, 0));
        BodyArmour e3 = new BodyArmour("3", new Position(0, 0));
        TheOneRing e4 = new TheOneRing("4", new Position(0, 0));
        Wood e5 = new Wood("5", new Position(0, 0));
        HealthPotion e6 = new HealthPotion("6", new Position(0, 0));
        InvisibilityPotion e7 = new InvisibilityPotion("7", new Position(0, 0));

        Switch e8 = new Switch("8", new Position(0, 0));
        Boulder e9 = new Boulder("9", new Position(0, 0));
        ZombieToasterSpawner e10 = new ZombieToasterSpawner("10", new Position(0, 0));

        dungeon.getPlayer().addToInventory(e1);
        dungeon.getPlayer().addToInventory(e2);
        dungeon.getPlayer().addToInventory(e3);
        dungeon.getPlayer().addToInventory(e4);
        dungeon.getPlayer().addToInventory(e5);
        dungeon.getPlayer().addToInventory(e6);
        dungeon.getPlayer().addToInventory(e7);

        dungeon.addEntity(e8);
        dungeon.addEntity(e9);
        dungeon.addEntity(e10);
        DungeonResponse savedGame = dc.saveGame(dungeon.getDungeonId());
        //Thread.sleep(800);
        DungeonResponse d2 = dc.loadGame(savedGame.getDungeonId());

        assertFalse(TestHelpers.getInventoryResponseList(d2, "shield").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "bow").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "armour").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "one_ring").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "wood").isEmpty());
        assertFalse(TestHelpers.getEntityResponseList(d2, "switch").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "health_potion").isEmpty());
        assertFalse(TestHelpers.getInventoryResponseList(d2, "invisibility_potion").isEmpty());
        assertFalse(TestHelpers.getEntityResponseList(d2, "boulder").isEmpty());
        assertFalse(TestHelpers.getEntityResponseList(d2, "zombie_toast_spawner").isEmpty());
    }

}