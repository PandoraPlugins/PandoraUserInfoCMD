package me.nanigans.pandorauserinfo.Inventories;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.nanigans.pandorauserinfo.PandoraUserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InvUtils {
    private static final PandoraUserInfo plugin = PandoraUserInfo.getPlugin(PandoraUserInfo.class);

    //    private final static int[] itemLocations = {13, 14, 15, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 39, 40, 41};
//    private final static ItemStack[] itemss = {createItem(Material.ENDER_PEARL, "Last Logout"), };
    private final static Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>(){{
        put(12, createItem(Material.ENDER_PEARL, "Last Logout"));
        put(13, createItem(Material.FIREBALL, "Dupe IP", "CMD:dupeip {player}"));
        put(14, createItem(Material.ENDER_PEARL, "Last Login"));
        put(20, createItem(Material.CHEST, "Inventory", "METHOD:openInventory"));
        put(21, createItem(Material.ENDER_CHEST, "Ender Chest", "METHOD:openEChest"));
        put(22, createItem(Material.PAPER, "Balance", "CMD:bal {player}"));
        put(23, createItem(Material.ENCHANTMENT_TABLE, "Faction Vault", "METHOD:toFacVault"));
        put(24, createItem(Material.CHEST, "Player Vault", "METHOD:toPlayerVault"));
        put(29, createItem(Material.BOOKSHELF, "Warnings", "METHOD:getWarnings"));
        put(30, createItem(Material.BOOK, "Mutes"));
        put(31, createItem(Material.PAPER, "Reports"));
        put(32, createItem(Material.BOOK, "Bans"));
        put(33, createItem(Material.BOOKSHELF, "Active Punishments"));
        put(39, createItem(Material.BED, "Homes"));
        put(40, createItem(Material.EYE_OF_ENDER, "Faction Warps"));
        put(41, createItem(Material.BED, "Factions Home"));
    }};

    /**
     * Creates the punishment page for moderation information.
     * Creates: Player skull, Punishment ID, Weather it's active or not, the reason of the punishment, the time given and when it'll expire,
     * and who punished them
     * @param info the information about the user
     * @return the created inventory
     */
    public static Inventory createPunishmentPage(UserInfoInventory info){

        ItemStack itemInfo = info.getLastClicked();
        if(ItemUtils.containsNBT(itemInfo,"DATA")) {
            String punishData = ItemUtils.getNBT(itemInfo, "DATA");
            if(punishData != null) {

                Map<String, Object> data = Arrays.stream(punishData.split(", "))
                        .map(s -> s.replaceFirst("\\{", "").replace("}", "").split("="))
                        .collect(Collectors.toMap(
                                a -> a[0],  //key
                                a -> a[1]   //value
                        ));

                Inventory inv = Bukkit.createInventory(info.getStaff(), 54, info.getUser().getName() + " " + data.get("type"));
                ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                SkullMeta headMeta = ((SkullMeta) head.getItemMeta());
                headMeta.setOwner(info.getUser().getName());
                headMeta.setDisplayName(info.getUser().getName());
                headMeta.setLore(ItemUtils.wordWrapLore(info.getUser().getUniqueId().toString(), "-", ChatColor.GRAY));
                head.setItemMeta(headMeta);
                inv.setItem(22, head);

                ItemStack ID = new ItemStack(Material.DOUBLE_PLANT);
                ItemMeta meta = ID.getItemMeta();
                meta.setDisplayName("Warn ID: " + data.get("id"));
                ID.setItemMeta(meta);
                inv.setItem(0, ID);

                ItemStack isActive = new ItemStack(Material.STAINED_GLASS_PANE, 1,
                        (short) (Boolean.parseBoolean(data.get("active").toString()) ? 13 : 14));
                if(Boolean.parseBoolean(data.get("active").toString()))
                    meta.setDisplayName(ChatColor.GREEN+"Active");
                else meta.setDisplayName(ChatColor.RED+"Inactive");
                isActive.setItemMeta(meta);
                inv.setItem(4, isActive);

                ItemStack reason = new ItemStack(Material.PAPER);
                meta.setDisplayName("Reason");
                meta.setLore(ItemUtils.wordWrapLore(data.get("reason").toString(), " ", ChatColor.GOLD));
                reason.setItemMeta(meta);
                inv.setItem(20, reason);

                head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                headMeta = ((SkullMeta) head.getItemMeta());
                headMeta.setOwner(data.get("punishedByUUID").toString());
                headMeta.setDisplayName("Reported By: " + ChatColor.GOLD +
                        Bukkit.getOfflinePlayer(UUID.fromString(data.get("punishedByUUID").toString())).getName());
                headMeta.setLore(ItemUtils.wordWrapLore(data.get("punishedByUUID").toString(), "-", ChatColor.GRAY));

                head.setItemMeta(headMeta);
                inv.setItem(40, head);

                ItemStack time = new ItemStack(Material.WATCH, 1);
                meta.setDisplayName("Time Given");
                String[] times = ItemUtils.getDateAndTime(Long.parseLong(data.get("timePunished").toString()));
                meta.setLore(Arrays.asList("Date: " + times[0], "Time: " + times[1]));

                time.setItemMeta(meta);
                inv.setItem(24, time);

                ItemStack expir = new ItemStack(Material.WATCH, 1);
                meta.setDisplayName("Expires");

                times = ItemUtils.getDateAndTime(Long.parseLong(data.get("expires").toString()));
                meta.setLore(Arrays.asList("Date: " + times[0], "Time: " + times[1]));

                expir.setItemMeta(meta);
                inv.setItem(33, expir);

                ItemStack back = createItem(Material.BARRIER, ChatColor.RED+"Back", "METHOD:"+data.get("method"));
                inv.setItem(45, back);

                return inv;
            }
        }

        return null;
    }


    public static Inventory genInfoPage(UserInfoInventory info){

        Inventory inv = Bukkit.createInventory(info.getStaff(), 54, info.getUser().getName()+" user information");
        items.forEach(inv::setItem);

        ItemStack whiteGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        for (int i = 0; i < inv.getContents().length; i++) {
            if(inv.getItem(i) == null) {
                inv.setItem(i, whiteGlass);
            }else {

                switch (i) {
                    case 12:
                        long time = plugin.getConfig().getLong("lastLeave."+info.getUser().getUniqueId());
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(time);
                        String date = c.get(Calendar.DAY_OF_WEEK) + " / " + c.get(Calendar.MONTH) + " / " + c.get(Calendar.YEAR);
                        String timer = (c.get(Calendar.HOUR_OF_DAY) > 10 ? c.get(Calendar.HOUR_OF_DAY) : "0"+c.get(Calendar.HOUR_OF_DAY))
                                + ":" + (c.get(Calendar.MINUTE) > 10 ? c.get(Calendar.MINUTE) : "0"+c.get(Calendar.MINUTE)) + ":"
                                + (c.get(Calendar.SECOND) > 10 ? c.get(Calendar.SECOND) : "0"+c.get(Calendar.SECOND));
                        ItemStack item = inv.getItem(i);
                        inv.setItem(i, setLore(Arrays.asList("Date: " + date, "Time: " + timer), item));
                        break;
                    case 14:
                        final long lastPlayed = info.getUser().getLastPlayed();
                        c = Calendar.getInstance();
                        c.setTimeInMillis(lastPlayed);
                         date = c.get(Calendar.DAY_OF_WEEK) + " / " + c.get(Calendar.MONTH) + " / " + c.get(Calendar.YEAR);
                         timer = (c.get(Calendar.HOUR_OF_DAY) > 10 ? c.get(Calendar.HOUR_OF_DAY) : "0"+c.get(Calendar.HOUR_OF_DAY))
                                 + ":" + (c.get(Calendar.MINUTE) > 10 ? c.get(Calendar.MINUTE) : "0"+c.get(Calendar.MINUTE)) + ":"
                                 + (c.get(Calendar.SECOND) > 10 ? c.get(Calendar.SECOND) : "0"+c.get(Calendar.SECOND));
                         item = inv.getItem(i);

                        inv.setItem(i, setLore(Arrays.asList("Date: " + date, "Time: " + timer), item));
                        break;
                    case 22:
                        User user = Essentials.getPlugin(Essentials.class).getUser(info.getUser().getUniqueId());
                        final BigDecimal balance = user.getMoney();
                        item = inv.getItem(i);
                        inv.setItem(i, setLore(Collections.singletonList("Balance: " + balance), item));

                }

            }
        }

        return inv;

    }


    public static Inventory genConfirmInventory(UserInfoInventory info, String invName){

        Inventory inv = Bukkit.createInventory(info.getStaff(), 9, invName);
        inv.setItem(4, info.getLastClicked());
        inv.setItem(0, createItem(Material.BARRIER, ChatColor.RED+"Back", "USERINFO:back"));
        for (int i = 0; i < inv.getContents().length; i++) {
            if(inv.getItem(i) == null)
                inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0));
        }

        return inv;
    }

    public static ItemStack setLore(List<String> lore, ItemStack item){
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;

    }

    public static ItemStack createItem(Material mat, String name){

        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material mat, String name, String... nbt){

        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        for (String s : nbt) {
            String[] pair = s.split(":");
            item = ItemUtils.setNBT(item, pair[0], pair[1]);
        }

        return item;
    }

}
