package dungeonmania.models.Goals;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.models.Entity;

public class BouldersGoal extends LeafGoal {
    private int bn;
    private int swn;

    @Override
    public boolean isComplete(List<Entity> entities) {
        // get the position of every 'boulder'
        List<Entity> boulderEntities = entities.stream().filter(e -> e.getType() == "boulder")
                .collect(Collectors.toList());

        // get the position of every 'floor_switch'
        List<Entity> floorSwitchEntities = entities.stream().filter(e -> e.getType() == "switch")
                .collect(Collectors.toList());
        // compare position of the player with the exit
        this.swn = floorSwitchEntities.size();

        this.bn = boulderEntities.size();
        // check every floor switch has a boulder in the same XYPosition
        for (Entity floorSwitch : floorSwitchEntities) {
            // if there is a floorSwitch with no boulder in same XYPosition, then goal is
            // not complete
            List<Entity> placedBoulder = boulderEntities.stream().filter(b -> b.sameXYPosition(floorSwitch))
                    .collect(Collectors.toList());
            this.swn -= placedBoulder.size();
            this.bn -= placedBoulder.size();
        }

        if (this.swn == 0) {
            return true;
        } else {
            return false;
        }
    };

    @Override
    public String toString() {
        if (this.swn == 0) {
            return "";
        } else if (this.swn > 1 && this.bn > 1) {
            return ":boulder(" + bn + ")/:switch(" + swn + ")";
        } else if (this.bn > 1) {
            return ":boulder(" + bn + ")/:switch";
        } else {
            return ":boulder/:switch";
        }
    }

    @Override
    public String toJSONString() {
        return "boulders";
    }
}
