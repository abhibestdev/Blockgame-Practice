package us.blockgame.practice.ladder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public interface ILadder {

    String getRawName();
    LadderType getLadderType();
    Inventory getInventory();
    ItemStack[] getArmor();
    ArrayList<UUID> getUnrankedQueue();
    ArrayList<UUID> getRankedQueue();
    ArrayList<UUID> getUnrankedMatch();
    ArrayList<UUID> getRankedMatch();
    ItemStack getDisplayItem();
    int getId();
    int getEditId();
    boolean isEditable();
    void setEditId(int editId);
}
