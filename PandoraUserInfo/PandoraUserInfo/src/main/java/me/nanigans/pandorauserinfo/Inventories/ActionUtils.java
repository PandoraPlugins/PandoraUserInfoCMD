package me.nanigans.pandorauserinfo.Inventories;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ActionUtils {

    /**
     * Saves the inventory of an offline player based on the items in a 9x4 inventory
     * @param info user info
     * @param saveInv inventory to save as
     * @requires saveInv is a size of 9x4 = 36
     * @throws IOException error
     */
    public static void saveOfflineInventory(UserInfoInventory info, Inventory saveInv) throws IOException {

        File playerFile = new File(info.getPlugin().getServer().getWorldContainer().getAbsolutePath().replace(".", "")
                + info.getStaff().getWorld().getName() + File.separator
                + "playerdata", info.getUser().getUniqueId().toString() + ".dat");

        NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(playerFile));
        NBTTagList newInv = new NBTTagList();

        for (int i = 0; i < saveInv.getContents().length; i++) {
            if(saveInv.getItem(i) == null) continue;

            final NBTTagCompound save = CraftItemStack.asNMSCopy(saveInv.getContents()[i]).save(new NBTTagCompound());

            save.setByte("Slot", (byte)i);
            newInv.add(save.clone());

        }

        nbt.set("Inventory", newInv);

        NBTCompressedStreamTools.a(nbt, new FileOutputStream(playerFile.getAbsolutePath()));

    }

    /**
     * Saves the ender chest of an offline player based on the items in a 9x3 inventory
     * @param info user info
     * @param saveInv the inventory to save as
     * @requires saveInv is a size of 9x3
     * @throws IOException error
     */
    public static void saveOfflineEnderChest(UserInfoInventory info, Inventory saveInv) throws IOException {

        File playerFile = new File(info.getPlugin().getServer().getWorldContainer().getAbsolutePath().replace(".", "")
                + info.getStaff().getWorld().getName() + File.separator
                + "playerdata", info.getUser().getUniqueId().toString() + ".dat");

        NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(playerFile));
        NBTTagList newInv = new NBTTagList();

        for (int i = 0; i < saveInv.getContents().length; i++) {
            if(saveInv.getItem(i) == null) continue;

            final NBTTagCompound save = CraftItemStack.asNMSCopy(saveInv.getContents()[i]).save(new NBTTagCompound());

            save.setByte("Slot", (byte)i);
            newInv.add(save.clone());

        }

        nbt.set("EnderItems", newInv);

        NBTCompressedStreamTools.a(nbt, new FileOutputStream(playerFile.getAbsolutePath()));


    }

    /**
     * Gets sql punish data from litebans plugins
     * @param info user info stuff
     * @param rs the result set of a table from litebans
     * @param method the method to call when this item is clicked
     * @param type the type of punishment  (i.e ban)
     * @return a map consisting of this sql punishment data
     * @throws SQLException an error
     */
    public static Map<String, Object> getSQLPunishData(UserInfoInventory info, ResultSet rs, String method, String type) throws SQLException {

        final String reason = rs.getString("reason");
        final String warnedByUUID = rs.getString("banned_by_uuid");
        final long time = rs.getLong("time");
        final long until = rs.getLong("until");
        final long id = rs.getLong("id");
        final boolean active = rs.getBoolean("active");

        final Map<String, Object> warnData = new HashMap<>();
        warnData.put("reason", reason);
        warnData.put("punishedByUUID", warnedByUUID);
        warnData.put("timePunished", time);
        warnData.put("expires", until);
        warnData.put("id", id);
        warnData.put("active", active);
        warnData.put("punishedUserUUID", info.getUser().getName());
        warnData.put("type", type);
        warnData.put("method", method);

        return warnData;

    }

}
