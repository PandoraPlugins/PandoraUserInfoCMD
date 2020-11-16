package me.nanigans.pandorauserinfo.Inventories;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.util.LazyLocation;
import litebans.api.Database;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        getPunishments(info, "warnings", "getWarnings", "Warns", Material.BOOKSHELF);

    }
    /**
     * Gets mutes from a player and displays it in an inventory where they can click on each warning to see information about it
     * @param info inforamtion about the shop
     */
    public static void getMutes(UserInfoInventory info){
        getPunishments(info, "mutes", "getMutes", "Mutes", Material.BOOK);
    }
    /**
     * Gets bans from a player and displays it in an inventory where they can click on each warning to see information about it
     * @param info inforamtion about the shop
     */
    public static void getBans(UserInfoInventory info){
        getPunishments(info, "bans", "getBans", "Bans", Material.BOOK);
    }
    /**
     * Gets kicks from a player and displays it in an inventory where they can click on each warning to see information about it
     * @param info inforamtion about the shop
     */
    public static void getKicks(UserInfoInventory info){
        getPunishments(info, "kicks", "getKicks", "Kicks", Material.BOOKSHELF);

    }

    /**
     * Gets the punishment information specified by the type of warning.
     * @param info inforamiton about the user
     * @param sqlTable the table to get punishments from sql
     * @param method the method this was called from
     * @param type the type of punishment
     * @param item the material to display the punishment as
     */
    public static void getPunishments(UserInfoInventory info, String sqlTable, String method, String type, Material item){

        new BukkitRunnable() {
            @Override
            public void run() {

                String uuid = info.getUser().getUniqueId().toString();
                String query = "SELECT * FROM {"+sqlTable+"} WHERE uuid=?";
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
                                info.getUser().getName() + "'s "+type);
                        int i = 1;
                        while (rs.next()) {
                            if(i > 45 && inv.getItem(44) != null) break;

                            Map<String, Object> muteData = ActionUtils.getSQLPunishData(info, rs, method, type);

                            boolean isActive = info.getFilterBy().equals(Filter.ACTIVE);
                            boolean isActiveData = Boolean.parseBoolean(muteData.get("active").toString());
                            if(isActive && !isActiveData) continue;
                            if(muteData.size() > 2) {

                                ItemStack rHead = InvUtils.createItem(item, muteData.get("type").toString().replace("s", "") + " #" + (i + ((info.getPunishmentPage() - 1) * 45)),
                                        "DATA:" + muteData.toString(), "METHOD:openPunishmentPage");
                                if(!inv.addItem(rHead).isEmpty())
                                    break;
                            }

                            i++;
                        }
                        inv.setItem(inv.getSize()-4, InvUtils.createItem(Material.PAPER, ChatColor.GOLD+"Filter By Active: " + info.getFilterBy().equals(Filter.ACTIVE), "METHOD:setFilterActive,"+method));
                        inv.setItem(inv.getSize()-6, InvUtils.createItem(Material.BARRIER, ChatColor.RED+"Back", "USERINFO:back"));
                        inv.setItem(inv.getSize()-1, InvUtils.createItem(Material.COMPASS, ChatColor.GOLD+"Forward", "METHOD:pageForward,"+method));
                        inv.setItem(inv.getSize()-9, InvUtils.createItem(Material.COMPASS, ChatColor.GOLD+"Backwards", "METHOD:pageBackwards,"+method));

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

    /**
     * Gets the warps to the users faction
     * @param info info about the user
     */
    public static void getFacWarps(UserInfoInventory info){

        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(info.getUser());
        Faction f = fPlayer.getFaction();
        if(f != null && !f.isWilderness()){

            final ConcurrentHashMap<String, LazyLocation> warps = f.getWarps();
            Inventory inv = Bukkit.createInventory(info.getStaff(), Math.min(Math.min(warps.size() - (warps.size() % 9), 36) + 18, 54),
                    f.getTag() + "'s Warps");
            warps.forEach((i, j) -> {
                ItemStack warp = InvUtils.createItem(Material.EYE_OF_ENDER, i, "CMD:tp "+j.getX()+" "+j.getY()+" "+j.getZ());
                ItemMeta meta = warp.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.GRAY+"Click to TP to", ChatColor.GRAY+"X: " + ChatColor.RED+ ((int) j.getX()), ChatColor.GRAY+"Y: "+ChatColor.GREEN+((int) j.getY()),
                        ChatColor.GRAY+"Z: " + ChatColor.BLUE+((int)j.getZ())));
                warp.setItemMeta(meta);

                inv.addItem(warp);
            });
            inv.setItem(inv.getSize()-5, InvUtils.createItem(Material.BARRIER, ChatColor.RED+"Back", "USERINFO:back"));
            info.swapInventories(inv);
            info.setInv(inv);

        }else{
            info.getStaff().closeInventory();
            info.getStaff().sendMessage(ChatColor.RED+"This user is not in a faction");
        }

    }

    /**
     * Gets the player in questions faction home
     * @param info the info about the user
     */
    public static void getFacHomes(UserInfoInventory info){

        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(info.getUser());
        Faction f = fPlayer.getFaction();
        if(f != null && !f.isWilderness()){

            final Location home = f.getHome();
            if(home != null) {
                Inventory inv = Bukkit.createInventory(info.getStaff(), 9, f.getTag() + "'s Home");

                ItemStack warp = InvUtils.createItem(Material.BED, "Faction Home", "CMD:tp " + home.getX() + " " + home.getY() + " " + home.getZ());
                ItemMeta meta = warp.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.GRAY+"Click to TP to",ChatColor.GRAY + "X: " + ChatColor.RED + ((int) home.getX()), ChatColor.GRAY + "Y: " + ChatColor.GREEN + ((int) home.getY()),
                        ChatColor.GRAY + "Z: " + ChatColor.BLUE + ((int) home.getZ())));
                warp.setItemMeta(meta);

                inv.setItem(4, warp);
                inv.setItem(0, InvUtils.createItem(Material.BARRIER, ChatColor.RED + "Back", "USERINFO:back"));
                info.swapInventories(inv);
                info.setInv(inv);
                for (int i = 0; i < inv.getContents().length; i++) {
                    if(inv.getContents()[i] == null)
                        inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 0));
                }

            }else{
                info.getStaff().closeInventory();
                info.getStaff().sendMessage(ChatColor.RED+"This faction does not have a home");
            }
        }else{
            info.getStaff().closeInventory();
            info.getStaff().sendMessage(ChatColor.RED+"This user is not in a faction");
        }

    }

    public void setFilterActive(UserInfoInventory info){
        if(info.getFilterBy() == Filter.ACTIVE)
            info.setFilterBy(Filter.NONE);
        else info.setFilterBy(Filter.ACTIVE);
    }
    public void pageBackwards(UserInfoInventory info){
        info.setPunishmentPage((short) (info.getPunishmentPage()-1));
    }
    public void pageForward(UserInfoInventory info){
        info.setPunishmentPage((short) (info.getPunishmentPage()+1));
    }

    public enum Filter{
        ACTIVE,
        NONE
    }

}
