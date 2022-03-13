package dungeonmania;

import dungeonmania.util.Position;
import dungeonmania.util.Direction;
import dungeonmania.models.Dungeon;
import dungeonmania.models.Entity;
import dungeonmania.models.Player;
import dungeonmania.models.StaticEntities.Exit;
import dungeonmania.models.StaticEntities.Wall;
import dungeonmania.models.Goals.GoalFactory;
import dungeonmania.models.Goals.GoalComponent;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class DungeonBuilder {
    
    private Dungeon dungeon;
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private String gameMode;

    private int size = 50;
    private Random randomIndex = new Random();
    private boolean[][] maze = new boolean[size][size];

    /**
     * Helper function of wallNeighbouts
     * @param pos
     * @param direction
     * @param distance
     * @return translated position
     */
    private Position translate(Position pos, Direction direction, int distance) {
        int i = 0;
        Position temp = pos;
        while (i < distance) {
            temp = temp.translateBy(direction);
            i += 1;
        }
        return temp;
    }


    /**
     * Get all neighbours, not on boundary, wall or empty
     * @param x
     * @param y
     * @return list of positions to generate dungeoen
     */
    private List<Position> allNeighbours(int x, int y, int distance) {
        List<Position> options = new ArrayList<>();
        Position start = new Position(x, y);
        // Extreme cases to handle boundaries
        // TODO: Change hardcoding
        // if (x <= distance && x > 0) {
        //     options.add(translate(start, Direction.RIGHT, distance));
        // }
        // if (x >= size - distance && x != size - 1) {
        //     options.add(translate(start, Direction.LEFT, distance));
        // }
        // if (y <= distance && y > 0) {
        //     options.add(translate(start, Direction.DOWN, distance));
        // }
        // if (y >= size - distance && y != size - 1) {
        //     options.add(translate(start, Direction.UP, distance));
        // }
        // if (x > distance && x < size - distance - 1) {
        //     options.add(translate(start, Direction.RIGHT, distance));
        //     options.add(translate(start, Direction.LEFT, distance));
        // }
        // if (y > distance && y < size - distance - 1) {
        //     options.add(translate(start, Direction.DOWN, distance));
        //     options.add(translate(start, Direction.UP, distance));
        // }
        if (x < distance) options.add(translate(start, Direction.RIGHT, distance));
        else if (x >= size - distance - 1)  options.add(translate(start, Direction.LEFT, distance));
        else {
            options.add(translate(start, Direction.LEFT, distance));
            options.add(translate(start, Direction.RIGHT, distance));
        }

        if (y < distance) options.add(translate(start, Direction.DOWN, distance));
        else if (y >= size - distance - 1)  options.add(translate(start, Direction.UP, distance));
        else {
            options.add(translate(start, Direction.UP, distance));
            options.add(translate(start, Direction.DOWN, distance));
        }

        return options;
    }


    /**
     * Get's wall neighbours
     * @param x
     * @param y
     * @return list of positions to generate dungeoen
     */
    private List<Position> wallNeighbours(int x, int y, int distance, boolean[][] maze) {
        List<Position> options = new ArrayList<>();
        for(Position op: allNeighbours(x, y, distance)){
            if (!maze[op.getX()][op.getY()]) options.add(op); 
        }
        return options;
    }


    /**
     * Get empty neighbours
     * @param x
     * @param y
     * @return list of positions to generate dungeoen
     */
    private List<Position> emptyNeighbours(int x, int y, int distance, boolean[][] maze) {
        List<Position> options = new ArrayList<>();
        // Check if positions are not walls
        for(Position op: allNeighbours(x, y, distance)){
            if (maze[op.getX()][op.getY()]) options.add(op); 
        }
        return options;
    }

    private Position getInBetween(Position a, Position b) {
        int xMid = (a.getX() + b.getX()) / 2;
        int yMid = (a.getY() + b.getY()) / 2;
        return new Position(xMid, yMid);
    }

    private boolean allVisited(boolean[][] maze, boolean xEven, boolean yEven) {
        int xRemainder = 1;
        int yRemainder = 1;
        if (xEven) {
            xRemainder = 0;
        }
        if (yEven) {
            yRemainder = 0;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i % 2 == xRemainder && j % 2 == yRemainder) {
                    if (!maze[i][j]) {
                        return false;
                    }

                }
            }
        }
        return true;
    }

    /**
     * Generate map (assumes map size to be always size x size and has no sub-boundaries)
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @param gameMode
     * @return dungeon
     */
    public DungeonBuilder(int xStart, int yStart, int xEnd, int yEnd, String gameMode) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.gameMode = gameMode;
    }

    private DungeonBuilder createDungeon() {
        this.dungeon = new Dungeon(UUID.randomUUID().toString(), "generated");
        this.dungeon.setWidth(size);
        this.dungeon.setHeight(size);
        this.dungeon.setEntities(new ArrayList<Entity>());
        return this;
        
    }
    private DungeonBuilder addExit() {
        Entity exit = new Exit(UUID.randomUUID().toString(), new Position(xEnd, yEnd));
        dungeon.addEntity(exit);
        return this;
    }

    private DungeonBuilder addPlayer() {
        Player player = new Player(UUID.randomUUID().toString(), new Position(xStart, yStart));
        player.setDungeon(dungeon);
        dungeon.addEntity(player);
        dungeon.setEntryPosition(player.getPosition());
        dungeon.setPlayer();
        return this;
    }

    private DungeonBuilder setGameMode() {
        dungeon.setGameModeFactory(LoadAndSaveDungeon.returnGameMode(gameMode, dungeon));
        this.dungeon = LoadAndSaveDungeon.returnGameMode(gameMode, dungeon).loadGame();
        return this;
    }
   
    private DungeonBuilder setExitGoal() {
        JSONObject goal = new JSONObject();
        goal.put("goal", "exit");
        GoalFactory goalsFactory = new GoalFactory();
        GoalComponent goals = goalsFactory.fromJSON(goal);
        dungeon.setGoalComponent(goals);
        return this;
    }

    private DungeonBuilder generateWallsMaze() {
        // Initialise 2d array
        // false representing a wall and true representing empty space
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = false;
            }
        }
        // Generate 2D Maze
        // maze[start] = empty
        maze[xStart][yStart] = true;
        // let options be a list of positions
        // add to options all neighbours of 'start' not on boundary that are of distance 2 away and are walls
        Set<Position> options = new HashSet<>(wallNeighbours(xStart, yStart, 2, maze));
        int random;
        List<Position> neighbours;
        // List<Position> nextVisited = new ArrayList<>();
        Position next;
        Position neighbour;
        Position inBetween;
        //  while options is not empty:
        while (!options.isEmpty()) {
            // let next = remove random from options
            random = randomIndex.nextInt(options.size());
            List<Position> arr = new ArrayList<>(options);
            next = arr.get(random);
            // let neighbours = each neighbour of distance 2 from next not on boundary that are empt
            neighbours = emptyNeighbours(next.getX(), next.getY(), 2, maze);
            // if neighbours is not empty:
            if (neighbours.size() != 0) {
                // let neighbour = random from neighbours
                random = randomIndex.nextInt(neighbours.size());
                List<Position> arr1 = new ArrayList<>(neighbours);
                neighbour = arr1.get(random);
                // maze[ next ] = empty (i.e. true)
                maze[next.getX()][next.getY()] = true;
                inBetween = getInBetween(next, neighbour);
                // maze[ position inbetween next and neighbour ] = empty (i.e. true)
                maze[inBetween.getX()][inBetween.getY()] = true;
                // maze[ neighbour ] = empty (i.e. true)
                maze[neighbour.getX()][neighbour.getY()] = true;
            }
            // add to options all neighbours of 'next' not on boundary that are of distance 2 away and are walls
            options.remove(next);
            // nextVisited.add(next);
            neighbours = wallNeighbours(next.getX(), next.getY(), 2, maze);
            options.addAll(neighbours);
            /*
            if (options.size() >= 30) {
                break;
            }*/
        }
        // at the end there is still a case where our end position isn't connected to the map
        // we don't necessarily need this, you can just keep randomly generating maps (was original intention)
        // but this will make it consistently have a pathway between the two.
        // if maze[end] is a wall:
        // Ensure exit is accessable
        if (!maze[xEnd][yEnd]) {
            //  maze[end] = empty
            maze[xEnd][yEnd] = true;
            //  let neighbours = neighbours not on boundary of distance 1 from maze[end]
            // TODO: is neighours in this case supposed to be empty or Wall???
            neighbours = allNeighbours(xEnd, yEnd, 1);
            // if there are no cells in neighbours that are empty:
            if (emptyNeighbours(xEnd, yEnd, 1, maze).size() == 0) {
                // let's connect it to the grid
                random = randomIndex.nextInt(neighbours.size());
                // let neighbour = random from neighbours
                List<Position> arr2 = new ArrayList<>(neighbours);
                neighbour = arr2.get(random);
                // maze[neighbour] = empty
                maze[neighbour.getX()][neighbour.getY()] = true;
            }
        }
        // add wall to this inner dungeon;
        for (int i=0; i < size; i++) {
            for (int j=0; j < size; j++) {
                if (!maze[i][j]) {
                    this.dungeon.addEntity(new Wall(UUID.randomUUID().toString(), new Position(i, j)));
                }
            }
        }
        // Build maze
        return this;
    }

    private void directBuilderSteps() {
        this.createDungeon()
        .addPlayer()
        .setGameMode()
        .generateWallsMaze()
        .addExit()
        .setExitGoal();
    }

    public void main(String[] args) {
        generateWallsMaze(); 
    }


    public Dungeon getResult() {
        this.directBuilderSteps();
        return this.dungeon;
    }

}
