package me.nanigans.pandorauserinfo.Inventories;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ItemUtils {

    public static String[] getDateAndTime(long time){

        Date date = new Date(time);
        final LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        final String dateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH).format(localDate);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        String timeS = (c.get(Calendar.HOUR_OF_DAY) > 10 ? c.get(Calendar.HOUR_OF_DAY) : "0"+c.get(Calendar.HOUR_OF_DAY))
                + ":" + (c.get(Calendar.MINUTE) > 10 ? c.get(Calendar.MINUTE) : "0"+c.get(Calendar.MINUTE)) + ":"
                + (c.get(Calendar.SECOND) > 10 ? c.get(Calendar.SECOND) : "0"+c.get(Calendar.SECOND));

        return new String[]{dateFormat, timeS};

    }


    public static List<String> wordWrapLore(String string, String splitter, ChatColor color) {
        StringBuilder sb = new StringBuilder(color+string);

        int i = 0;
        while (i + 35 < sb.length() && (i = sb.lastIndexOf(splitter, i + 35)) != -1) {
            sb.replace(i, i + 1, "\n"+color);
        }
        return Arrays.asList(sb.toString().split("\n"));

    }

        public static ItemStack setNBT(ItemStack item, String key, String value){

            net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);

            NBTTagCompound tag = stack.getTag();

            if (tag != null) {

                tag.setString(key, value);
                stack.setTag(tag);

            }

            item = CraftItemStack.asCraftMirror(stack);
            return item;

        }

        public static boolean containsNBT(ItemStack item, String key){

            try {
                net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);

                if (stack.hasTag()) {
                    NBTTagCompound tag = stack.getTag();

                    if (tag != null) {

                        return tag.hasKey(key);

                    } else return false;
                } else return false;

            }catch(Exception ignored){
                return false;
            }

        }

        public static String getNBT(ItemStack item, String key){

            if(containsNBT(item, key)){

                net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
                NBTTagCompound tag = stack.getTag();

                return tag.get(key).toString().replaceAll("\"", "");

            }

            return null;
        }


    }
