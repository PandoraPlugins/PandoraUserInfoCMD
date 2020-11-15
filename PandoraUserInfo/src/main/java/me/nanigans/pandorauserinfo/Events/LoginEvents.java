package me.nanigans.pandorauserinfo.Events;

import me.nanigans.pandorauserinfo.PandoraUserInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class LoginEvents implements Listener {
    private PandoraUserInfo plugin = PandoraUserInfo.getPlugin(PandoraUserInfo.class);

    @EventHandler
    public void onLeave(PlayerQuitEvent event){

        plugin.getConfig().set("lastLeave."+event.getPlayer().getUniqueId(), new Date().getTime());
        plugin.saveConfig();

    }

}
