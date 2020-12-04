package me.nanigans.pandorauserinfo.Commands;

import me.nanigans.pandorauserinfo.Inventories.UserInfoInventory;
import me.nanigans.pandorauserinfo.PandoraUserInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserInfo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("userinfo")){

            if(sender instanceof Player){

                Player player = ((Player) sender);
                if(PandoraUserInfo.hasPermsTo(player, "UserInfo.PerformCommand")) {

                    if (args.length > 0) {

                        OfflinePlayer f = Bukkit.getOfflinePlayer(args[0]);

                        if (f.hasPlayedBefore() || f.isOnline()) {

                            new UserInfoInventory(player, f);

                        } else {

                            player.sendMessage(ChatColor.RED + "Cannot find this player. This player has either never joined this server ("
                                    + player.getWorld().getName() + ") or does not exist");
                        }
                        return true;

                    }
                }else{
                    player.sendMessage(ChatColor.RED+"Invalid Permissions");
                    return true;
                }
            }else{
                sender.sendMessage(ChatColor.RED+"Only players may use this command!");
                return true;
            }

        }

        return false;
    }
}
