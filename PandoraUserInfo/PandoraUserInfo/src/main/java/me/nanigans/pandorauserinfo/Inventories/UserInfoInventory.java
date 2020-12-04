package me.nanigans.pandorauserinfo.Inventories;

import me.nanigans.pandorauserinfo.PandoraUserInfo;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class UserInfoInventory implements Listener {
    private final PandoraUserInfo plugin = PandoraUserInfo.getPlugin(PandoraUserInfo.class);
    private final Player staff;
    private final OfflinePlayer user;
    private Inventory inv;
    private boolean isSwapping = false;
    private ItemStack lastClicked;
    private short punishmentPage = 1;
    private UserInfoActions.Filter filterBy = UserInfoActions.Filter.NONE;
    private boolean inOtherInventory = false;
    private boolean inOtherEChest = false;


    public UserInfoInventory(Player looking, OfflinePlayer whosInfo){

        this.staff = looking;
        this.user = whosInfo;
        Inventory inv = InvUtils.genInfoPage(this);
        if(inv != null) {
            looking.openInventory(inv);
            this.inv = inv;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }else{
            looking.sendMessage(ChatColor.RED+"Something went wrong when generating player information");
        }

    }

    /**
     * Checks when a player clicks an item in this inventory
     * @param event invclick event
     * @throws NoSuchMethodException error
     * @throws InvocationTargetException error
     * @throws IllegalAccessException error
     */
    @EventHandler
    public void onInvClick(InventoryClickEvent event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if(event.getInventory() != null && event.getClickedInventory() != null) {
            if (event.getInventory().equals(this.inv)) {
                if(!this.inOtherInventory && !this.inOtherEChest)
                event.setCancelled(true);

                ((Player) event.getWhoClicked()).playSound(this.staff.getLocation(), Sound.valueOf("CLICK"), 2f, 1f);
                if(event.getCurrentItem() != null){
                    ItemStack item = event.getCurrentItem();
                    this.lastClicked = item;

                    if(ItemUtils.containsNBT(item, "CMD")){

                        this.staff.performCommand(ItemUtils.getNBT(item, "CMD").replace("{player}", this.user.getName()));
                        this.staff.closeInventory();

                    }else if(ItemUtils.containsNBT(item, "METHOD")){
                        String[] nbt = ItemUtils.getNBT(item, "METHOD").split(",");
                        for (String s : nbt) {
                            UserInfoActions.class.getMethod(s, this.getClass()).invoke(new UserInfoActions(), this);
                        }
                    }else if(ItemUtils.containsNBT(item, "USERINFO")){
                        Inventory inv = InvUtils.genInfoPage(this);
                        this.inv = inv;
                        this.swapInventories(inv);
                    }

                }

            }
        }

    }

    /**
     * When player closes inventory, it'll cancel this event handler.
     * It'll check if player is editing offline inventory and save it when staff closes it
     * @param event inventoryclose event
     * @throws Throwable error
     */

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) throws Throwable {
        if(event.getInventory().equals(this.inv)){

            this.punishmentPage = 1;
            if(!this.isSwapping) {
                HandlerList.unregisterAll(this);
                this.finalize();
            }

            if(this.inOtherInventory){

                ActionUtils.saveOfflineInventory(this, event.getInventory());

            }
            if(this.inOtherEChest){
                ActionUtils.saveOfflineEnderChest(this, event.getInventory());
            }
        }
    }


    public void swapInventories(Inventory toInv){
        isSwapping = true;
        staff.openInventory(toInv);
        isSwapping = false;
    }




    public void setInOtherInventory(boolean inOtherInventory) {
        this.inOtherInventory = inOtherInventory;
    }

    public void setInOtherEChest(boolean inOtherEChest) {
        this.inOtherEChest = inOtherEChest;
    }

    public UserInfoActions.Filter getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(UserInfoActions.Filter filterBy) {
        this.filterBy = filterBy;
    }
    public short getPunishmentPage() {
        return punishmentPage;
    }

    public void setPunishmentPage(short punishmentPage) {
        this.punishmentPage = (short) Math.max(punishmentPage, 1);
    }
    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public Player getStaff() {
        return staff;
    }

    public OfflinePlayer getUser() {
        return user;
    }
    public PandoraUserInfo getPlugin(){
        return this.plugin;
    }


    public ItemStack getLastClicked() {
        return lastClicked;
    }

    public void setLastClicked(ItemStack lastClicked) {
        this.lastClicked = lastClicked;
    }
}
