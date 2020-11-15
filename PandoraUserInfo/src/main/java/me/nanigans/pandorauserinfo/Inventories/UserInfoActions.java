package me.nanigans.pandorauserinfo.Inventories;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import litebans.api.Database;
import me.nanigans.pandorauserinfo.Commands.UserInfo;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActions {

    public static void openPunishmentPage(UserInfoInventory info){
        Inventory inv = InvUtils.createPunishmentPage(info);
        if(inv != null){
            info.setInv(inv);
            info.swapInventories(inv);
        }
    }

    /**
     * Opens a players inventory. If the player is online, it will execute the command invsee
     * if the player is offline it will go into the player data's folder and read it and open it
     * @param info information about the shop
     */
    public static void openInventory(UserInfoInventory info){

        if(info.getUser().isOnline()){
            info.getStaff().performCommand("invsee " + info.getUser().getName());
        }else{

            try {//TODO Come back to this so that people can edit inventories

                File playerFile = new File(info.getPlugin().getServer().getWorldContainer().getAbsolutePath().replace(".", "")
                        + info.getStaff().getWorld().getName() + File.separator
                        + "playerdata", info.getUser().getUniqueId().toString() + ".dat");

                NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(playerFile));

                NBTTagList inventory = (NBTTagList) nbt.get("Inventory");

                Inventory inv = new CraftInventoryCustom(null, 36, "Offline inventory");
                for (int i = 0; i < inventory.size(); i++) {
                    NBTTagCompound compound = inventory.get(i);
                    if (!compound.isEmpty()) {
                        final int slot = Integer.parseInt(compound.get("Slot").toString().replace("b", ""));
                        ItemStack stack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));
                        inv.setItem(slot, stack);

                    }
                }

                info.getStaff().openInventory(inv);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Opens a players ender chest. If the player is online, it will execute the command echest
     * if the player if offline it will go into the player data folder and read it and open it
     * @param info information about the shop
     */

    public static void openEChest(UserInfoInventory info){

        if(info.getUser().isOnline()){
            info.getStaff().performCommand("echest " + info.getUser().getName());
        }else{

            try {//TODO Come back to this so that people can edit inventories

                File playerFile = new File(info.getPlugin().getServer().getWorldContainer().getAbsolutePath().replace(".", "")
                        + info.getStaff().getWorld().getName() + File.separator
                        + "playerdata", info.getUser().getUniqueId().toString() + ".dat");

                NBTTagCompound nbt = NBTCompressedStreamTools.a(new FileInputStream(playerFile));

                NBTTagList inventory = (NBTTagList) nbt.get("EnderItems");

                Inventory inv = new CraftInventoryCustom(null, 27, "Offline Ender Chest");
                for (int i = 0; i < inventory.size(); i++) {
                    NBTTagCompound compound = inventory.get(i);
                    if (!compound.isEmpty()) {
                        final int slot = Integer.parseInt(compound.get("Slot").toString().replace("b", ""));
                        ItemStack stack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(compound));
                        inv.setItem(slot, stack);

                    }
                }

                info.getStaff().openInventory(inv);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * This will promt the staff member to teleport to the faction vault while at the same time giving the xyz location of it
     * @param info information about the shop
     */
    public static void toFacVault(UserInfoInventory info){
        final FPlayer player = FPlayers.getInstance().getByOfflinePlayer(info.getUser());
        final Faction faction = player.getFaction();
        ItemStack item = info.getLastClicked();
        final Location fLoc = faction.getVault();

        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Teleport to " + faction.getTag() + "'s vault");
        if(fLoc != null) {
            itemMeta.setLore(Arrays.asList("X: " + ChatColor.RED + fLoc.getBlockX(), "Y: " + ChatColor.GREEN + fLoc.getBlockY(),
                    "Z: " + ChatColor.BLUE + fLoc.getBlockZ()));
            item.setItemMeta(itemMeta);

            item = ItemUtils.setNBT(item, "CMD", "tp " + fLoc.getX() + " " + fLoc.getY() + " " + fLoc.getZ());
        }
        else{
            itemMeta.setLore(Collections.singletonList(ChatColor.RED+"This faction has no vault"));
            item.setItemMeta(itemMeta);
        }
        info.setLastClicked(item);

        Inventory inv = InvUtils.genConfirmInventory(info, "Teleport to the faction vault?");

        info.setInv(inv);
        info.swapInventories(inv);

    }

    /**
     * Gets warnings from a player and displays it in an inventory where they can click on each warning to see information about it
     * @param info inforamtion about the shop
     */
    public static void getWarnings(UserInfoInventory info){

        new BukkitRunnable() {
            @Override
            public void run() {

                String uuid = info.getUser().getUniqueId().toString();
                String query = "SELECT * FROM {warnings} WHERE uuid=?";
                try (PreparedStatement st = Database.get().prepareStatement(query)) {
                    st.setString(1, uuid);
                    try (ResultSet rS = st.executeQuery()) {
                        rS.last();
                        final int rsSize = rS.getRow();
                        ResultSet rs = st.executeQuery();

                        if(rsSize > 45 && info.getPunishmentPage() != 1){
                            for(int i = (info.getPunishmentPage()-1)*45; i < info.getPunishmentPage()*45; i++){
                                rs.next();
                            }
                        }

                        Inventory inv = Bukkit.createInventory(info.getStaff(), Math.min(Math.min(rsSize - (rsSize % 9), 36) + 18, 54),
                                info.getUser().getName() + "'s Warnings");
                        int i = 1;
                        while (rs.next()) {
                            if(i > 45) break;

                            Map<String, Object> warnData = ActionUtils.getSQLPunishData(info, rs, "getWarnings", "Warns");

                            if(!inv.addItem(InvUtils.createItem(Material.BOOKSHELF, "Warning #"+(i+((info.getPunishmentPage()-1)*45)), "DATA:"+warnData,
                                    "METHOD:openPunishmentPage")).isEmpty())
                                        break;
                            i++;
                        }
                        inv.setItem(inv.getSize()-5, InvUtils.createItem(Material.BARRIER, ChatColor.RED+"Back", "USERINFO:back"));
                        inv.setItem(inv.getSize()-1, InvUtils.createItem(Material.COMPASS, ChatColor.GOLD+"Forward", "METHOD:pageForward,getWarnings"));
                        inv.setItem(inv.getSize()-9, InvUtils.createItem(Material.COMPASS, ChatColor.GOLD+"Backwards", "METHOD:pageBackwards,getWarnings"));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                info.swapInventories(inv);
                                info.setInv(inv);
                            }
                        }.runTask(info.getPlugin());

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(info.getPlugin());

    }


    public void pageBackwards(UserInfoInventory info){
        info.setPunishmentPage((short) (info.getPunishmentPage()-1));
    }
    public void pageForward(UserInfoInventory info){
        info.setPunishmentPage((short) (info.getPunishmentPage()+1));
    }

}
