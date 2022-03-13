package dungeonmania.models;

import dungeonmania.exceptions.*;
import dungeonmania.models.StaticEntities.Collectable;
import dungeonmania.models.StaticEntities.Wieldable;
import dungeonmania.response.models.ItemResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Inventory {

    private List<Entity> inventoryInfo = new ArrayList<>();
    private Map<String, Integer> inventoryCount = new HashMap<>();
    private List<String> buildables = new ArrayList<>();

    /**
     * Set up inventory fields from jsonfile, used when saving or loading game
     * 
     * @param inventoryInfo
     * @param inventoryCount
     * @return
     */
    public Inventory fromJson(List<Entity> inventoryInfo, Map<String, Integer> inventoryCount) {
        // Assumed collectable items to be defined by entity interacting with it
        // setCollectableItems(collectableItems);
        this.inventoryInfo = inventoryInfo;
        this.inventoryCount = inventoryCount;
        return this;
    }

    public void updateBuildables() {
        List<String> updateBuildables = new ArrayList<>();

        if (this.getCount("wood") >= 1 && this.getCount("arrow") >= 3) {
            updateBuildables.add("bow");
        }

        if (this.getCount("wood") >= 2
                && (this.getCount("treasure") >= 1 || this.getCount("sun_stone") >= 1 || this.getCount("key") >= 1)) {
            updateBuildables.add("shield");
        }

        // test crafting with 1 wood/ 2 arrows + treasure/key + sunstone
        if ((this.getCount("wood") >= 1 || this.getCount("arrow") >= 2)
                && (this.getCount("treasure") >= 1 || this.getCount("key") >= 1) && this.getCount("sun_stone") >= 1) {
            updateBuildables.add("sceptre");
        }

        // test crafting with 1 armour + sunstone
        if (this.getCount("wood") >= 2 && (this.getCount("treasure") >= 1 || this.getCount("key") >= 1)) {
            updateBuildables.add("midnight_armour");
        }
        setBuildables(updateBuildables);
    }

    /**
     * Converts Inventory to JSON for saving & loading a game
     * 
     * @return JSON file with details for this Inventory
     */
    public JSONObject toJson() {
        JSONObject jsonInventory = new JSONObject();
        JSONArray jsonInventoryInfo = new JSONArray();
        JSONObject jsonInventoryCount = new JSONObject(inventoryCount);

        for (Entity e : inventoryInfo) {
            jsonInventoryInfo.put(e.getJSON());
        }

        jsonInventory.put("inventoryCount", jsonInventoryCount);
        jsonInventory.put("inventoryInfo", jsonInventoryInfo);
        return jsonInventory;
    }

    /**
     * Get how many items of the string itemType are in Inventory
     * 
     * @param itemType
     * @return # of items
     */
    public int getCount(String itemType) {
        boolean itemTypeExists = this.inventoryCount.containsKey(itemType);
        int count = 0;
        if (itemTypeExists) {
            count = this.inventoryCount.get(itemType);
        }
        return count;
    }

    /**
     * When adding or removing an inventory item, update fields in Inventory
     * accordingly
     * 
     * @param id
     * @param itemType
     * @param add      - boolean true if adding, false if removing
     */
    public void updateCount(String id, String itemType, boolean add) {
        boolean itemTypeExists = this.inventoryCount.containsKey(itemType);
        if (add) {
            if (itemTypeExists) {
                int currCount = this.inventoryCount.get(itemType);
                this.inventoryCount.replace(itemType, currCount + 1);
            } else {
                this.inventoryCount.put(itemType, 1);
            }
        } else {
            int currCount = this.inventoryCount.get(itemType);
            if (currCount == 1) {
                this.inventoryCount.remove(itemType);
                Entity toDelete = this.inventoryInfo.stream().filter(e -> e.getId().equals(id))
                        .collect(Collectors.toList()).get(0);
                this.inventoryInfo.remove(toDelete);
            } else {
                this.inventoryCount.replace(itemType, currCount - 1);
            }
        }
        updateBuildables();
    }

    /**
     * Gets Entities in Inventory matching itemType if exists
     * 
     * @param itemType
     * @return a list of Entity if any contained, null if none contained
     */
    public List<Entity> getIfContains(String itemType) {
        List<Entity> result = inventoryInfo.stream().filter(ent -> ent.getType().equals(itemType))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Gets Wieldable thins in Inventory if any exist
     * 
     * @return list of Wieldables if any, null if none contained
     */
    public List<Wieldable> getWieldables() {
        return inventoryInfo.stream().filter(ent -> ent instanceof Wieldable).map(w -> (Wieldable) w)
                .collect(Collectors.toList());
    }

    /**
     * Gets List of buildable items.
     * 
     * @return List<String> return the buildables
     */
    public List<String> getBuildables() {
        return buildables;
    }

    /**
     * Return for testing purposes only.
     * 
     * @return
     */
    public List<Entity> getInventoryInfo() {
        return this.inventoryInfo;
    }

    /**
     * add item from inventory
     * 
     * @param item
     * @throws InvalidActionException
     */
    public void addToInventory(Entity item) throws InvalidActionException {
        if (item instanceof Collectable) {
            this.inventoryInfo.add(item);
            updateCount(item.getId(), item.getType(), true);
        } else {
            throw new InvalidActionException(String.format("Character/ Player cannot pick up %s", item.getType()));
        }
    }

    /**
     * remove item from inventory
     * 
     * @param item
     * @throws InvalidActionException
     */
    public void useFromInventory(Entity item) throws InvalidActionException {
        boolean checkItemExists = this.inventoryCount.containsKey(item.getType());
        if (checkItemExists && item instanceof Collectable) {
            updateCount(item.getId(), item.getType(), false);
            this.inventoryInfo.remove(item);

        } else {
            throw new InvalidActionException(
                    String.format("Character/ Player cannot remove %s from inventory", item.getType()));
        }
    }

    /**
     * Converts all the inventory into ItemResponses
     * 
     * @return
     */
    public List<ItemResponse> getInventoryResponse() {
        List<ItemResponse> inventoryRespose = new ArrayList<>();
        inventoryInfo.forEach((entity) -> inventoryRespose.add(entity.createItemResponse()));
        return inventoryRespose;
    }

    /**
     * Delete everything within the Inventory
     */
    public void clearInventory() {
        inventoryInfo = new ArrayList<>();
        inventoryCount = new HashMap<>();
    }

    /**
     * @param inventoryInfo the inventoryInfo to set
     */
    public void setInventoryInfo(List<Entity> inventoryInfo) {
        this.inventoryInfo = inventoryInfo;
    }

    /**
     * @return Map<String, Integer> return the inventoryCount
     */
    public Map<String, Integer> getInventoryCount() {
        return inventoryCount;
    }

    /**
     * @param inventoryCount the inventoryCount to set
     */
    public void setInventoryCount(Map<String, Integer> inventoryCount) {
        this.inventoryCount = inventoryCount;
    }

    /**
     * @param buildables the buildables to set
     */
    public void setBuildables(List<String> buildables) {
        this.buildables = buildables;
    }

}
