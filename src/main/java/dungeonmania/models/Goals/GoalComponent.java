package dungeonmania.models.Goals;

import java.util.List;

import dungeonmania.models.Entity;

public interface GoalComponent {
    public abstract boolean isComplete(List<Entity> entities);

    public abstract void addGoalComponent(GoalComponent g);

    public abstract String toString();
}
