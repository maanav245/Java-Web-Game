package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dungeonmania.util.Position;
import dungeonmania.exceptions.*;
import dungeonmania.models.*;
import dungeonmania.models.StaticEntities.Door;
import dungeonmania.models.StaticEntities.Treasure;

public class MVPv1PlayerItemTests {

    @Test
    public void testAddItemToInventory() {
        Player p = new Player("player1", new Position(0, 0));
        Treasure t1 = new Treasure("coin", new Position(0, 0));
        Door door = new Door("door", new Position(0, 0), 0);
        assertDoesNotThrow(() -> p.addToInventory(t1));
        assertThrows(InvalidActionException.class, () -> p.addToInventory(door));
    }

    @Test
    public void testCountInventory() {
        Player p = new Player("player1", new Position(4, 6));
        Treasure t1 = new Treasure("coin", new Position(0, 0));
        Treasure t2 = new Treasure("gold", new Position(0, 1));
        Treasure t3 = new Treasure("goblet", new Position(0, 2));
        List<String> itemsList = Arrays.asList("coin", "goblet", "gold");
        assertDoesNotThrow(() -> p.addToInventory(t1));
        assertDoesNotThrow(() -> p.addToInventory(t2));
        assertDoesNotThrow(() -> p.addToInventory(t3));
        // TO DO: WRITE A FUNCTION THAT GETS THE COUNT OF AN ITEM THIS COUNT WILL TEST
        assertTrue(p.getInventory().getInventoryResponse().size() == 3);
        assertTrue(p.getInventory().getInventoryResponse().stream().allMatch(item -> itemsList.contains(item.getId())));
    }

    @Test
    public void testPlayerGetInventory() {
        Player p = new Player("player1", new Position(0, 1));
        Treasure t1 = new Treasure("coin", new Position(0, 0));
        Treasure t2 = new Treasure("gold", new Position(0, 1));
        List<String> itemList = new ArrayList<String>();
        itemList.add("coin");
        itemList.add("gold");
        assertDoesNotThrow(() -> p.addToInventory(t1));
        assertDoesNotThrow(() -> p.addToInventory(t2));
        assertTrue(p.getInventory().getInventoryResponse().stream().allMatch(item -> itemList.contains(item.getId())));
    }

}
