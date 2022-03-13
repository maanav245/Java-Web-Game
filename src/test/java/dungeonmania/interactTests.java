package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;

public class interactTests {

    @Test
    public void invalidEntityId() {
        DungeonManiaController dc = new DungeonManiaController();
        dc.newGame("3x3maze", "standard");
        assertThrows(IllegalArgumentException.class, () -> dc.interact("non-existent"));
    }

}
