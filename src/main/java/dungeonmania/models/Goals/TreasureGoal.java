package dungeonmania.models.Goals;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Entity;

public class TreasureGoal extends LeafGoal {
    private int n;

    @Override
    public boolean isComplete(List<Entity> entities) {
        // get the position of all 'treasure'
        List<Entity> treasureEntities = entities.stream().filter(e -> e.getType().equals("treasure"))
                .collect(Collectors.toList());
        this.n = treasureEntities.size();
        // if treasure is still on map, then it hasn't been 'picked up' by the player
        return treasureEntities.size() == 0 ? true : false;
    }

    @Override
    public String toString() {
        // get number of treasure that still need to be collected
        if (n == 0) {
            return "";
        } else if (n > 1) {
            return ":treasure(" + n + ")";
        } else {
            return ":treasure";
        }
    }

    @Override
    public String toJSONString() {
        return "treasure";
    }
}
