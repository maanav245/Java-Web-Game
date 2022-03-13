package dungeonmania.models.Goals;

import java.util.List;

import dungeonmania.models.Entity;

public abstract class LeafGoal implements GoalComponent {

    /**
     * @param g
     * @return boolean
     */
    public abstract boolean isComplete(List<Entity> entities);

    @Override
    public void addGoalComponent(GoalComponent g) {
    };

    public abstract String toString();

    public abstract String toJSONString();
}
