package dungeonmania.models.Movement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.StaticEntities.Blockable;
import dungeonmania.models.StaticEntities.Boulder;
import dungeonmania.models.StaticEntities.SwampTile;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MoveTowards extends MovementStrategy {

    /**
     * Generates a hashmap containing all the positions which the mercenary has
     * trouble going through
     * 
     * @param ents
     * @param moving
     * @return HashMap<Position, Integer>
     */
    public HashMap<Position, Integer> generateMapMerc(List<Entity> ents, Entity moving) {
        HashMap<Position, Integer> block_list = new HashMap<Position, Integer>();

        for (Entity ent : ents) {
            if (block_list.containsKey(new Position(ent.getPosition().getX(), ent.getPosition().getY()))) {
                continue;
            } else {
                if (ent instanceof Boulder || ent instanceof Blockable && ((Blockable) ent).isBlocking(moving, ents)) {
                    block_list.put(new Position(ent.getPosition().getX(), ent.getPosition().getY()), -1);

                } else if (ent instanceof SwampTile) {
                    block_list.put(new Position(ent.getPosition().getX(), ent.getPosition().getY()),
                            ((SwampTile) ent).getMovementFactor());
                }

            }
        }

        return block_list;

    }

    /**
     * calculates the position with the minimum distance in the distance array
     * 
     * @param dist
     * @param settled
     * @return Position
     */
    public Position minDistance(HashMap<Position, Double> dist, ArrayList<Position> settled) {
        // Initialize min value
        double min = Double.MAX_VALUE;
        Position min_index = null;

        for (Position pos : dist.keySet()) {
            if (!settled.contains(pos) && dist.get(pos) <= min) {
                min_index = pos;
                min = dist.get(pos);
            }
        }

        return min_index;
    }

    /**
     * Djkistra algo using a 'prev' array in order to back track to find the correct
     * position
     * 
     * @param dungeon
     * @param entity
     * @return Direction
     */
    @Override
    public Direction getDirection(Dungeon dungeon, Entity entity) {
        System.out.println("");
        HashMap<Position, Integer> enity_map = generateMapMerc(dungeon.getEntities(), entity);
        System.out.println(enity_map);
        HashMap<Position, Double> dist = new HashMap<Position, Double>();
        HashMap<Position, Position> prev = new HashMap<Position, Position>();
        Position currPosition = entity.getPosition();
        Position charac_pos = dungeon.getPlayer().getPosition();
        int min_x = Integer.MAX_VALUE;
        int max_x = dungeon.getWidth();
        for (Entity ent_min : dungeon.getEntities()) {
            if (ent_min.getPosition().getX() < min_x) {
                min_x = ent_min.getPosition().getX();
            }
            if (ent_min.getPosition().getX() > max_x) {
                max_x = ent_min.getPosition().getX();
            }
        }
        System.out.println("max_x: " + max_x);
        System.out.println("mix_x: " + min_x);

        int min_y = Integer.MAX_VALUE;
        int max_y = dungeon.getHeight();
        for (Entity ent_min : dungeon.getEntities()) {
            if (ent_min.getPosition().getY() < min_y) {
                min_y = ent_min.getPosition().getY();
            }
            if (ent_min.getPosition().getY() > max_y) {
                max_y = ent_min.getPosition().getY();
            }

        }
        System.out.println("max_y: " + max_y);
        System.out.println("min_y: " + min_y);
        for (int i = min_x; i < max_x + 1; i++) {
            for (int j = min_y; j < max_y + 1; j++) {
                dist.put(new Position(i, j), Double.MAX_VALUE);
                prev.put(new Position(i, j), null);
            }
        }
        dist.replace(new Position(currPosition.getX(), currPosition.getY()), Double.MAX_VALUE, 0.0);

        ArrayList<Position> settled = new ArrayList<Position>();

        while (settled.size() != dist.size()) {

            Position min_pos = minDistance(dist, settled);
            if (min_pos == null) {

                break;

            }
            for (Position adj_pos : min_pos.getAdjacentPositionsSides()) {
                // System.out.println(this.enity_map.containsKey(adj_pos));
                // System.out.println(dist.get(min_pos));
                // System.out.println(dist.get(adj_pos));
                if (dist.containsKey(adj_pos) && dist.get(min_pos) + 1.0 < dist.get(adj_pos)
                        && !enity_map.containsKey(adj_pos)) {
                    dist.replace(adj_pos, dist.get(min_pos) + 1.0);
                    prev.replace(adj_pos, min_pos);
                } else if (enity_map.containsKey(adj_pos) && !enity_map.get(adj_pos).equals(-1)
                        && dist.containsKey(adj_pos)
                        && dist.get(min_pos) + (enity_map.get(adj_pos)) < dist.get(adj_pos)) {

                    dist.replace(adj_pos, dist.get(min_pos) + (enity_map.get(adj_pos)));
                    prev.replace(adj_pos, min_pos);
                } else if (enity_map.containsKey(adj_pos) && (enity_map.get(adj_pos) > 0) && dist.containsKey(adj_pos)
                        && dist.get(min_pos) + (enity_map.get(adj_pos)) < dist.get(adj_pos)) {
                    dist.replace(adj_pos, dist.get(min_pos) + (enity_map.get(adj_pos)));
                    prev.replace(adj_pos, min_pos);
                }
            }
            settled.add(min_pos);
        }

        Position movement = charac_pos;

        Position final_pos = movement;

        while (movement != null && !movement.equals(new Position(currPosition.getX(), currPosition.getY()))) {

            final_pos = movement;
            movement = prev.get(movement);

        }
        System.out.println("Merc final position is:" + currPosition);
        System.out.println("Merc should be at:" + final_pos);
        System.out.println("CHarac real position is:" + charac_pos);
        Position diffPos = Position.calculatePositionBetween(currPosition, final_pos);
        if (diffPos.equals(new Position(1, 0))) {
            return Direction.RIGHT;
        } else if (diffPos.equals(new Position(-1, 0))) {
            return Direction.LEFT;
        } else if (diffPos.equals(new Position(0, 1))) {
            return Direction.DOWN;
        } else if (diffPos.equals(new Position(0, -1))) {
            return Direction.UP;
        }
        return Direction.NONE;

    }

}
