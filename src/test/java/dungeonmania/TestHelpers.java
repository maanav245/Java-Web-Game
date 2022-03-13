package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public class TestHelpers {
    public static <T extends Comparable<? super T>> void assertListAreEqualIgnoringOrder(List<T> a, List<T> b) {
        Collections.sort(a);
        Collections.sort(b);
        assertArrayEquals(a.toArray(), b.toArray());
    }

    public static List<EntityResponse> getEntityResponseList(DungeonResponse dungeon, String entityName) {
        return dungeon.getEntities().stream().filter(entity -> entity.getType().equals(entityName))
                .collect(Collectors.toList());
    }

    public static List<Entity> getEntityList(Dungeon dungeon, String entityName) {
        return dungeon.getEntities().stream().filter(entity -> entity.getType().equals(entityName))
                .collect(Collectors.toList());
    }

    public static Position getPlayerPosition(DungeonResponse dungeon) {
        return dungeon.getEntities().stream().filter(entity -> entity.getType().equals("player"))
                .collect(Collectors.toList()).get(0).getPosition();
    }

    public static List<ItemResponse> getInventoryResponseList(DungeonResponse dungeon, String entityName) {
        return dungeon.getInventory().stream().filter(entity -> entity.getType().equals(entityName))
                .collect(Collectors.toList());
    }

    public static EntityResponse getEntityFromID(DungeonResponse dungeon, String id) {
        return dungeon.getEntities().stream().filter(entity -> entity.getId().equals(id)).collect(Collectors.toList())
                .get(0);
    }
}
