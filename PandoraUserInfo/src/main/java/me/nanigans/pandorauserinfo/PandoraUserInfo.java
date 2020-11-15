package me.nanigans.pandorauserinfo;

import me.nanigans.pandorauserinfo.Commands.UserInfo;
import me.nanigans.pandorauserinfo.Events.LoginEvents;
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
