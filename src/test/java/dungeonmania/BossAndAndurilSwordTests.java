package dungeonmania;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.Battle.Battle;
import dungeonmania.models.Battle.DoBattle;
import dungeonmania.models.Modes.GameMode;
import dungeonmania.models.Modes.GameModeFactory;
import dungeonmania.models.MovingEntities.Assassin;
import dungeonmania.models.MovingEntities.Hydra;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class BossAndAndurilSwordTests {
    @Test
    public void testHydraOnlyLoadsAndSpawnsDuringHardMode() {
        // even if there is a 'hydra' in the maze, it will not be loaded in Standard or
        // PeacefulMode
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("hydraAndurilSword", "standard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // move left to avoid all enemies
        dc.tick(null, Direction.LEFT);
        List<Entity> hydras = TestHelpers.getEntityList(dr1, "hydra");
        assertEquals(hydras.size(), 0);

    }

    @Test
    public void testHydraRespawnsHardMode50Ticks() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("hydraAndurilSword", "hard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // move left to avoid all enemies
        dc.tick(null, Direction.LEFT);
        List<Entity> hydras = TestHelpers.getEntityList(dr1, "hydra");
        // starts out with one hydra
        assertEquals(hydras.size(), 1);
        // move in a left to avoid battle for 50 ticks
        for (int i = 0; i < 50; i++) {
            dc.tick(null, Direction.LEFT);
        }
        hydras = TestHelpers.getEntityList(dr1, "hydra");
        assertEquals(hydras.size(), 2);

    }

    @Test
    public void testAssassinOccaisionallySpawnsInsteadOfRegularMercenary() {
        DungeonManiaController dc = new DungeonManiaController();
        // repeat spawning test 10 times on new dungeons
        
        String id = dc.newGame("hydraAssassinAndurilSword", "standard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        GameModeFactory gameModeF = dr1.getGameModeFactory();
        GameMode gameM = (GameMode) gameModeF;
        // set assassin spawn random
        long assassinSpawnSeed = System.currentTimeMillis();
        gameM.setAssassinSpawnChance(assassinSpawnSeed);

        Random parallel = new Random(assassinSpawnSeed);
        int newInt = parallel.nextInt(11);
        
        Player player = dr1.getPlayer();
 

        // there is an assassin to the right of the player
        List<Entity> assassins = TestHelpers.getEntityList(dr1, "assassin");
        assertEquals(assassins.size(), 1);
        int expectedInitAssassinSize = 1;
        dc.tick(null, Direction.UP);
        // player stays in the initial position for 20 ticks with high HP in order to win battles
        for (int j = 0; j < 20; j++) {
            // give player high HP to survive in order to test spawns
            player.setHP(5000);
            dc.tick(null, Direction.DOWN);
            if (player.getHP() < 5000) {
                // battle has occurred and mercenary/ assassin is expected to die
                assassins = TestHelpers.getEntityList(dr1, "assassin");
                assertEquals(assassins.size(), 0);
                expectedInitAssassinSize = 0;
            }
            dr1 = dc.getDungeon(id);
        }

        // after 4 more ticks, ordinarily a mercenary will spawn
        // check that this 'random' spawn is actually an assassin with expected seed
        assassins = TestHelpers.getEntityList(dr1, "assassin");
        List<Entity> mercenaries = TestHelpers.getEntityList(dr1, "mercenary");
        if (newInt < 3) {
        // new assassin expected to spawn
        assertEquals(assassins.size(), 1 + expectedInitAssassinSize);
        assertEquals(mercenaries.size(), 0);
        } else {
        // mercenary spawns, not assassin
        assertEquals(assassins.size(), expectedInitAssassinSize);
        assertEquals(mercenaries.size(), 1);}

        
    }

    @Test
    public void testAndurilSwordCausesTripleDamageAgainstBosses() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("hydraAssassinAndurilSword", "standard").getDungeonId();
        Dungeon dr1 = dc.getDungeon(id);
        // move up to collect AndurilSword
        dc.tick(null, Direction.UP);
        // there is an assassin to the right of the player
        List<Entity> assassins = TestHelpers.getEntityList(dr1, "assassin");
        assertEquals(assassins.size(), 1);
        Entity assassin1 = assassins.get(0);
        Assassin ac = (Assassin) assassin1;
        // set assassip HP to 3 times what a single player in standard can beat equiped
        // with a sword
        // equiped with a regular sword the player would lose
        ac.setHP(45);
        // start Battle with player
        Player player = (Player) TestHelpers.getEntityList(dr1, "player").get(0);
        Entity losingEntity = Battle.startBattle(player, (DoBattle) ac);
        // loser should be the assassin
        assertTrue(losingEntity instanceof Assassin);

    }

    @Test
    public void testAttacksAgainstHydraCanIncreaseItsHP() {
        DungeonManiaController dc = new DungeonManiaController();

        // a series of 10 tests to check pseudo random behaviour
        // set Hydra to be crazy high HP so result will be determined by 1 battle
        for (Integer i = 0; i < 10; i++) {
            String id = dc.newGame("hydraAndurilSword", "standard").getDungeonId();
            Dungeon currDungeon = dc.getDungeon(id);

            // set new random seed for hydra
            Player player = currDungeon.getPlayer();
            long seed = System.currentTimeMillis();
            // create new hydra in same position as one in json map
            Hydra newHydra = new Hydra("hydra" + i.toString(), new Position(3, 3));
            newHydra.setNewRandomSeed(seed);
            Random parallel = new Random(seed);
            int newInt = parallel.nextInt();
            // set crazy high stats for player and hydra so will be over in 1 round
            player.setHP(200);
            newHydra.setHP(100);
            // start a battle
            Entity losingEntity = Battle.startBattle(player, (DoBattle) newHydra);
            // even 'random' result int will decrease Hydra HP -> Hydra loss
            if (newInt % 2 == 0) {
                assertTrue(losingEntity instanceof Hydra);
                // odd 'random' result int will increase Hydra HP -> Player loss
            } else {
                assertTrue(losingEntity instanceof Player);
            }
        }

    }

    @Test
    public void testAndurilSwordMeansHydraCannotIncreaseHPFromAttack() {
        DungeonManiaController dc = new DungeonManiaController();
        // odd 'random' int will decrease HP

        // event 'randdom' int will decrease H
        // a series of 10 tests to check pseudo random behaviour
        // set Hydra to be crazy high HP so result will be determined by 1 battle
        for (Integer i = 0; i < 10; i++) {
            String id = dc.newGame("hydraAndurilSword", "standard").getDungeonId();
            Dungeon currDungeon = dc.getDungeon(id);
            // move up to collect AndurilSword
            dc.tick(null, Direction.UP);

            // set new random seed for hydra
            Player player = currDungeon.getPlayer();
            long seed = System.currentTimeMillis();
            // create new hydra in same position as one in json map
            Hydra newHydra = new Hydra("hydra" + i.toString(), new Position(3, 3));
            newHydra.setNewRandomSeed(seed);
            Random parallel = new Random(seed);
            int newInt = parallel.nextInt();
            // set crazy high stats for player and hydra so will be over in 1 round
            player.setHP(200);
            newHydra.setHP(100);
            // start a battle
            Entity losingEntity = Battle.startBattle(player, (DoBattle) newHydra);
            // even 'random' result int will decrease Hydra HP -> Hydra loss
            if (newInt % 2 == 0) {
                assertTrue(losingEntity instanceof Hydra);
                // odd 'random' result int will still decrease Hydra HP -> Hydra loss
            } else {
                assertTrue(losingEntity instanceof Hydra);
            }
        }
    }

    @Test
    public void HydraCannotSpawnNoPositions() {
        DungeonManiaController dc = new DungeonManiaController();

        for (Integer i = 0; i < 10; i++) {
            String id = dc.newGame("spiderBlocked", "hard").getDungeonId();
            Dungeon currDungeon = dc.getDungeon(id);
            // move left to avoid all enemies
            dc.tick(null, Direction.NONE);
            List<Entity> hydras = TestHelpers.getEntityList(currDungeon, "hydra");
            // starts out with no hydra
            assertEquals(hydras.size(), 0);
            // move in a left to avoid battle for 50 ticks
            for (int j = 0; j < 50; j++) {
                dc.tick(null, Direction.LEFT);
            }
            hydras = TestHelpers.getEntityList(currDungeon, "hydra");
            assertEquals(hydras.size(), 1);

        }

    }

    @Test
    public void SpiderCannotSpawnNoPositions() {
        DungeonManiaController dc = new DungeonManiaController();
        String id = dc.newGame("spiderBlocked", "hard").getDungeonId();
        Dungeon currDungeon = dc.getDungeon(id);
        // move left to avoid all enemies
        dc.tick(null, Direction.NONE);
        List<Entity> spiders = TestHelpers.getEntityList(currDungeon, "spider");
        // starts out with no hydra
        assertEquals(spiders.size(), 0);
        // move in a left to avoid battle for 50 ticks
        for (int j = 0; j < 50; j++) {
            dc.tick(null, Direction.LEFT);
        }
        spiders = TestHelpers.getEntityList(currDungeon, "spider");
        assertEquals(spiders.size(), 0);
    }

}
