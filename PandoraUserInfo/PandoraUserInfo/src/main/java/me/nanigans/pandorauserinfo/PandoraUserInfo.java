package me.nanigans.pandorauserinfo;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.nanigans.pandorauserinfo.Commands.UserInfo;
import me.nanigans.pandorauserinfo.Events.LoginEvents;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PandoraUserInfo extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getConfig().options().copyDefaults(true);
        saveConfig();
        getCommand("userinfo").setExecutor(new UserInfo());
        getServer().getPluginManager().registerEvents(new LoginEvents(), this);

    }

    public static boolean hasPermsTo(Player player, String permission){

        return UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getGroups().stream().flatMap(j -> j.getAdditionalPermissions().stream()).anyMatch(j -> j.getName().equals(permission))
                || UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getGroups().stream().flatMap(j -> j.getPermissions().stream()).anyMatch(j -> j.getName().equals(permission))
                || UltraPermissions.getAPI().getUsers().uuid(player.getUniqueId()).getAllPermissions().stream().anyMatch(j -> j.getName().equals(permission));

    }

    public static boolean hasPerms(Player player, String permission){
        return hasPermsTo(player, permission) || hasPermsTo(player, "UserInfo.*");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
