package us.blockgame.practice.ladder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class Ladder implements ILadder {

    private final String rawName;
    private final LadderType ladderType;
    private final Inventory inventory;
    private final ItemStack[] armor;
    private final ItemStack displayItem;
    private final int id;
    private int editId;
    private final boolean editable;
    private final ArrayList<UUID> unrankedQueue;
    private final ArrayList<UUID> rankedQueue;
    private final ArrayList<UUID> unrankedMatch;
    private final ArrayList<UUID> rankedMatch;

    public Ladder(String rawName, LadderType ladderType, Inventory inventory, ItemStack[] armor, ItemStack displayItem, int id, int editId, boolean editable) {
        this.rawName = rawName;
        this.ladderType = ladderType;
        this.inventory = inventory;
        this.armor = armor;
        this.displayItem = displayItem;
        this.id = id;
        this.editId = editId;
        this.editable = editable;
        unrankedQueue = new ArrayList<>();
        rankedQueue = new ArrayList<>();
        unrankedMatch = new ArrayList<>();
        rankedMatch = new ArrayList<>();
    }

    @Override
    public String getRawName() {
        return rawName;
    }

    @Override
    public LadderType getLadderType() {
        return ladderType;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack[] getArmor() {
        return armor;
    }

    @Override
    public ArrayList<UUID> getUnrankedQueue() {
        return unrankedQueue;
    }

    @Override
    public ArrayList<UUID> getRankedQueue() {
        return rankedQueue;
    }

    @Override
    public ArrayList<UUID> getUnrankedMatch() {
        return unrankedMatch;
    }

    @Override
    public ArrayList<UUID> getRankedMatch() {
        return rankedMatch;
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getEditId() {
        return editId;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditId(int editId) {
        this.editId = editId;
    }
}
