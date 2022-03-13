package dungeonmania.models.Goals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.models.Entity;

public class GoalFactory {
    /*
     * Converts goals to JSON file
     * 
     * @return JSONObject with formated goals
     */
    public JSONObject toJSON(GoalComponent goalCondition) {
        JSONObject JSONgoals = new JSONObject();
        if (goalCondition instanceof CompositeGoals) {
            // add logic
            CompositeGoals cGoals = (CompositeGoals) goalCondition;
            JSONgoals.put("goal", cGoals.getBooleanLogic());
            // add subgoals
            JSONArray subGoalArray = new JSONArray();
            for (GoalComponent g : cGoals.getLeafGoals()) {
                JSONObject leaf = new JSONObject();
                if (g instanceof CompositeGoals) {
                    // in the case where there are nested goals
                    JSONObject goalValue = toJSON(g);
                    leaf.put("goal", goalValue);
                } else {
                    // in the case it is a normal node
                    String goalValue = ((LeafGoal) g).toJSONString();
                    leaf.put("goal", goalValue);
                }
                subGoalArray.put(leaf);
            }
            // subgoals put in goalNest
            JSONgoals.put("subgoals", subGoalArray);
        } else {
            JSONgoals.put("goal", ((LeafGoal) goalCondition).toJSONString());
        }
        return JSONgoals;
    }

    /*
     * Converts from JSONObject to goal components which can be read.
     * 
     * @return GoalComponent which is a tree of subgoals
     */

    public GoalComponent fromJSON(JSONObject goals, List<Entity> entities) {
        GoalComponent compositeGoals;
        String goalSting = goals.getString("goal");

        if (goalSting.equals("AND")) {
            compositeGoals = new AndCompositeGoal();
            JSONArray subgoals = goals.getJSONArray("subgoals");
            subgoals.forEach(g -> {
                generateLeafGoal(compositeGoals, ((JSONObject) g));
            });

        } else if (goalSting.equals("OR")) {
            compositeGoals = new OrCompositeGoal();
            JSONArray subgoals = goals.getJSONArray("subgoals");
            subgoals.forEach(g -> {
                generateLeafGoal(compositeGoals, ((JSONObject) g));
            });
        } else if (goalSting.equals("treasure")) {
            compositeGoals = new TreasureGoal();
        } else if (goalSting.equals("exit")) {
            compositeGoals = new ExitGoal();
        } else if (goalSting.equals("boulders")) {
            compositeGoals = new BouldersGoal();
        } else {
            compositeGoals = new EnemySpawnersGoal();
        }
        return compositeGoals;
    }

    public GoalComponent fromJSON(JSONObject goals) {
        GoalComponent compositeGoals;
        String goalSting = goals.getString("goal");

        if (goalSting.equals("AND")) {
            compositeGoals = new AndCompositeGoal();
            JSONArray subgoals = goals.getJSONArray("subgoals");
            subgoals.forEach(g -> {
                generateLeafGoal(compositeGoals, ((JSONObject) g));
            });

        } else if (goalSting.equals("OR")) {
            compositeGoals = new OrCompositeGoal();
            JSONArray subgoals = goals.getJSONArray("subgoals");
            subgoals.forEach(g -> {
                generateLeafGoal(compositeGoals, ((JSONObject) g));
            });
        } else if (goalSting.equals("treasure")) {
            compositeGoals = new TreasureGoal();
        } else if (goalSting.equals("exit")) {
            compositeGoals = new ExitGoal();
        } else if (goalSting.equals("boulders")) {
            compositeGoals = new BouldersGoal();
        } else {
            compositeGoals = new EnemySpawnersGoal();
        }
        return compositeGoals;
    }

    /*
     * generates leaf or tree of goals based on parent goal
     * 
     * @return GoalComponent which the parent of a tree of subgoals
     */

    private static GoalComponent generateLeafGoal(GoalComponent goal, JSONObject o) {
        if (o.getString("goal").equals("AND")) {
            AndCompositeGoal andGoal = new AndCompositeGoal();
            goal.addGoalComponent(andGoal);
            JSONArray subgoals = o.getJSONArray("subgoals");
            subgoals.forEach(g -> {
                generateLeafGoal(andGoal, ((JSONObject) g));
            });

        } else if (o.getString("goal").equals("OR")) {
            OrCompositeGoal orGoal = new OrCompositeGoal();
            JSONArray subgoals = o.getJSONArray("subgoals");
            goal.addGoalComponent(orGoal);
            subgoals.forEach(g -> {
                generateLeafGoal(orGoal, ((JSONObject) g));
            });

        } else if (o.getString("goal").equals("treasure")) {
            TreasureGoal tgoal = new TreasureGoal();
            goal.addGoalComponent(tgoal);

        } else if (o.getString("goal").equals("exit")) {
            ExitGoal egoal = new ExitGoal();
            goal.addGoalComponent(egoal);
        } else if (o.getString("goal").equals("boulders")) {
            BouldersGoal bgoal = new BouldersGoal();
            goal.addGoalComponent(bgoal);
        } else {
            EnemySpawnersGoal enemygoal = new EnemySpawnersGoal();
            goal.addGoalComponent(enemygoal);
        }
        return goal;
    }
}
