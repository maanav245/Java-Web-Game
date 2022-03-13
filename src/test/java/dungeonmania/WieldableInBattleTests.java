package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.Goals.GoalComponent;
import dungeonmania.models.MovingEntities.Mercenary;
import dungeonmania.models.StaticEntities.Bow;
import dungeonmania.models.StaticEntities.Shield;
import dungeonmania.models.StaticEntities.Wieldable;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class WieldableInBattleTests {

        @Test
        public void testBattleReducesWieldableItemDurability() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                // player moves UP to get sword
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                // check sword has been picked up by player

                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> swordE = player.getInventory().getIfContains("sword");
                // check that the player has a sword in inventory

                assertTrue(swordE.size() == 1);
                Wieldable sword = (Wieldable) swordE.get(0);
                int beforeBattleDurability = sword.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;

                // same as BattleTests, mercenary will lose without player's sword in play
                Entity losingEntity = Battle.startBattle(player, mercenary);
                assertTrue(losingEntity != null);
                assertTrue(losingEntity instanceof Mercenary);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                // check player's sword's durability is less than before battle OR is no longer
                // in players inventory
                swordE = player.getInventory().getIfContains("sword");
                if (swordE.size() == 1) {
                        sword = (Wieldable) swordE.get(0);
                }
                assertTrue(sword.getDurability() < beforeBattleDurability);

        }

        @Test
        public void testSwordDisappearsAfterEndedDurability() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("tenEnemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                // player moves UP to get sword
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                // check sword has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> swordE = player.getInventory().getIfContains("sword");
                // check that the player has a sword in inventory

                assertTrue(swordE.size() == 1);
                Wieldable sword = (Wieldable) swordE.get(0);
                int beforeBattleDurability = sword.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                player = (Player) playerE;
                player.setHP(10000);
                // 10 mercentaries just to the RIGHT of Player original starting position
                dc.tick(null, Direction.DOWN);
                // player to move towards 10 mercenaries to trigger 10 sequential battles if not
                // already done
                dungeon = dc.tick(null, Direction.RIGHT);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                // check player's sword is no longer in players inventory
                swordE = player.getInventory().getIfContains("sword");
                assertTrue(swordE.size() == 0);

        }

        @Test
        public void testSwordIncreasesAttackPower() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                // player moves UP to get sword
                dc.tick(null, Direction.UP);
                // check sword has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> swordE = player.getInventory().getIfContains("sword");
                // check that the player has a sword in inventory

                assertTrue(swordE.size() == 1);
                Wieldable sword = (Wieldable) swordE.get(0);
                int beforeBattleDurability = sword.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;
                // player wins battle if mercenary HP is 36 without sowrd, but will lose if 37+
                mercenary.setHP(37);
                // same as BattleTests, mercenary will lose without player's sword in play
                Entity losingEntity = Battle.startBattle(player, mercenary);
                assertTrue(losingEntity != null);

                // with sword increasing attack power by 1.5, player should still win despite
                // high mercenary HP
                assertTrue(losingEntity instanceof Mercenary);

                // check player's sword's durability is less than before battle OR is no longer
                // in players inventory
                swordE = player.getInventory().getIfContains("sword");
                if (swordE.size() == 1) {
                        sword = (Wieldable) swordE.get(0);
                }
                assertTrue(swordE.size() == 0 || sword.getDurability() < beforeBattleDurability);

        }

        @Test
        public void testBowDisappearsAfterEndedDurability() {
                // Is not save/load game safe, need changes to bows
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("tenEnemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                Bow b = new Bow("1", new Position(0, 0));
                Player player = currDungeon.getPlayer();
                player.addToInventory(b);

                // player moves UP to get sword
                dc.tick(null, Direction.UP);

                List<Entity> bowE = player.getInventory().getIfContains("bow");
                // check that the player has a sword in inventory

                assertTrue(bowE.size() == 1);
                Wieldable bow = (Wieldable) bowE.get(0);
                int beforeBattleDurability = bow.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                player = (Player) playerE;
                player.setHP(10000);
                // 10 mercentaries just to the RIGHT of Player original starting position
                dc.tick(null, Direction.DOWN);
                // player to move towards 10 mercenaries to trigger 10 sequential battles if not
                // already done
                DungeonResponse dungeon = dc.tick(null, Direction.RIGHT);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());

                // check player's sword is no longer in players inventory
                bowE = player.getInventory().getIfContains("bow");
                assertTrue(bowE.size() == 0);

        }

        @Test
        public void testBowIncreasesAttackPower() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                Bow b = new Bow("1", new Position(0, 0));
                Player player = currDungeon.getPlayer();
                player.addToInventory(b);
                // player moves UP to get sword
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                player = currDungeon.getPlayer();
                List<Entity> bowE = player.getInventory().getIfContains("bow");
                // check that the player has a sword in inventory

                assertTrue(bowE.size() == 1);
                Wieldable bow = (Wieldable) bowE.get(0);
                int beforeBattleDurability = bow.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;
                // player wins battle if mercenary HP is 40 (without bow) but would lose if 41
                // or higher
                mercenary.setHP(41);
                // same as BattleTests, mercenary will lose without player's sword in play
                Entity losingEntity = Battle.startBattle(player, mercenary);
                assertTrue(losingEntity != null);

                // with sword increasing attack power by 1.5, player should still win despite
                // high mercenary HP
                assertTrue(losingEntity instanceof Mercenary);

                // check player's sword's durability is less than before battle OR is no longer
                // in players inventory
                bowE = player.getInventory().getIfContains("bow");
                if (bowE.size() == 1) {
                        bow = (Wieldable) bowE.get(0);
                }
                assertTrue(bowE.size() == 0 || bow.getDurability() < beforeBattleDurability);

        }

        @Test
        public void testShieldDisappearsAfterEndedDurability() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("tenEnemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                Shield s = new Shield("1", new Position(0, 0));
                Player player = currDungeon.getPlayer();
                player.addToInventory(s);

                // player moves UP to get sword
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                List<Entity> shieldE = player.getInventory().getIfContains("shield");
                // check that the player has a sword in inventory

                assertTrue(shieldE.size() == 1);
                Wieldable shield = (Wieldable) shieldE.get(0);
                int beforeBattleDurability = shield.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                player = (Player) playerE;
                player.setHP(10000);
                // 10 mercentaries just to the RIGHT of Player original starting position
                dc.tick(null, Direction.DOWN);
                // player to move towards 10 mercenaries to trigger 10 sequential battles if not
                // already done
                dungeon = dc.tick(null, Direction.RIGHT);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                player = currDungeon.getPlayer();
                // check player's sword is no longer in players inventory
                shieldE = player.getInventory().getIfContains("shield");
                assertTrue(shieldE.size() == 0);

        }

        @Test
        public void testShieldIncreasesAttackPower() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("enemiesExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);
                Shield s = new Shield("1", new Position(0, 0));
                Player player = currDungeon.getPlayer();
                player.addToInventory(s);
                // player moves UP to get sword
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                player = currDungeon.getPlayer();

                List<Entity> shieldE = player.getInventory().getIfContains("shield");
                // check that the player has a sword in inventory

                assertTrue(shieldE.size() == 1);
                Wieldable shield = (Wieldable) shieldE.get(0);
                int beforeBattleDurability = shield.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;

                DoBattle mercenary = (DoBattle) mercenaryE;
                // player wins battle if mercenary HP is 40, loses if its 41
                mercenary.setHP(41);
                // same as BattleTests, mercenary will lose without player's sword in play
                Entity losingEntity = Battle.startBattle(player, mercenary);
                assertTrue(losingEntity != null);

                // with sword increasing attack power by 1.5, player should still win despite
                // high mercenary HP
                assertTrue(losingEntity instanceof Mercenary);

                // check player's sword's durability is less than before battle OR is no longer
                // in players inventory
                shieldE = player.getInventory().getIfContains("shield");
                if (shieldE.size() == 1) {
                        shield = (Wieldable) shieldE.get(0);
                }
                assertTrue(shieldE.size() == 0 || shield.getDurability() < beforeBattleDurability);

        }

        @Test
        public void testArmourDecreasesAttackImpactAsExpected() throws InterruptedException {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse creategame = dc.newGame("armourExit", "standard");
                String dungeonId = creategame.getDungeonId();
                dc.saveGame(dungeonId);

                Thread.sleep(600);
                // DungeonResponse exitgame = dc.loadGame(dungeonId);
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                // player moves UP to get armour
                DungeonResponse dungeon = dc.tick(null, Direction.UP);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());

                // check sword has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> armourE = player.getInventory().getIfContains("armour");
                // check that the player has a sword in inventory

                assertTrue(armourE.size() == 1);
                Wieldable armour = (Wieldable) armourE.get(0);
                int beforeBattleDurability = armour.getDurability();

                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;
                // player wins battle if mercenary HP is 40, without weildable, 41 and above it
                // loses
                mercenary.setHP(41);
                // same as BattleTests, mercenary will lose without player's armour in play
                Entity losingEntity = Battle.startBattle(player, mercenary);
                assertTrue(losingEntity != null);
                // with armour increasing attack power by 1.5, player should still win despite
                // high mercenary HP
                assertTrue(losingEntity instanceof Mercenary);

                // // check player's armour's durability is less than before battle OR is no
                // longer in players inventory
                armourE = player.getInventory().getIfContains("armour");
                if (armourE.size() == 1) {
                        armour = (Wieldable) armourE.get(0);
                }
                assertTrue(armourE.size() == 0 || armour.getDurability() < beforeBattleDurability);
        }

        @Test
        public void testIntegrationInvincibilityPotionInstantBattleWinDisappearsFromPlayerInventory() {
                DungeonManiaController dc = new DungeonManiaController();
                DungeonResponse exitgame = dc.newGame("invincibleExit", "standard");
                String dungeonId = exitgame.getDungeonId();
                Dungeon currDungeon = dc.getDungeon(dungeonId);

                // player moves UP to get invincibility_potion
                dc.tick(null, Direction.UP);
                // check invincibility_potion has been picked up by player
                Player player = (Player) currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("player")).collect(Collectors.toList())
                                .get(0);
                List<Entity> invincibility_potionE = player.getInventory().getIfContains("invincibility_potion");
                // check that the player has a invincibility_potion in inventory
                assertTrue(invincibility_potionE.size() == 1);
                String potionId = invincibility_potionE.get(0).getId();
                Entity playerE = currDungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                                .collect(Collectors.toList()).get(0);
                Entity mercenaryE = currDungeon.getEntities().stream()
                                .filter(entity -> entity.getType().equals("mercenary")).collect(Collectors.toList())
                                .get(0);
                player = (Player) playerE;
                DoBattle mercenary = (DoBattle) mercenaryE;

                // same as BattleTests, mercenary will lose without player's
                // invincibility_potion in play
                int beforeBattlePlayerHP = player.getHP();
                // give mercenary a crazy HP level before battle
                mercenary.setHP(10000000);

                // battle entered by tick
                DungeonResponse dungeon = dc.tick(potionId, Direction.RIGHT);

                dungeon = dc.saveGame(dungeon.getDungeonId());
                // try {
                //         Thread.sleep(500);
                // } catch (InterruptedException e) {
                //         // TODO Auto-generated catch block
                //         e.printStackTrace();
                // }
                dungeon = dc.loadGame(dungeon.getDungeonId());
                currDungeon = dc.getDungeon(dungeon.getDungeonId());
                player = currDungeon.getPlayer();

                // player HP is identical to what player started with before the battle & potion
                // is used
                assertEquals(beforeBattlePlayerHP, player.getHP());
                assertEquals(player.getInventory().getCount("invincibility_potion"), 0);

        }

}
