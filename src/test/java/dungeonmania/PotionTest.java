package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Movement.Circle;
import dungeonmania.models.Movement.MoveAway;
import dungeonmania.models.Movement.MoveRandom;
import dungeonmania.models.Movement.MoveTowards;
import dungeonmania.models.Movement.MoveWith;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.models.MovingEntities.Character;

public class PotionTest {
    @Test
    public void testPotaionNewGame() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("Potions", "standard");

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "invisibility_potion").size());
        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "invincibility_potion").size());
        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "health_potion").size());
    }

    @Test
    public void testPotionLoadAndSave() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeonOld = dc.newGame("Potions", "standard");
        DungeonResponse dungeon = dc.saveGame(dungeonOld.getDungeonId());

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "invisibility_potion").size());
        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "invincibility_potion").size());
        assertEquals(1, TestHelpers.getEntityResponseList(dungeon, "health_potion").size());
    }

    @Test
    public void testPotionUse() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("Potions", "standard");
        dungeon = dc.tick(null, Direction.RIGHT);

        // player picked up potions
        assertEquals(0, TestHelpers.getEntityResponseList(dungeon, "invisibility_potion").size());
        assertEquals(0, TestHelpers.getEntityResponseList(dungeon, "invincibility_potion").size());
        assertEquals(0, TestHelpers.getEntityResponseList(dungeon, "health_potion").size());

        ItemResponse invis = TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").get(0);
        ItemResponse invince = TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").get(0);
        ItemResponse health = TestHelpers.getInventoryResponseList(dungeon, "health_potion").get(0);

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        dungeon = dc.tick(invis.getId(), Direction.NONE);
        dungeon = dc.tick(invince.getId(), Direction.NONE);
        dungeon = dc.tick(health.getId(), Direction.NONE);

        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());

        assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").size());
        assertEquals(0, TestHelpers.getInventoryResponseList(dungeon, "health_potion").size());

    }

    @Test
    public void testPotionChangeStrategy() {
        // Does not work with s/l
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("Potions", "standard");
        dungeon = dc.tick(null, Direction.RIGHT);

        ItemResponse invis = TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").get(0);
        ItemResponse invince = TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").get(0);
        ItemResponse health = TestHelpers.getInventoryResponseList(dungeon, "health_potion").get(0);

        // zombie Random aka no change, spider circle no change, Mercenary Random
        dungeon = dc.tick(invis.getId(), Direction.NONE);
        List<Entity> entities = dc.getDungeon(dungeon.getDungeonId()).getEntities();
        Character zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        Character spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        Character mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveRandom.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        assertEquals(MoveRandom.class, mercenary.getStrategy().getClass());

        // zombie MoveAway, spider circle no change, Mercenary moveAway
        dungeon = dc.tick(invince.getId(), Direction.NONE);
        entities = dc.getDungeon(dungeon.getDungeonId()).getEntities();
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveAway.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        assertEquals(MoveAway.class, mercenary.getStrategy().getClass());

        // no change
        dungeon = dc.tick(health.getId(), Direction.NONE);
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveAway.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        assertEquals(MoveAway.class, mercenary.getStrategy().getClass());

        // potions wear off, go back to normal, potions last 15 moves
        for (int i = 0; i < 15; i++)
            dungeon = dc.tick(null, Direction.NONE);
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveRandom.class, zombie.getStrategy().getClass());
        assertEquals(MoveTowards.class, mercenary.getStrategy().getClass());

    }

    @Test
    public void testPotionSceptreNoEffectBribeableEnemy() {
        // Does not work with s/l
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("PotionsSceptre", "standard");
        dungeon = dc.tick(null, Direction.RIGHT);
        String mercenaryID = TestHelpers.getEntityResponseList(dungeon, "mercenary").get(0).getId();
        dungeon = dc.tick(null, Direction.NONE);
        dungeon = dc.interact(mercenaryID);

        ItemResponse invis = TestHelpers.getInventoryResponseList(dungeon, "invisibility_potion").get(0);
        ItemResponse invince = TestHelpers.getInventoryResponseList(dungeon, "invincibility_potion").get(0);
        ItemResponse health = TestHelpers.getInventoryResponseList(dungeon, "health_potion").get(0);

        // zombie Random aka no change, spider circle no change, Mercenary Random
        dungeon = dc.tick(invis.getId(), Direction.NONE);
        List<Entity> entities = dc.getDungeon(dungeon.getDungeonId()).getEntities();
        Character zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        Character spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        Character mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveRandom.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        // Still moves with player
        assertEquals(MoveWith.class, mercenary.getStrategy().getClass());

        // zombie MoveAway, spider circle no change, Mercenary moveAway
        dungeon = dc.tick(invince.getId(), Direction.NONE);
        entities = dc.getDungeon(dungeon.getDungeonId()).getEntities();
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveAway.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        assertEquals(MoveWith.class, mercenary.getStrategy().getClass());

        // no change
        dungeon = dc.tick(health.getId(), Direction.NONE);
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        spider = (Character) entities.stream().filter(entity -> entity.getType().equals("spider"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveAway.class, zombie.getStrategy().getClass());
        assertEquals(Circle.class, spider.getStrategy().getClass());
        // Still moves with player
        assertEquals(MoveWith.class, mercenary.getStrategy().getClass());

        // potions wear off, go back to normal, potions last 15 moves
        for (int i = 0; i < 15; i++)
            dungeon = dc.tick(null, Direction.NONE);
        zombie = (Character) entities.stream().filter(entity -> entity.getType().equals("zombie_toast"))
                .collect(Collectors.toList()).get(0);
        mercenary = (Character) entities.stream().filter(entity -> entity.getType().equals("mercenary"))
                .collect(Collectors.toList()).get(0);

        assertEquals(MoveRandom.class, zombie.getStrategy().getClass());
        // Sceptre wears off
        assertEquals(MoveTowards.class, mercenary.getStrategy().getClass());

    }

    @Test
    public void testHealthPotionWorks() {
        DungeonManiaController dc = new DungeonManiaController();
        DungeonResponse dungeon = dc.newGame("Potions", "standard");
        dungeon = dc.tick(null, Direction.RIGHT);

        ItemResponse health = TestHelpers.getInventoryResponseList(dungeon, "health_potion").get(0);
        Player player = dc.getDungeon(dungeon.getDungeonId()).getPlayer();

        int HP = player.getHP();
        player.setHP(1);
        assertNotEquals(HP, player.getInitialHP());

        dungeon = dc.tick(health.getId(), Direction.NONE);
        dungeon = dc.saveGame(dungeon.getDungeonId());
        // try {
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        dungeon = dc.loadGame(dungeon.getDungeonId());
        player = dc.getDungeon(dungeon.getDungeonId()).getPlayer();
        assertEquals(player.getHP(), player.getInitialHP());
    }

}
